package org.koulibrary.koulibraryreservationapp.managers;

import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.entities.Library;
import org.koulibrary.koulibraryreservationapp.entities.LibraryClosures;
import org.koulibrary.koulibraryreservationapp.exceptions.IntervalDateException;
import org.koulibrary.koulibraryreservationapp.exceptions.LibraryAlreadyExistsException;
import org.koulibrary.koulibraryreservationapp.exceptions.LibraryNotFoundException;
import org.koulibrary.koulibraryreservationapp.repositories.LibraryClosuresRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            throw new IntervalDateException("The library is already closed during this time interval.");
        }


        return libraryClosuresRepository.save(libraryClosures);

    }

    @Transactional(readOnly = true)
    public LibraryClosures getLibraryClosureById(Long libraryClosureId) {

        return libraryClosuresRepository.findById(libraryClosureId)
                .orElseThrow(() -> new LibraryNotFoundException("Library closure not found with id " + libraryClosureId));

    }

    @Transactional
    public void updateLibraryClosure(LibraryClosures libraryClosuresToUpdate) {
        libraryClosuresRepository.save(libraryClosuresToUpdate);
    }

    @Transactional(readOnly = true)
    public Page<LibraryClosures> getAllLibraryClosures(Pageable pageable,Library library) {


        return libraryClosuresRepository.findByLibrary(library,pageable);
    }

    @Transactional
    public void deleteLibraryClosureId(Long closureId) {

        libraryClosuresRepository.deleteById(closureId);
    }
}
