package org.koulibrary.koulibraryreservationapp.repositories;

import org.koulibrary.koulibraryreservationapp.entities.Saloon;
import org.koulibrary.koulibraryreservationapp.entities.SaloonTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public interface SaloonTimeSlotRepository extends JpaRepository<SaloonTimeSlot, Long> {


    boolean existsBySaloonAndDateAndStartTime(Saloon saloon, LocalDate date, LocalTime slotStart);

    @Query("SELECT MAX(s.date) FROM SaloonTimeSlot s WHERE s.saloon = :saloon")
    LocalDate findMaxDateBySaloon(@Param("saloon") Saloon saloon);


    @Query("SELECT s.startTime FROM SaloonTimeSlot s WHERE s.saloon = :saloon AND s.date = :date")
    Set<LocalTime> findStartTimesBySaloonAndDate(@Param("saloon") Saloon saloon,
                                                 @Param("date") LocalDate date);
}
