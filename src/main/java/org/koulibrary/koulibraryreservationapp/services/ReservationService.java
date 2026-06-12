package org.koulibrary.koulibraryreservationapp.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.koulibrary.koulibraryreservationapp.domains.*;
import org.koulibrary.koulibraryreservationapp.dtos.requests.CancelReservationRequest;
import org.koulibrary.koulibraryreservationapp.dtos.requests.CheckInRequest;
import org.koulibrary.koulibraryreservationapp.dtos.requests.CreateReservationRequest;
import org.koulibrary.koulibraryreservationapp.dtos.responses.MyReservationResponse;
import org.koulibrary.koulibraryreservationapp.dtos.responses.PageResponse;
import org.koulibrary.koulibraryreservationapp.dtos.responses.ReservationResponse;
import org.koulibrary.koulibraryreservationapp.entities.*;
import org.koulibrary.koulibraryreservationapp.exceptions.*;
import org.koulibrary.koulibraryreservationapp.repositories.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.koulibrary.koulibraryreservationapp.configs.TimeConfig.APP_ZONE;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SaloonTimeSlotRepository saloonTimeSlotRepository;
    private final DeskRepository deskRepository;
    private final PenaltyRepository penaltyRepository;
    private final UserRepository userRepository;
    private final ReservationStatusLogRepository reservationStatusLogRepository;

    @Transactional
    public ReservationResponse create(String keycloakSub, CreateReservationRequest req) {

        LocalDateTime now = LocalDateTime.now(APP_ZONE);
        LocalDate today = now.toLocalDate();

        // 1) Fetch user WITH PESSIMISTIC LOCK (Maintains active reservation limit invariants)
        User user = userRepository.findByKeycloakIdForUpdate(keycloakSub)
                .orElseThrow(() -> new UserNotFoundException("User not found for Keycloak ID: " + keycloakSub));

        if (user.getUserStatus() == UserStatus.BLOCKED) {
            throw new UserBlockedException("Your account is blocked, you cannot create a reservation");
        }

        if (penaltyRepository.existsActivePenalty(user.getId(), PenaltyStatus.ACTIVE, now)) {
            throw new UserBlockedException("You have an active penalty. You cannot reserve until your penalty period expires");
        }

        // 2) Slot validation
        SaloonTimeSlot slot = saloonTimeSlotRepository.findById(req.getSlotId())
                .orElseThrow(() -> new SlotNotFoundException("Slot not found with ID: " + req.getSlotId()));

        if (Boolean.FALSE.equals(slot.getIsAvailable())) {
            throw new SlotNotAvailableException("The selected time slot is closed for reservations");
        }

        // 3) Reservation window validation
        Library library = slot.getSaloon().getLibrary();
        LocalDate slotDate = slot.getDate();
        LocalDateTime slotStart = LocalDateTime.of(slotDate, slot.getStartTime());
        LocalDateTime slotEnd   = LocalDateTime.of(slotDate, slot.getEndTime());

        // Slot bitmişse reddet (devam eden slot dahil rezerve edilebilir)
        if (!slotEnd.isAfter(now)) {
            throw new ReservationWindowException("This time slot has already ended");
        }

        if (slotDate.isAfter(today.plusDays(library.getReservationWindowInDays()))) {
            throw new ReservationWindowException(
                    "You can only reserve for the next " + library.getReservationWindowInDays() + " days");
        }

        // 4) Active reservation limit check (Thread-safe due to pessimistic lock)
        Long activeCount = reservationRepository.countByUserIdAndStatusIn(user.getId(), ReservationStatus.OCCUPYING);
        if (activeCount >= library.getMaxActiveReservationsPerUser()) {
            throw new MaxActiveReservationsReachedException(
                    "You can have a maximum of " + library.getMaxActiveReservationsPerUser() + " active reservations");
        }

        // one person only gets one reservation per slot
        if (reservationRepository.existsByUserIdAndSlotIdAndStatusIn(
                user.getId(), slot.getId(), ReservationStatus.OCCUPYING)) {
            throw new AlreadyReservedInSlotException("Already Reserved In This slot before");
        }

        // 5) Desk validation
        Desk desk = deskRepository.findById(req.getDeskId())
                .orElseThrow(() -> new DeskNotFoundException("Desk not found with ID: " + req.getDeskId()));

        if (!desk.getSaloon().getId().equals(slot.getSaloon().getId())) {
            throw new DeskAndSlotSaloonMismatchException("The desk and the time slot do not belong to the same saloon");
        }

        if (desk.getStatus() == DeskStatus.OUT_OF_SERVICE) {
            throw new DeskOutOfServiceException("The selected desk is out of service");
        }

        // 6) Optimistic availability check (Fail-fast strategy for high concurrency)
        if (reservationRepository.existsByDeskIdAndSlotIdAndStatusIn(
                desk.getId(), slot.getId(), ReservationStatus.OCCUPYING)) {
            throw new DeskAlreadyReservedException("This desk is already reserved for the selected time slot");
        }

        // 7) Insert & Transaction Guarantee (Backed by partial unique index)
        Reservation reservation = Reservation.builder()
                .user(user)
                .desk(desk)
                .slot(slot)
                .startTime(slotStart)
                .endTime(slotEnd)
                .reservationTime(now)
                .status(ReservationStatus.PENDING)
                .build();

        try {
            // flush is required to force the DB constraint check before the commit phase
            reservation = reservationRepository.saveAndFlush(reservation);
            logStatusChange(reservation, null, ReservationStatus.PENDING, user, null, "Reservation created");
        } catch (DataIntegrityViolationException e) {
            throw new DeskAlreadyReservedException("This desk was just reserved by another user");
        }

        return toResponse(reservation);
    }

    private ReservationResponse toResponse(Reservation r) {
        return ReservationResponse.builder()
                .id(r.getId())
                .deskId(r.getDesk().getId())
                .deskNumber(r.getDesk().getDeskNumber())
                .slotId(r.getSlot().getId())
                .startTime(r.getStartTime())
                .endTime(r.getEndTime())
                .status(r.getStatus())
                .reservationTime(r.getReservationTime())
                .build();
    }

    @Transactional(readOnly = true)
    public PageResponse<MyReservationResponse> getMyReservations(String keycloakSub, Pageable pageable) {

        User user = userRepository.findByKeycloakId(keycloakSub)
                .orElseThrow(() -> new UserNotFoundException("User not found for Keycloak ID: " + keycloakSub));

        Page<Reservation> page = reservationRepository.findByUserIdWithDetails(user.getId(), pageable);

        List<MyReservationResponse> content = page.getContent().stream()
                .map(this::toMyResponse)
                .toList();

        return PageResponse.<MyReservationResponse>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isLast(page.isLast())
                .build();
    }

    private MyReservationResponse toMyResponse(Reservation reservation) {
        Saloon saloon = reservation.getSlot().getSaloon();
        return MyReservationResponse.builder()
                .id(reservation.getId())
                .deskId(reservation.getDesk().getId())
                .deskNumber(reservation.getDesk().getDeskNumber())
                .saloonName(saloon.getName())
                .libraryName(saloon.getLibrary().getName())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .status(reservation.getStatus())
                .reservationTime(reservation.getReservationTime())
                .checkInTime(reservation.getCheckInTime())
                .build();
    }

    @Transactional
    public MyReservationResponse cancel(String keycloakSub, Long reservationId, CancelReservationRequest req) {

        User user = userRepository.findByKeycloakId(keycloakSub)
                .orElseThrow(() -> new UserNotFoundException("User not found for Keycloak ID: " + keycloakSub));

        Reservation reservation = reservationRepository.findByIdWithDetails(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with ID: " + reservationId));

        // Ownership: a user can only cancel their own reservation
        if (!reservation.getUser().getId().equals(user.getId())) {
            throw new ReservationDoesNotBelongToUserException("This reservation does not belong to you");
        }

        // Only PENDING can be cancelled (ACTIVE ends via /complete)
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new ReservationNotCancellableException(
                    "Only pending reservations can be cancelled. Current status: " + reservation.getStatus());
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        if (req.getReason() != null && !req.getReason().isBlank()) {
            reservation.setCancellationReason(req.getReason());
        }
        logStatusChange(reservation, ReservationStatus.PENDING, ReservationStatus.CANCELLED,
                user, StatusChangeReason.USER_CANCELLED, req.getReason());

        return toMyResponse(reservation);
    }

    @Transactional
    public MyReservationResponse checkIn(String keycloakSub, CheckInRequest req) {

        User user = userRepository.findByKeycloakId(keycloakSub)
                .orElseThrow(() -> new UserNotFoundException("User not found for Keycloak ID: " + keycloakSub));

        Desk desk = deskRepository.findByQrCodeCode(req.getDeskQrToken())
                .orElseThrow(() -> new InvalidQrCodeException("Invalid QR code"));

        if (desk.getQrCode().getStatus() != QRCodeStatus.ACTIVE) {
            throw new InvalidQrCodeException("This QR code is no longer active");
        }

        LocalDateTime now = LocalDateTime.now(APP_ZONE);

        Reservation reservation = reservationRepository
                .findPendingForCheckIn(user.getId(), desk.getId(), ReservationStatus.PENDING, now)
                .stream()
                .findFirst()
                .orElseThrow(() -> new CheckInNotAvailableException(
                        "You do not have a pending reservation available for check-in at this desk right now"));

        Library library = reservation.getSlot().getSaloon().getLibrary();

        // Walk-in (slot başladıktan sonra rezerve eden) için check-in süresini
        // rezerve ettiği andan başlat: deadline = max(startTime, reservationTime) + timeout
        LocalDateTime windowBase = reservation.getReservationTime().isAfter(reservation.getStartTime())
                ? reservation.getReservationTime()
                : reservation.getStartTime();
        LocalDateTime deadline = windowBase.plusMinutes(library.getCheckInTimeoutMinutes());

        if (now.isBefore(reservation.getStartTime())) {
            throw new CheckInNotAvailableException("The reservation time window has not started yet");
        }
        if (now.isAfter(deadline)) {
            throw new CheckInWindowExpiredException("Your check-in time frame has expired");
        }

        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setCheckInTime(now);
        logStatusChange(reservation, ReservationStatus.PENDING, ReservationStatus.ACTIVE, user, null, "checked in");

        return toMyResponse(reservation);
    }

    @Transactional
    public MyReservationResponse complete(String keycloakSub, Long reservationId) {

        User user = userRepository.findByKeycloakId(keycloakSub)
                .orElseThrow(() -> new UserNotFoundException("User not found for Keycloak ID: " + keycloakSub));

        Reservation reservation = reservationRepository.findByIdWithDetails(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with ID: " + reservationId));

        if (!reservation.getUser().getId().equals(user.getId())) {
            throw new ReservationDoesNotBelongToUserException("This reservation does not belong to you");
        }

        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new ReservationNotCompletableException(
                    "Only active (checked-in) reservations can be completed. Current status: " + reservation.getStatus());
        }

        reservation.setStatus(ReservationStatus.COMPLETED);
        logStatusChange(reservation, ReservationStatus.ACTIVE, ReservationStatus.COMPLETED, user, null, null);

        return toMyResponse(reservation);
    }

    private void logStatusChange(Reservation r, ReservationStatus from, ReservationStatus to,
                                 User changedBy, StatusChangeReason reason, String note) {
        reservationStatusLogRepository.save(ReservationStatusLog.builder()
                .reservation(r).changedBy(changedBy)
                .fromStatus(from).toStatus(to)
                .reason(reason).note(note).build());
    }
}