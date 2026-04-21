package org.koulibrary.koulibraryreservationapp.managers;

import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.entities.Library;
import org.koulibrary.koulibraryreservationapp.entities.LibraryWorkingHours;
import org.koulibrary.koulibraryreservationapp.exceptions.LibraryWorkingHoursAlreadyCreatedException;
import org.koulibrary.koulibraryreservationapp.exceptions.LibraryWorkingHoursNotFoundException;
import org.koulibrary.koulibraryreservationapp.repositories.LibraryWorkingHoursRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LibraryWorkingHoursManager {

    private final LibraryWorkingHoursRepository libraryWorkingHoursRepository;



    public LibraryWorkingHours saveLibraryWorkingHours(LibraryWorkingHours libraryWorkingHours) {

        if (libraryWorkingHoursRepository.existsByDayOfWeekAndLibrary(libraryWorkingHours.getDayOfWeek(), libraryWorkingHours.getLibrary())){
            throw new LibraryWorkingHoursAlreadyCreatedException("Library already exists with library: " + libraryWorkingHours.getLibrary().getName() + " and day: " + libraryWorkingHours.getDayOfWeek().name());
        }

        return libraryWorkingHoursRepository.save(libraryWorkingHours);


    }

    public LibraryWorkingHours getLibraryWorkingHoursById(Long workingHoursId) {

        return libraryWorkingHoursRepository.findById(workingHoursId).orElseThrow(
                () -> new LibraryWorkingHoursNotFoundException("Library Working hours does not exist with id: " + workingHoursId));

    }


    public void updateLibraryWorkingHours(LibraryWorkingHours libraryWorkingHours) {
        libraryWorkingHoursRepository.save(libraryWorkingHours);
    }




    public Page<LibraryWorkingHours> getAllLibraryWorkingHoursByLibrary(Pageable pageable, Library library) {

        return libraryWorkingHoursRepository.findByLibrary(library,pageable);
    }

    public Page<LibraryWorkingHours> getAllLibraryWorkingHours(Pageable pageable) {

        return libraryWorkingHoursRepository.findAll(pageable);
    }



    public void deleteLibraryWorkingHoursById(Long workingHoursId) {
        libraryWorkingHoursRepository.deleteById(workingHoursId);
    }
}
