package org.koulibrary.koulibraryreservationapp.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jspecify.annotations.Nullable;
import org.koulibrary.koulibraryreservationapp.domains.DeskStatus;
import org.koulibrary.koulibraryreservationapp.domains.LibraryStatus;
import org.koulibrary.koulibraryreservationapp.domains.ReservationStatus;
import org.koulibrary.koulibraryreservationapp.domains.SaloonStatus;
import org.koulibrary.koulibraryreservationapp.dtos.responses.SlotResponse;
import org.koulibrary.koulibraryreservationapp.entities.*;
import org.koulibrary.koulibraryreservationapp.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class LibraryTimeSlotService {

    private final int DAYS_AHEAD = 7;

    private final SlotGeneratorService slotGeneratorService;

    private final SaloonRepository saloonRepository;

    private final ReservationRepository reservationRepository;

    private final DeskRepository deskRepository;

    private final SaloonTimeSlotRepository saloonTimeSlotRepository;



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


    @Transactional(readOnly = true)
    public List<SlotResponse> getSlots(Long saloonId, LocalDate date) {

        List<SaloonTimeSlot> slots = saloonTimeSlotRepository.findBySaloonIdAndDate(saloonId, date);
        if (slots.isEmpty()) return List.of();

        slots = slots.stream()
                .sorted(Comparator.comparing(SaloonTimeSlot::getStartTime))
                .toList();

        long bookableDeskCount = deskRepository.countBySaloonIdAndStatusNot(saloonId, DeskStatus.OUT_OF_SERVICE);

        List<Long> slotIds = slots.stream().map(SaloonTimeSlot::getId).toList();
        Map<Long, Long> reservedBySlot = new HashMap<>();
        for (Object[] row : reservationRepository.countReservedBySlotIds(slotIds, ReservationStatus.OCCUPYING)) {
            reservedBySlot.put((Long) row[0], (Long) row[1]);
        }

        return slots.stream().map(slot -> {
            boolean available = Boolean.TRUE.equals(slot.getIsAvailable());
            long reserved = reservedBySlot.getOrDefault(slot.getId(), 0L);
            long free = available ? Math.max(0, bookableDeskCount - reserved) : 0;
            return new SlotResponse(
                    slot.getId(), slot.getDate(),
                    slot.getStartTime(), slot.getEndTime(),
                    available, free);
        }).toList();
    }
}