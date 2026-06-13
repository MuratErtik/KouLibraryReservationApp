package org.koulibrary.koulibraryreservationapp.listeners;

import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.events.NotificationEvent;
import org.koulibrary.koulibraryreservationapp.services.NotificationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(NotificationEvent event) {
        notificationService.deliver(event); // fires only after the business tx commits
    }
}
