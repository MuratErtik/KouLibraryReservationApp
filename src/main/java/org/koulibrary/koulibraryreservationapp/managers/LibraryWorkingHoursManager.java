package org.koulibrary.koulibraryreservationapp.managers;

import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.entities.LibraryWorkingHours;
import org.koulibrary.koulibraryreservationapp.exceptions.LibraryWorkingHoursAlreadyCreatedException;
import org.koulibrary.koulibraryreservationapp.repositories.LibraryWorkingHoursRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class LibraryWorkingHoursManager {

    private final LibraryWorkingHoursRepository libraryWorkingHoursRepository;


    @Transactional
    public LibraryWorkingHours saveLibraryWorkingHours(LibraryWorkingHours libraryWorkingHours) {

        if (libraryWorkingHoursRepository.existsByDayOfWeekAndLibrary(libraryWorkingHours.getDayOfWeek(), libraryWorkingHours.getLibrary())){
            throw new LibraryWorkingHoursAlreadyCreatedException("Library already exists with library: " + libraryWorkingHours.getLibrary().getName() + " and day: " + libraryWorkingHours.getDayOfWeek().name());
        }

        return libraryWorkingHoursRepository.save(libraryWorkingHours);


    }
}
