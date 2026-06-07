package org.koulibrary.koulibraryreservationapp.repositories;

import org.koulibrary.koulibraryreservationapp.entities.Library;
import org.koulibrary.koulibraryreservationapp.entities.LibraryClosures;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LibraryClosuresRepository extends JpaRepository<LibraryClosures, Long> {

    Boolean existsByLibraryAndStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqual(
            Library library,
            LocalDateTime closingDate,
            LocalDateTime openingDate
    );

    Optional<LibraryClosures> findById(Long id);

    Page<LibraryClosures> findByLibrary(Library library, Pageable pageable);

    Boolean existsByLibraryAndStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqualAndIdNot(Library library, LocalDateTime startDateTimeIsLessThan, LocalDateTime endDateTimeIsGreaterThan, Long id);

    @Query("""
        SELECT c from LibraryClosures c
        WHERE c.library.id = :libraryId
        AND CAST(c.startDateTime AS date) <= :date 
        AND CAST(c.endDateTime AS date) >= :date
          """)
    List<LibraryClosures> findByLibraryIdAndDate(@Param("libraryId") Long libraryId, @Param("date") LocalDate date);
}
