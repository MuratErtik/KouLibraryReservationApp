package org.koulibrary.koulibraryreservationapp.managers;

import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.entities.Saloon;
import org.koulibrary.koulibraryreservationapp.entities.SaloonWorkingHours;
import org.koulibrary.koulibraryreservationapp.exceptions.LibraryWorkingHoursAlreadyCreatedException;
import org.koulibrary.koulibraryreservationapp.exceptions.SaloonWorkingHoursNotFoundException;
import org.koulibrary.koulibraryreservationapp.repositories.SaloonWorkingHoursRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SaloonWorkingHoursManager {

    private final SaloonWorkingHoursRepository saloonWorkingHoursRepository;


    @Transactional
    public SaloonWorkingHours saveSaloonWorkingHours(SaloonWorkingHours saloonWorkingHours) {

        if (saloonWorkingHoursRepository.existsByDayOfWeekAndSaloon(saloonWorkingHours.getDayOfWeek(), saloonWorkingHours.getSaloon())){
            throw new LibraryWorkingHoursAlreadyCreatedException("Saloon working hours already exists with saloon: " + saloonWorkingHours.getSaloon().getName() + " and day: " + saloonWorkingHours.getDayOfWeek().name());
        }

        return saloonWorkingHoursRepository.save(saloonWorkingHours);


    }

    @Transactional(readOnly = true)
    public SaloonWorkingHours getSaloonWorkingHoursById(Long saloonWorkingHoursId) {

        return saloonWorkingHoursRepository.findById(saloonWorkingHoursId).orElseThrow(
                () -> new SaloonWorkingHoursNotFoundException("Saloon Working hours does not exist with id: " + saloonWorkingHoursId));

    }

    @Transactional
    public void updateSaloonWorkingHours(SaloonWorkingHours saloonWorkingHours) {
        saloonWorkingHoursRepository.save(saloonWorkingHours);
    }



    @Transactional(readOnly = true)
    public Page<SaloonWorkingHours> getAllSaloonWorkingHours(Pageable pageable, Saloon saloon) {

        return saloonWorkingHoursRepository.findBySaloon(saloon,pageable);
    }


    @Transactional
    public void deleteSaloonWorkingHoursById(Long saloonWorkingHoursId) {
        saloonWorkingHoursRepository.deleteById(saloonWorkingHoursId);
    }
}
