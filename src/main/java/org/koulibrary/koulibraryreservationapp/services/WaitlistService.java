package org.koulibrary.koulibraryreservationapp.services;

import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.domains.*;
import org.koulibrary.koulibraryreservationapp.dtos.responses.NotificationContent;
import org.koulibrary.koulibraryreservationapp.dtos.responses.PageResponse;
import org.koulibrary.koulibraryreservationapp.dtos.responses.WaitlistResponse;
import org.koulibrary.koulibraryreservationapp.entities.SaloonTimeSlot;
import org.koulibrary.koulibraryreservationapp.entities.User;
import org.koulibrary.koulibraryreservationapp.entities.Waitlist;
import org.koulibrary.koulibraryreservationapp.events.NotificationEvent;
import org.koulibrary.koulibraryreservationapp.exceptions.*;
import org.koulibrary.koulibraryreservationapp.repositories.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.koulibrary.koulibraryreservationapp.configs.TimeConfig.APP_ZONE;

@Service
@RequiredArgsConstructor
public class WaitlistService {

    public static final List<WaitlistStatus> ACTIVE = List.of(WaitlistStatus.WAITING, WaitlistStatus.NOTIFIED);
    private static final int CLAIM_WINDOW_MINUTES = 15;

    private final WaitlistRepository waitlistRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final DeskRepository deskRepository;
    private final SaloonTimeSlotRepository saloonTimeSlotRepository;
    private final PenaltyRepository penaltyRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public WaitlistResponse join(String keycloakSub, Long slotId) {
        LocalDateTime now = LocalDateTime.now(APP_ZONE);

        User user = userRepository.findByKeycloakId(keycloakSub)
                .orElseThrow(() -> new UserNotFoundException("User not found for Keycloak ID: " + keycloakSub));
        if (user.getUserStatus() != UserStatus.ACTIVE) {
            throw new AccountNotActiveException("Your account is not active");
        }
        if (penaltyRepository.existsBlockingPenalty(user.getId(), PenaltyService.BLOCKING, now)) {
            throw new UserBlockedException("You have an active penalty");
        }

        SaloonTimeSlot slot = saloonTimeSlotRepository.findById(slotId)
                .orElseThrow(() -> new SlotNotFoundException("Slot not found with id " + slotId));

        LocalDateTime slotEnd = LocalDateTime.of(slot.getDate(), slot.getEndTime());
        if (!slotEnd.isAfter(now))                     throw new SlotNotAvailableException("Slot already ended");
        if (Boolean.FALSE.equals(slot.getIsAvailable())) throw new SlotNotAvailableException("Slot is not available");

        long bookable = deskRepository.countBySaloonIdAndStatusNot(slot.getSaloon().getId(), DeskStatus.OUT_OF_SERVICE);
        long occupied = reservationRepository.countOccupyingBySlotId(slotId, ReservationStatus.OCCUPYING);
        if (occupied < bookable) {
            throw new SlotNotFullException("There are free desks in this slot; reserve directly");
        }
        if (reservationRepository.existsByUserIdAndSlotIdAndStatusIn(user.getId(), slotId, ReservationStatus.OCCUPYING)) {
            throw new AlreadyOnWaitlistException("You already have a reservation for this slot");
        }
        if (waitlistRepository.existsByUserIdAndSlotIdAndStatusIn(user.getId(), slotId, ACTIVE)) {
            throw new AlreadyOnWaitlistException("You are already on the waitlist for this slot");
        }
        int max = slot.getSaloon().getLibrary().getMaxActiveReservationsPerUser();
        if (waitlistRepository.countByUserIdAndStatusIn(user.getId(), ACTIVE) >= max) {
            throw new WaitlistLimitReachedException("Waitlist limit reached");
        }

        Waitlist w = Waitlist.builder()
                .user(user)
                .slot(slot)
                .status(WaitlistStatus.WAITING)
                .requestedAt(now)           // explicit APP_ZONE (Builder.Default would be UTC)
                .expiresAt(slotEnd)         // overall validity; overwritten to claim deadline on NOTIFIED
                .build();
        waitlistRepository.save(w);
        return toResponse(w);
    }

