package org.koulibrary.koulibraryreservationapp.managers;

import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.entities.Library;
import org.koulibrary.koulibraryreservationapp.entities.LibraryClosures;
import org.koulibrary.koulibraryreservationapp.entities.Saloon;
import org.koulibrary.koulibraryreservationapp.exceptions.SaloonAlreadyExistException;
import org.koulibrary.koulibraryreservationapp.exceptions.SaloonNotFoundException;
import org.koulibrary.koulibraryreservationapp.repositories.SaloonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SaloonManager {

    private final SaloonRepository saloonRepository;

    @Transactional
    public Saloon saveSaloon(Saloon saloon) {

        if(saloonRepository.existsByLibraryAndFloorAndName(saloon.getLibrary(), saloon.getFloor(), saloon.getName())) {
            throw new SaloonAlreadyExistException("Saloon already exists with this name: "+ saloon.getName()+" and floor: "+ saloon.getFloor());
        }


        return saloonRepository.save(saloon);

    }

    @Transactional(readOnly = true)
    public Saloon getSaloonById(Long saloonId) {

        return saloonRepository.findById(saloonId)
                .orElseThrow(() -> new SaloonNotFoundException("Saloon not found with id " + saloonId));

    }


    @Transactional
    public void updateSaloon(Saloon saloonToUpdate) {
        saloonRepository.save(saloonToUpdate);
    }

    @Transactional(readOnly = true)
    public Page<Saloon> getAllSaloons(Pageable pageable, Library library) {


        return saloonRepository.findByLibrary(library,pageable);
    }

    @Transactional
    public void deleteSaloonById(Long saloonId) {
        saloonRepository.deleteById(saloonId);
    }
}
