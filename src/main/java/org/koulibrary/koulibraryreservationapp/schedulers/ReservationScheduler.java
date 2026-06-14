package org.koulibrary.koulibraryreservationapp.schedulers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.koulibrary.koulibraryreservationapp.services.NotificationService;
import org.koulibrary.koulibraryreservationapp.services.PenaltyService;
import org.koulibrary.koulibraryreservationapp.services.ReservationService;
import org.koulibrary.koulibraryreservationapp.services.WaitlistService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationScheduler {

    private final ReservationService reservationService;

    private final PenaltyService penaltyService;
    private final NotificationService notificationService;
    private final WaitlistService waitlistService;

    @Scheduled(fixedDelay = 60_000)   // every min
    public void runLifecycle() {
        int noShows   = reservationService.markNoShows();
        int completed = reservationService.autoCompleteEnded();
        int expired   = penaltyService.expirePenalties();
        int reminders = notificationService.sendDueCheckInReminders();
        int waitlistOffers = waitlistService.processWaitlist();

        if (noShows > 0 || completed > 0 || expired > 0 || reminders > 0) {
            log.info("Lifecycle: {} no-show, {} completed, {} penalty expired, {} reminder sent, {} waitlist offers",
                    noShows, completed, expired, reminders, waitlistOffers);
        }
    }
}
