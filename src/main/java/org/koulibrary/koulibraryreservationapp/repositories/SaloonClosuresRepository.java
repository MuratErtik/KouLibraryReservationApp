package org.koulibrary.koulibraryreservationapp.repositories;


import org.koulibrary.koulibraryreservationapp.entities.Saloon;
import org.koulibrary.koulibraryreservationapp.entities.SaloonClosure;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SaloonClosuresRepository extends JpaRepository<SaloonClosure, Long> {



    Boolean existsBySaloonAndStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqual(
            Saloon saloon,
            LocalDateTime closingDate,
            LocalDateTime openingDate
    );


    Boolean existsBySaloonAndStartDateTimeLessThanEqualAndEndDateTimeGreaterThanAndIdNot(Saloon saloon,
                                                                                         LocalDateTime startDateTimeIsLessThan,
                                                                                         LocalDateTime endDateTimeIsGreaterThan, Long id);


    Page<SaloonClosure> findBySaloon(Saloon saloon, Pageable pageable);

    @Query("""
    SELECT s from SaloonClosure s 
    WHERE s.saloon.id = :saloonId
    AND CAST(s.startDateTime AS date) <= :date 
    AND CAST(s.endDateTime AS date) >= :date
    """)
    List<SaloonClosure> findBySaloonIdAndDate(@Param("saloonId") Long saloonId ,@Param("date") LocalDate date);
}
