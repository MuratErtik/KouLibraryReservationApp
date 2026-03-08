package org.koulibrary.koulibraryreservationapp.repositories;

import org.koulibrary.koulibraryreservationapp.entities.Library;
import org.koulibrary.koulibraryreservationapp.entities.LibraryWorkingHours;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;

public interface LibraryWorkingHoursRepository  extends JpaRepository<LibraryWorkingHours, Long> {

    Boolean existsByDayOfWeekAndLibrary(DayOfWeek dayOfWeek, Library library);

    Page<LibraryWorkingHours> findByLibrary(Library library, Pageable pageable);
}
