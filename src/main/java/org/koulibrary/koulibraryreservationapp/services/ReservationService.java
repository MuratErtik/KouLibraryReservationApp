package org.koulibrary.koulibraryreservationapp.services;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.koulibrary.koulibraryreservationapp.domains.DeskStatus;
import org.koulibrary.koulibraryreservationapp.domains.PenaltyStatus;
import org.koulibrary.koulibraryreservationapp.domains.ReservationStatus;
import org.koulibrary.koulibraryreservationapp.domains.UserStatus;
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



}