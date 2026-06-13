package org.koulibrary.koulibraryreservationapp.services;

import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.domains.NotificationMessages;
import org.koulibrary.koulibraryreservationapp.domains.NotificationType;
import org.koulibrary.koulibraryreservationapp.domains.ReservationStatus;
import org.koulibrary.koulibraryreservationapp.dtos.responses.NotificationContent;
import org.koulibrary.koulibraryreservationapp.dtos.responses.NotificationResponse;
import org.koulibrary.koulibraryreservationapp.dtos.responses.PageResponse;
import org.koulibrary.koulibraryreservationapp.entities.Notification;
import org.koulibrary.koulibraryreservationapp.entities.Reservation;
import org.koulibrary.koulibraryreservationapp.entities.User;
import org.koulibrary.koulibraryreservationapp.events.NotificationEvent;
import org.koulibrary.koulibraryreservationapp.exceptions.NotificationNotFoundException;
import org.koulibrary.koulibraryreservationapp.exceptions.UserNotFoundException;
import org.koulibrary.koulibraryreservationapp.repositories.NotificationRepository;
import org.koulibrary.koulibraryreservationapp.repositories.PenaltyRepository;
import org.koulibrary.koulibraryreservationapp.repositories.ReservationRepository;
import org.koulibrary.koulibraryreservationapp.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.koulibrary.koulibraryreservationapp.configs.TimeConfig.APP_ZONE;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final PenaltyRepository penaltyRepository;
    private final EmailService emailService;

    private static final int CHECKIN_REMINDER_LEAD_MINUTES = 15;

    // Event-driven path (penalty / admin-cancel) — runs AFTER the business tx commits
    @Transactional
    public void deliver(NotificationEvent e) {
        Notification n = Notification.builder()
                .user(userRepository.getReferenceById(e.userId()))
                .type(e.type())
                .title(e.title())
                .body(e.body())
                .reservation(e.reservationId() != null ? reservationRepository.getReferenceById(e.reservationId()) : null)
                .penalty(e.penaltyId() != null ? penaltyRepository.getReferenceById(e.penaltyId()) : null)
                .createdAt(LocalDateTime.now(APP_ZONE))
                .build();
        notificationRepository.save(n);

        if (e.email() != null && !e.email().isBlank()) {
            emailService.send(e.email(), e.title(), e.body());
        }
    }

    // Scheduled path (check-in reminders) — with dedup via the notifications table
    @Transactional
    public int sendDueCheckInReminders() {
        LocalDateTime now = LocalDateTime.now(APP_ZONE);
        LocalDateTime until = now.plusMinutes(CHECKIN_REMINDER_LEAD_MINUTES);

        List<Reservation> upcoming =
                reservationRepository.findUpcomingByStatus(ReservationStatus.PENDING, now, until);

        int sent = 0;
        for (Reservation r : upcoming) {
            if (notificationRepository.existsByReservationIdAndType(r.getId(), NotificationType.CHECK_IN_REMINDER)) {
                continue; // already reminded
            }
            NotificationContent c = NotificationMessages.checkInReminder(
                    r.getSlot().getSaloon().getName(), r.getDesk().getDeskNumber(), r.getStartTime());

            Notification n = Notification.builder()
                    .user(r.getUser()).type(NotificationType.CHECK_IN_REMINDER)
                    .title(c.title()).body(c.body()).reservation(r)
                    .createdAt(now).build();
            notificationRepository.save(n);

            String email = r.getUser().getEmail();
            if (email != null && !email.isBlank()) {
                emailService.send(email, c.title(), c.body());
            }
            sent++;
        }
        return sent;
    }

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getMyNotifications(String keycloakSub, Pageable pageable) {
        User user = userRepository.findByKeycloakId(keycloakSub)
                .orElseThrow(() -> new UserNotFoundException("User not found for Keycloak ID: " + keycloakSub));
        Page<Notification> page = notificationRepository.findByUserId(user.getId(), pageable);
        List<NotificationResponse> content = page.getContent().stream().map(this::toResponse).toList();
        return PageResponse.<NotificationResponse>builder()
                .content(content).pageNumber(page.getNumber()).pageSize(page.getSize())
                .totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).isLast(page.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(String keycloakSub) {
        User user = userRepository.findByKeycloakId(keycloakSub)
                .orElseThrow(() -> new UserNotFoundException("User not found for Keycloak ID: " + keycloakSub));
        return notificationRepository.countByUserIdAndIsReadFalse(user.getId());
    }

    @Transactional
    public void markRead(String keycloakSub, Long id) {
        User user = userRepository.findByKeycloakId(keycloakSub)
                .orElseThrow(() -> new UserNotFoundException("User not found for Keycloak ID: " + keycloakSub));
        Notification n = notificationRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotificationNotFoundException("Notification not found with ID: " + id));
        if (Boolean.FALSE.equals(n.getIsRead())) {
            n.setIsRead(true);
            n.setReadAt(LocalDateTime.now(APP_ZONE));
        }
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .type(n.getType())
                .title(n.getTitle())
                .body(n.getBody())
                .read(Boolean.TRUE.equals(n.getIsRead()))
                .createdAt(n.getCreatedAt())
                .reservationId(n.getReservation() != null ? n.getReservation().getId() : null)
                .penaltyId(n.getPenalty() != null ? n.getPenalty().getId() : null)
                .build();
    }
}
