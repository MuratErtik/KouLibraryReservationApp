package org.koulibrary.koulibraryreservationapp.repositories;

import org.koulibrary.koulibraryreservationapp.entities.LibraryWorkingHours;
import org.koulibrary.koulibraryreservationapp.entities.Saloon;
import org.koulibrary.koulibraryreservationapp.entities.SaloonWorkingHours;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;

public interface SaloonWorkingHoursRepository  extends JpaRepository<SaloonWorkingHours, Long> {

    Page<SaloonWorkingHours> findBySaloon(Saloon saloon, Pageable pageable);

    boolean existsByDayOfWeekAndSaloon(DayOfWeek dayOfWeek, Saloon saloon);
}
