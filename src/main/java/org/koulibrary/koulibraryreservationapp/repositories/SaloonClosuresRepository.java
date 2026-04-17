package org.koulibrary.koulibraryreservationapp.repositories;


import org.koulibrary.koulibraryreservationapp.entities.Saloon;
import org.koulibrary.koulibraryreservationapp.entities.SaloonClosure;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

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
}
