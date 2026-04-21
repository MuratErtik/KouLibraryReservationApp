package org.koulibrary.koulibraryreservationapp.managers;


import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.entities.Library;
import org.koulibrary.koulibraryreservationapp.entities.LibraryClosures;
import org.koulibrary.koulibraryreservationapp.exceptions.IntervalDateException;
import org.koulibrary.koulibraryreservationapp.exceptions.LibraryNotFoundException;
import org.koulibrary.koulibraryreservationapp.repositories.LibraryClosuresRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LibraryClosureManager {

    private final LibraryClosuresRepository libraryClosuresRepository;



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


    public LibraryClosures getLibraryClosureById(Long libraryClosureId) {

        return libraryClosuresRepository.findById(libraryClosureId)
                .orElseThrow(() -> new LibraryNotFoundException("Library closure not found with id " + libraryClosureId));

    }


    public void updateLibraryClosure(LibraryClosures libraryClosuresToUpdate) {
        libraryClosuresRepository.save(libraryClosuresToUpdate);
    }


    public Page<LibraryClosures> getAllLibraryClosuresByLibrary(Pageable pageable,Library library) {


        return libraryClosuresRepository.findByLibrary(library,pageable);
    }


    public Page<LibraryClosures> getAllLibraryClosures(Pageable pageable) {


        return libraryClosuresRepository.findAll(pageable);
    }


    public void deleteLibraryClosureId(Long closureId) {

        libraryClosuresRepository.deleteById(closureId);
    }


    public void checkDateIntervalConflict(Library library, LibraryClosures libraryClosures, LocalDateTime startDateTime, LocalDateTime endDateTime) {

        if (libraryClosuresRepository
                .existsByLibraryAndStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqualAndIdNot(
                        library,
                        endDateTime,
                        startDateTime,
                        libraryClosures.getId()
                )) {
            throw new IntervalDateException("The library is already closed during this time interval.");
        }

    }
}
