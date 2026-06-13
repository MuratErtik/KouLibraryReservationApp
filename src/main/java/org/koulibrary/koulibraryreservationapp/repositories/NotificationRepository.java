package org.koulibrary.koulibraryreservationapp.repositories;

import org.koulibrary.koulibraryreservationapp.domains.NotificationType;
import org.koulibrary.koulibraryreservationapp.entities.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUserId(Long userId, Pageable pageable);
    Long countByUserIdAndIsReadFalse(Long userId);
    Boolean existsByReservationIdAndType(Long reservationId, NotificationType type); // reminder dedup
    Optional<Notification> findByIdAndUserId(Long id, Long userId);

}
