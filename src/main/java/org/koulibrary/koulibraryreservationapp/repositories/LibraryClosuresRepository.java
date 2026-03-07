package org.koulibrary.koulibraryreservationapp.repositories;

import org.koulibrary.koulibraryreservationapp.entities.Library;
import org.koulibrary.koulibraryreservationapp.entities.LibraryClosures;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface LibraryClosuresRepository extends JpaRepository<LibraryClosures, Long> {

    Boolean existsByLibraryAndStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqual(
            Library library,
            LocalDateTime closingDate,
            LocalDateTime openingDate
    );

    Optional<LibraryClosures> findById(Long id);

}
