package org.koulibrary.koulibraryreservationapp.managers;

import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.entities.Saloon;
import org.koulibrary.koulibraryreservationapp.entities.SaloonWorkingHours;
import org.koulibrary.koulibraryreservationapp.exceptions.LibraryWorkingHoursAlreadyCreatedException;
import org.koulibrary.koulibraryreservationapp.exceptions.SaloonWorkingHoursNotFoundException;
import org.koulibrary.koulibraryreservationapp.repositories.LibraryWorkingHoursRepository;
import org.koulibrary.koulibraryreservationapp.repositories.SaloonWorkingHoursRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaloonWorkingHoursManager {

    private final SaloonWorkingHoursRepository saloonWorkingHoursRepository;

    private final LibraryWorkingHoursRepository libraryWorkingHoursRepository;



    public SaloonWorkingHours saveSaloonWorkingHours(SaloonWorkingHours saloonWorkingHours) {

        if (saloonWorkingHoursRepository.existsByDayOfWeekAndSaloon(saloonWorkingHours.getDayOfWeek(), saloonWorkingHours.getSaloon())){
            throw new LibraryWorkingHoursAlreadyCreatedException("Saloon working hours already exists with saloon: " + saloonWorkingHours.getSaloon().getName() + " and day: " + saloonWorkingHours.getDayOfWeek().name());
        }

        return saloonWorkingHoursRepository.save(saloonWorkingHours);


    }

    public SaloonWorkingHours getSaloonWorkingHoursById(Long saloonWorkingHoursId) {

        return saloonWorkingHoursRepository.findById(saloonWorkingHoursId)
                .orElseThrow(() -> new SaloonWorkingHoursNotFoundException(
                        "Working hours does not exist in Saloon or Library with id: " + saloonWorkingHoursId));
    }


    public void updateSaloonWorkingHours(SaloonWorkingHours saloonWorkingHours) {
        saloonWorkingHoursRepository.save(saloonWorkingHours);
    }



    public Page<SaloonWorkingHours> getAllSaloonWorkingHours(Pageable pageable, Saloon saloon) {

        return saloonWorkingHoursRepository.findBySaloon(saloon,pageable);
    }



    public void deleteSaloonWorkingHoursById(Long saloonWorkingHoursId) {
        saloonWorkingHoursRepository.deleteById(saloonWorkingHoursId);
    }
}
