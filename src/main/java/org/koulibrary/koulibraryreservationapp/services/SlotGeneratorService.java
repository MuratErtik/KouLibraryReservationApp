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
    private final ReservationService reservationService;   // CHANGED

    public static final int WINDOW_DAYS = 7;

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

        LocalTime opening, closing;
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

        Set<LocalTime> existingStartTimes = saloonTimeSlotRepository.findStartTimesBySaloonAndDate(saloon, date);
        Set<LocalTime> plannedStartTimes = new HashSet<>();
        List<SaloonTimeSlot> slotsToSave = new ArrayList<>();
        LocalTime cursor = opening;

        while (true) {
            LocalTime slotEnd = cursor.plusMinutes(duration);
            if (!slotEnd.isAfter(cursor) || slotEnd.isAfter(closing)) break;

            LocalTime slotStart = cursor;
            cursor = slotEnd;

            if (existingStartTimes.contains(slotStart) || !plannedStartTimes.add(slotStart)) {
                continue;
            }

            boolean closed =
                    isConflicting(date, slotStart, slotEnd, libraryClosures,
                            LibraryClosures::getStartDateTime, LibraryClosures::getEndDateTime) ||
                            isConflicting(date, slotStart, slotEnd, saloonClosures,
                                    SaloonClosure::getStartDateTime, SaloonClosure::getEndDateTime);

            slotsToSave.add(SaloonTimeSlot.builder()
                    .saloon(saloon).date(date)
                    .startTime(slotStart).endTime(slotEnd)
                    .isAvailable(!closed)
                    .build());

            if (slotsToSave.size() >= 50) {
                saloonTimeSlotRepository.saveAll(slotsToSave);
                entityManager.flush(); entityManager.clear(); slotsToSave.clear();
            }
        }
        if (!slotsToSave.isEmpty()) saloonTimeSlotRepository.saveAll(slotsToSave);
    }

    @Transactional
    public void recomputeAvailability(Saloon saloon, Library library, LocalDate from, LocalDate to) {

        boolean operating = library.getStatus() == LibraryStatus.OPEN
                && saloon.getStatus() == SaloonStatus.OPEN;

        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            List<SaloonTimeSlot> slots = saloonTimeSlotRepository.findBySaloonIdAndDate(saloon.getId(), date);
            if (slots.isEmpty()) continue;

            DayHours hours = resolveHours(saloon, library, date);
            List<LibraryClosures> libC = libraryClosuresRepository.findByLibraryIdAndDate(library.getId(), date);
            List<SaloonClosure>  salC = saloonClosuresRepository.findBySaloonIdAndDate(saloon.getId(), date);

            for (SaloonTimeSlot slot : slots) {
                boolean withinHours = hours.open()
                        && !slot.getStartTime().isBefore(hours.opening())
                        && !slot.getEndTime().isAfter(hours.closing());

                boolean closed =
                        isConflicting(date, slot.getStartTime(), slot.getEndTime(), libC,
                                LibraryClosures::getStartDateTime, LibraryClosures::getEndDateTime) ||
                                isConflicting(date, slot.getStartTime(), slot.getEndTime(), salC,
                                        SaloonClosure::getStartDateTime, SaloonClosure::getEndDateTime);

                boolean wasAvailable = Boolean.TRUE.equals(slot.getIsAvailable());
                boolean available = operating && withinHours && !closed;
                slot.setIsAvailable(available);

                // only on transition available -> unavailable, cancel PENDING reservations (+ notify)
                if (wasAvailable && !available) {
                    reservationService.cancelPendingReservationsForClosure(slot.getId());
                }
            }
        }
    }

    private record DayHours(boolean open, LocalTime opening, LocalTime closing) {}

    private DayHours resolveHours(Saloon saloon, Library library, LocalDate date) {
        DayOfWeek dow = date.getDayOfWeek();

        Optional<LibraryWorkingHours> lwh =
                libraryWorkingHoursRepository.findByLibraryAndDayOfWeek(library, dow);
        if (lwh.isEmpty()) return new DayHours(false, null, null);

        Optional<SaloonWorkingHours> swh =
                saloonWorkingHoursRepository.findBySaloonAndDayOfWeek(saloon, dow);
        if (swh.isPresent()) {
            if (Boolean.TRUE.equals(swh.get().getIsClosed())) return new DayHours(false, null, null);
            return new DayHours(true, swh.get().getOpeningTime(), swh.get().getClosingTime());
        }
        return new DayHours(true, lwh.get().getOpeningTime(), lwh.get().getClosingTime());
    }

    @Transactional
    public void recomputeAvailability(Saloon saloon, Library library) {
        LocalDate today = LocalDate.now();
        recomputeAvailability(saloon, library, today, today.plusDays(WINDOW_DAYS));
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

    public void generateForSaloon(Saloon saloon, Library library, LocalDate from, LocalDate to) {
        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            generateSlotsForDate(saloon, library, date);
        }
    }

    @Transactional
    public void syncSaloon(Saloon saloon, Library library) {
        LocalDate from = LocalDate.now();
        LocalDate to   = from.plusDays(WINDOW_DAYS);
        generateForSaloon(saloon, library, from, to);
        recomputeAvailability(saloon, library, from, to);
    }
}