package org.koulibrary.koulibraryreservationapp.services;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
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

        // 1) Fetch user WITH PESSIMISTIC LOCK (Maintains active reservation limit invariants)
        User user = userRepository.findByKeycloakIdForUpdate(keycloakSub)
                .orElseThrow(() -> new UserNotFoundException("User not found for Keycloak ID: " + keycloakSub));

        if (user.getUserStatus() == UserStatus.BLOCKED) {
            throw new UserBlockedException("Your account is blocked, you cannot create a reservation");
        }

        if (penaltyRepository.existsActivePenalty(user.getId(), PenaltyStatus.ACTIVE, LocalDateTime.now())) {
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

        if (slotStart.isBefore(LocalDateTime.now())) {
            throw new ReservationWindowException("Cannot create a reservation for a past time slot");
        }

        if (slotDate.isAfter(LocalDate.now().plusDays(library.getReservationWindowInDays()))) {
            throw new ReservationWindowException(
                    "You can only reserve for the next " + library.getReservationWindowInDays() + " days");
        }

        // 4) Active reservation limit check (Thread-safe due to pessimistic lock)
        Long activeCount = reservationRepository.countByUserIdAndStatusIn(user.getId(), ReservationStatus.OCCUPYING);
        if (activeCount >= library.getMaxActiveReservationsPerUser()) {
            throw new MaxActiveReservationsReachedException(
                    "You can have a maximum of " + library.getMaxActiveReservationsPerUser() + " active reservations");
        }

        // one person only get one reservation per a slot
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
                .endTime(LocalDateTime.of(slotDate, slot.getEndTime()))
                .reservationTime(LocalDateTime.now())
                .status(ReservationStatus.PENDING)
                .build();

        try {
            // flush is required here to force database constraint check before transaction commit phase
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

        // Fetch user WITHOUT LOCK (Read-only operation)
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

        // 1) Fetch user by Keycloak ID
        User user = userRepository.findByKeycloakId(keycloakSub)
                .orElseThrow(() -> new UserNotFoundException("User not found for Keycloak ID: " + keycloakSub));

        // 2) Fetch reservation with details
        Reservation reservation = reservationRepository.findByIdWithDetails(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with ID: " + reservationId));

        // 3) Ownership check: A user can only cancel their own reservation
        if (!reservation.getUser().getId().equals(user.getId())) {
            throw new ReservationDoesNotBelongToUserException("This reservation does not belong to you");
        }

        // 4) State check: Only non-terminal (cancellable) statuses can be cancelled
        // Note: Assumes PENDING or OCCUPYING are cancellable.
        if (reservation.getStatus() == ReservationStatus.CANCELLED ||
                reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new ReservationNotCancellableException(
                    "This reservation cannot be cancelled. Current status: " + reservation.getStatus());
        }

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new ReservationNotCancellableException(
                    "Only pending reservations can be cancelled. Current status: " + reservation.getStatus());
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        if (req.getReason() != null && !req.getReason().isBlank()) reservation.setCancellationReason(req.getReason());
        logStatusChange(reservation, ReservationStatus.PENDING, ReservationStatus.CANCELLED,
                user, StatusChangeReason.USER_CANCELLED, req.getReason());

        // Managed entity will automatically flush on commit. @Version handles optimistic locking.
        return toMyResponse(reservation);
    }


    @Transactional
    public MyReservationResponse checkIn(String keycloakSub, CheckInRequest req) {

        // 1) Fetch user by Keycloak ID
        User user = userRepository.findByKeycloakId(keycloakSub)
                .orElseThrow(() -> new UserNotFoundException("User not found for Keycloak ID: " + keycloakSub));

        // 2) Fetch desk by QR token from DTO request
        Desk desk = deskRepository.findByQrCodeCode(req.getDeskQrToken())
                .orElseThrow(() -> new InvalidQrCodeException("Invalid QR code"));

        // 3) QR Code status verification (Prevents check-in using cancelled or renewed QR codes)
        if (desk.getQrCode().getStatus() != QRCodeStatus.ACTIVE) {
            throw new InvalidQrCodeException("This QR code is no longer active");
        }

        LocalDateTime now = LocalDateTime.now();

        // 4) Find the pending reservation for this user and specific desk
        Reservation reservation = reservationRepository
                .findPendingForCheckIn(user.getId(), desk.getId(), ReservationStatus.PENDING, now)
                .stream()
                .findFirst()
                .orElseThrow(() -> new CheckInNotAvailableException(
                        "You do not have a pending reservation available for check-in at this desk right now"));

        // 5) Window verification: [startTime, startTime + checkInTimeoutMinutes]
        Library library = reservation.getSlot().getSaloon().getLibrary();
        LocalDateTime deadline = reservation.getStartTime().plusMinutes(library.getCheckInTimeoutMinutes());

        if (now.isBefore(reservation.getStartTime())) {
            throw new CheckInNotAvailableException("The reservation time window has not started yet");
        }

        if (now.isAfter(deadline)) {
            throw new CheckInWindowExpiredException("Your check-in time frame has expired");
        }

        // 6) Activate reservation status and set check-in time
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setCheckInTime(now);
        logStatusChange(reservation, ReservationStatus.PENDING, ReservationStatus.ACTIVE, user, null, "checked in");

        // Managed entity will automatically flush on transaction commit. @Version handles concurrency control.
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