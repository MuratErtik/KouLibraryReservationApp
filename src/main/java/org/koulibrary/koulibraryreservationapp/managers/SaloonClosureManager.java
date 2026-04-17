package org.koulibrary.koulibraryreservationapp.managers;



import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.entities.Saloon;
import org.koulibrary.koulibraryreservationapp.entities.SaloonClosure;
import org.koulibrary.koulibraryreservationapp.exceptions.IntervalDateException;
import org.koulibrary.koulibraryreservationapp.exceptions.SaloonNotFoundException;
import org.koulibrary.koulibraryreservationapp.repositories.SaloonClosuresRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SaloonClosureManager {

    private final SaloonClosuresRepository saloonClosuresRepository;


    @Transactional
    public SaloonClosure saveSaloonClosure(SaloonClosure saloonClosures) {

        if(saloonClosuresRepository.existsBySaloonAndStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqual(
                saloonClosures.getSaloon(),
                saloonClosures.getStartDateTime(),
                saloonClosures.getEndDateTime()
        )){
            throw new IntervalDateException("The saloon is already closed during this time interval.");
        }


        return saloonClosuresRepository.save(saloonClosures);

    }

    @Transactional(readOnly = true)
    public SaloonClosure getSaloonClosureById(Long saloonClosureId) {

        return saloonClosuresRepository.findById(saloonClosureId)
                .orElseThrow(() -> new SaloonNotFoundException("Saloon closure not found with id " + saloonClosureId));

    }

    @Transactional
    public void updateSaloonClosure(SaloonClosure saloonClosuresToUpdate) {
        saloonClosuresRepository.save(saloonClosuresToUpdate);
    }

    @Transactional(readOnly = true)
    public Page<SaloonClosure> getAllSaloonClosureBySaloon(Pageable pageable, Saloon saloon) {


        return saloonClosuresRepository.findBySaloon(saloon,pageable);
    }

    @Transactional(readOnly = true)
    public Page<SaloonClosure> getAllSaloonClosure(Pageable pageable) {


        return saloonClosuresRepository.findAll(pageable);
    }

    @Transactional
    public void deleteSaloonClosureId(Long closureId) {

        saloonClosuresRepository.deleteById(closureId);
    }

    @Transactional()
    public void checkDateIntervalConflict(Saloon saloon, SaloonClosure saloonClosures, LocalDateTime startDateTime, LocalDateTime endDateTime) {

        if (saloonClosuresRepository
                .existsBySaloonAndStartDateTimeLessThanEqualAndEndDateTimeGreaterThanAndIdNot(
                        saloon,
                        endDateTime,
                        startDateTime,
                        saloonClosures.getId()
                )) {
            throw new IntervalDateException("The saloon is already closed during this time interval.");
        }

    }
}
