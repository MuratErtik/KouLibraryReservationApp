package org.koulibrary.koulibraryreservationapp.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.koulibrary.koulibraryreservationapp.domains.LibraryStatus;
import org.koulibrary.koulibraryreservationapp.domains.SaloonStatus;
import org.koulibrary.koulibraryreservationapp.entities.*;
import org.koulibrary.koulibraryreservationapp.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class LibraryTimeSlotService {

    private final int DAYS_AHEAD = 7;



    private final SlotGeneratorService slotGeneratorService;




    private final SaloonRepository saloonRepository;



    @Transactional
    public void generateSlotsForActiveSaloons() {

        List<Saloon> saloons = saloonRepository.findAllByStatus(SaloonStatus.OPEN);

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(DAYS_AHEAD);

        for (Saloon saloon : saloons) {

            Library library = saloon.getLibrary();

            if (library.getStatus() != LibraryStatus.OPEN) {
                log.debug("Skipping saloon {} because library {} is {}",
                        saloon.getName(), library.getName(), library.getStatus());
                continue;
            }


            List<LocalDate> dates = today.datesUntil(endDate.plusDays(1)).toList();

            for (LocalDate date : dates) {
                try {
                    slotGeneratorService.generateSlotsForDate(saloon, library, date);
                } catch (Exception e) {
                    log.error("Saloon {} - Slot generation failed for date {}: {}",
                            saloon.getId(), date, e.getMessage());
                }
            }
        }
    }





}