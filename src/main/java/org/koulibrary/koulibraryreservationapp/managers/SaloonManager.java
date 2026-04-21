package org.koulibrary.koulibraryreservationapp.managers;

import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.entities.Library;
import org.koulibrary.koulibraryreservationapp.entities.Saloon;
import org.koulibrary.koulibraryreservationapp.exceptions.SaloonAlreadyExistException;
import org.koulibrary.koulibraryreservationapp.exceptions.SaloonNotFoundException;
import org.koulibrary.koulibraryreservationapp.repositories.SaloonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaloonManager {

    private final SaloonRepository saloonRepository;


    public Saloon saveSaloon(Saloon saloon) {

        if(saloonRepository.existsByLibraryAndFloorAndName(saloon.getLibrary(), saloon.getFloor(), saloon.getName())) {
            throw new SaloonAlreadyExistException("Saloon already exists with this name: "+ saloon.getName()+" and floor: "+ saloon.getFloor());
        }


        return saloonRepository.save(saloon);

    }

    public Saloon getSaloonById(Long saloonId) {

        return saloonRepository.findById(saloonId)
                .orElseThrow(() -> new SaloonNotFoundException("Saloon not found with id " + saloonId));

    }



    public void updateSaloon(Saloon saloonToUpdate) {
        saloonRepository.save(saloonToUpdate);
    }

    public Page<Saloon> getAllSaloons(Pageable pageable, Library library) {


        return saloonRepository.findByLibrary(library,pageable);
    }


    public void deleteSaloonById(Long saloonId) {
        saloonRepository.deleteById(saloonId);
    }

    public boolean findDuplicate(Long libraryId, Integer floorToCheck, String nameToCheck, Long saloonId) {
        return saloonRepository.existsByLibraryIdAndFloorAndNameAndIdNot(
                libraryId, floorToCheck, nameToCheck, saloonId
        );

    }
}
