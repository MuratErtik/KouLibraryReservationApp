package org.koulibrary.koulibraryreservationapp.events;

import org.koulibrary.koulibraryreservationapp.domains.NotificationType;

public record NotificationEvent(
        Long userId, String email, NotificationType type,
        String title, String body, Long reservationId, Long penaltyId) {}