    @Transactional
    public void cancel(String keycloakSub, Long id) {
        User user = userRepository.findByKeycloakId(keycloakSub)
                .orElseThrow(() -> new UserNotFoundException("User not found for Keycloak ID: " + keycloakSub));
        Waitlist w = waitlistRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new WaitlistNotFoundException("Waitlist entry not found with id " + id));
        if (w.getStatus() == WaitlistStatus.WAITING || w.getStatus() == WaitlistStatus.NOTIFIED) {
            w.setStatus(WaitlistStatus.CANCELLED);
        }
    }

    @Transactional(readOnly = true)
    public PageResponse<WaitlistResponse> getMine(String keycloakSub, Pageable pageable) {
        User user = userRepository.findByKeycloakId(keycloakSub)
                .orElseThrow(() -> new UserNotFoundException("User not found for Keycloak ID: " + keycloakSub));
        Page<Waitlist> page = waitlistRepository.findByUserId(user.getId(), pageable);
        List<WaitlistResponse> content = page.getContent().stream().map(this::toResponse).toList();
        return PageResponse.<WaitlistResponse>builder()
                .content(content).pageNumber(page.getNumber()).pageSize(page.getSize())
                .totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).isLast(page.isLast())
                .build();
    }

    // --- scheduler job ---
    @Transactional
    public int processWaitlist() {
        LocalDateTime now = LocalDateTime.now(APP_ZONE);

        // 1. expire stale entries (NOTIFIED claim window passed OR slot passed)
        waitlistRepository.findByStatusInAndExpiresAtLessThanEqual(ACTIVE, now)
                .forEach(w -> w.setStatus(WaitlistStatus.EXPIRED));

        // 2. offer to next in line per slot
        int offered = 0;
        for (Long slotId : waitlistRepository.findSlotIdsByStatus(WaitlistStatus.WAITING)) {
            offered += offerForSlot(slotId, now);
        }
        return offered;
    }

    private int offerForSlot(Long slotId, LocalDateTime now) {
        SaloonTimeSlot slot = saloonTimeSlotRepository.findById(slotId).orElse(null);
        if (slot == null || Boolean.FALSE.equals(slot.getIsAvailable())) return 0;

        LocalDateTime slotEnd = LocalDateTime.of(slot.getDate(), slot.getEndTime());
        if (!slotEnd.isAfter(now)) return 0;

        long bookable = deskRepository.countBySaloonIdAndStatusNot(slot.getSaloon().getId(), DeskStatus.OUT_OF_SERVICE);
        long occupied = reservationRepository.countOccupyingBySlotId(slotId, ReservationStatus.OCCUPYING);
        long free = bookable - occupied;
        if (free <= 0) return 0;

        long activeClaims = waitlistRepository
                .findBySlotIdAndStatusInOrderByRequestedAt(slotId, List.of(WaitlistStatus.NOTIFIED)).size();
        long canOffer = free - activeClaims;
        if (canOffer <= 0) return 0;

        List<Waitlist> waiting = waitlistRepository
                .findBySlotIdAndStatusInOrderByRequestedAt(slotId, List.of(WaitlistStatus.WAITING));

        int count = 0;
        for (Waitlist w : waiting) {
            if (count >= canOffer) break;

            LocalDateTime claimDeadline = now.plusMinutes(CLAIM_WINDOW_MINUTES);
            w.setStatus(WaitlistStatus.NOTIFIED);
            w.setNotifiedAt(now);
            w.setExpiresAt(claimDeadline.isBefore(slotEnd) ? claimDeadline : slotEnd);

            NotificationContent c = NotificationMessages.waitlistAvailable(
                    slot.getSaloon().getName(), slot.getDate(), slot.getStartTime(), slot.getEndTime());
            eventPublisher.publishEvent(new NotificationEvent(
                    w.getUser().getId(), w.getUser().getEmail(), NotificationType.WAITLIST_AVAILABLE,
                    c.title(), c.body(), null, null));
            count++;
        }
        return count;
    }

    private WaitlistResponse toResponse(Waitlist w) {
        SaloonTimeSlot s = w.getSlot();
        return WaitlistResponse.builder()
                .id(w.getId())
                .slotId(s.getId())
                .saloonName(s.getSaloon().getName())
                .date(s.getDate())
                .startTime(s.getStartTime())
                .endTime(s.getEndTime())
                .status(w.getStatus())
                .requestedAt(w.getRequestedAt())
                .notifiedAt(w.getNotifiedAt())
                .expiresAt(w.getExpiresAt())
                .build();
    }
}
