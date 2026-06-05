package org.koulibrary.koulibraryreservationapp.schedulers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.koulibrary.koulibraryreservationapp.services.LibraryTimeSlotService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TimeSlotScheduler {

    private final LibraryTimeSlotService generatorService;



    // it works every at 02.00AM
    @Scheduled(cron = "0 22 12 * * *", zone = "Europe/Istanbul")
    public void generateUpcomingSlots() {

        log.info("TimeSlot generation started");
        generatorService.generateSlotsForActiveSaloons();
        log.info("TimeSlot generation completed");
    }



}
