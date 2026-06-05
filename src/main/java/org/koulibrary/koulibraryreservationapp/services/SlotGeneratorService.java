package org.koulibrary.koulibraryreservationapp.services;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.koulibrary.koulibraryreservationapp.domains.LibraryStatus;
import org.koulibrary.koulibraryreservationapp.domains.SaloonStatus;
import org.koulibrary.koulibraryreservationapp.entities.*;
import org.koulibrary.koulibraryreservationapp.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
public class SlotGeneratorService {

    private final SaloonTimeSlotRepository saloonTimeSlotRepository;
    private final LibraryWorkingHoursRepository libraryWorkingHoursRepository;
    private final LibraryClosuresRepository libraryClosuresRepository;
    private final SaloonWorkingHoursRepository saloonWorkingHoursRepository;
    private final SaloonClosuresRepository saloonClosuresRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void generateSlotsForDate(Saloon saloon, Library library, LocalDate date) {

        DayOfWeek dayOfWeek = date.getDayOfWeek();

        Optional<LibraryWorkingHours> libraryWorkingHours =
                libraryWorkingHoursRepository.findByLibraryAndDayOfWeek(library, dayOfWeek);

        if (libraryWorkingHours.isEmpty()) {
            log.debug("{} is closed on {}, no slots generated", library.getName(), date);
            return;
        }

        List<LibraryClosures> libraryClosures =
                libraryClosuresRepository.findByLibraryIdAndDate(library.getId(), date);

        boolean isLibraryFullyClosed = libraryClosures.stream().anyMatch(sc ->
                !sc.getStartDateTime().toLocalTime().isAfter(libraryWorkingHours.get().getOpeningTime()) &&
                        !sc.getEndDateTime().toLocalTime().isBefore(libraryWorkingHours.get().getClosingTime())
        );

        if (isLibraryFullyClosed) {
            log.debug("{} is closed all day on {}", library.getName(), date);
            return;
        }

        LocalTime opening;
        LocalTime closing;

        Optional<SaloonWorkingHours> saloonWorkingHours =
                saloonWorkingHoursRepository.findBySaloonAndDayOfWeek(saloon, dayOfWeek);

        if (saloonWorkingHours.isPresent()) {
            if (saloonWorkingHours.get().getIsClosed()) {
                log.debug("Saloon {} is closed on {}", saloon.getId(), dayOfWeek);
                return;
            }
            opening = saloonWorkingHours.get().getOpeningTime();
            closing = saloonWorkingHours.get().getClosingTime();
        } else {
            opening = libraryWorkingHours.get().getOpeningTime();
            closing = libraryWorkingHours.get().getClosingTime();
        }

        List<SaloonClosure> saloonClosures =
                saloonClosuresRepository.findBySaloonIdAndDate(saloon.getId(), date);

        boolean isSaloonFullyClosed = saloonClosures.stream().anyMatch(sc ->
                !sc.getStartDateTime().toLocalTime().isAfter(opening) &&
                        !sc.getEndDateTime().toLocalTime().isBefore(closing)
        );

        if (isSaloonFullyClosed) {
            log.debug("{} is closed all day on {}", saloon.getName(), date);
            return;
        }

        int duration = saloon.getSlotDurationMinutes() != null
                ? saloon.getSlotDurationMinutes()
                : library.getSlotDurationMinutes();

        generateSlots(saloon, date, opening, closing, duration, libraryClosures, saloonClosures);

        entityManager.flush();
        entityManager.clear();
    }

    private void generateSlots(Saloon saloon, LocalDate date,
                               LocalTime opening, LocalTime closing, int duration,
                               List<LibraryClosures> libraryClosures,
                               List<SaloonClosure> saloonClosures) {

        Set<LocalTime> existingStartTimes =
                saloonTimeSlotRepository.findStartTimesBySaloonAndDate(saloon, date);
        Set<LocalTime> plannedStartTimes = new HashSet<>();

        List<SaloonTimeSlot> slotsToSave = new ArrayList<>();
        LocalTime cursor = opening;

        while (true) {
            LocalTime slotEnd = cursor.plusMinutes(duration);

            // 1) Past midnight (slotEnd wrapped around)  stop
            // 2) Exceeded closing time stop
            if (!slotEnd.isAfter(cursor) || slotEnd.isAfter(closing)) {
                break;
            }

            LocalTime slotStart = cursor;
            cursor = slotEnd;

            if (isConflicting(date, slotStart, slotEnd, libraryClosures,
                    LibraryClosures::getStartDateTime, LibraryClosures::getEndDateTime)) {
                continue;
            }
            if (isConflicting(date, slotStart, slotEnd, saloonClosures,
                    SaloonClosure::getStartDateTime, SaloonClosure::getEndDateTime)) {
                continue;
            }

            // Skip if it already exists in DB or was generated in this round
            if (existingStartTimes.contains(slotStart) || !plannedStartTimes.add(slotStart)) {
                continue;
            }

            slotsToSave.add(SaloonTimeSlot.builder()
                    .saloon(saloon).date(date)
                    .startTime(slotStart).endTime(slotEnd)
                    .isAvailable(true).build());

            if (slotsToSave.size() >= 50) {
                saloonTimeSlotRepository.saveAll(slotsToSave);
                entityManager.flush();
                entityManager.clear();
                slotsToSave.clear();
            }
        }

        if (!slotsToSave.isEmpty()) {
            saloonTimeSlotRepository.saveAll(slotsToSave);
        }
    }

    private <T> boolean isConflicting(LocalDate date, LocalTime slotStart, LocalTime slotEnd,
                                      List<T> closures,
                                      Function<T, LocalDateTime> getStart,
                                      Function<T, LocalDateTime> getEnd) {

        LocalDateTime slotStartDT = LocalDateTime.of(date, slotStart);
        LocalDateTime slotEndDT = LocalDateTime.of(date, slotEnd);

        return closures.stream().anyMatch(closure ->
                slotStartDT.isBefore(getEnd.apply(closure)) &&
                        slotEndDT.isAfter(getStart.apply(closure))
        );
    }
}