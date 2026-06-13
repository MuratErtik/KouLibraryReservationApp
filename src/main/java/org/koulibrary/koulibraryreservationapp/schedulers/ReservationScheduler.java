package org.koulibrary.koulibraryreservationapp.schedulers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.koulibrary.koulibraryreservationapp.services.ReservationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationScheduler {

    private final ReservationService reservationService;

    @Scheduled(fixedDelay = 60_000)   // every min
    public void runLifecycle() {
        int noShows   = reservationService.markNoShows();
        int completed = reservationService.autoCompleteEnded();
        if (noShows > 0 || completed > 0) {
            log.info("Lifecycle: {} no-show, {} auto-completed", noShows, completed);
        }
    }
}
