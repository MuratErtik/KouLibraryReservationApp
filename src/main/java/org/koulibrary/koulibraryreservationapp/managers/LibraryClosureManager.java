package org.koulibrary.koulibraryreservationapp.managers;

import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.entities.Library;
import org.koulibrary.koulibraryreservationapp.entities.LibraryClosures;
import org.koulibrary.koulibraryreservationapp.exceptions.LibraryAlreadyExistsException;
import org.koulibrary.koulibraryreservationapp.repositories.LibraryClosuresRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;

@Component
@RequiredArgsConstructor
public class LibraryClosureManager {

    private final LibraryClosuresRepository libraryClosuresRepository;


    @Transactional
    public LibraryClosures saveLibraryClosures(LibraryClosures libraryClosures) {

        if(libraryClosuresRepository.existsByLibraryAndStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqual(
                libraryClosures.getLibrary(),
                libraryClosures.getStartDateTime(),
                libraryClosures.getEndDateTime()
        )){
            throw new DateTimeException("The library is already closed during this time interval.");
        }


        return libraryClosuresRepository.save(libraryClosures);

    }
}
