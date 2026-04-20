package org.koulibrary.koulibraryreservationapp.managers;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.entities.Desk;

import org.koulibrary.koulibraryreservationapp.entities.Library;
import org.koulibrary.koulibraryreservationapp.entities.Saloon;
import org.koulibrary.koulibraryreservationapp.exceptions.DeskAlreadyExistsException;
import org.koulibrary.koulibraryreservationapp.exceptions.DeskNotFoundException;
import org.koulibrary.koulibraryreservationapp.exceptions.LibraryAlreadyExistsException;
import org.koulibrary.koulibraryreservationapp.repositories.DeskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DeskManager {

    private final DeskRepository deskRepository;

    @Transactional
    public Desk saveDesk(Desk desk) {

        if(deskRepository.existsByDeskNumberAndSaloon(desk.getDeskNumber(), desk.getSaloon())) {
            throw new DeskAlreadyExistsException("Desk already exists with saloon name: " + desk.getSaloon().getName()+" and saloon number: " + desk.getDeskNumber());
        }
        return deskRepository.save(desk);

    }

    @Transactional(readOnly = true)
    public Desk getDeskById(Long deskId) {

        return deskRepository.findById(deskId).orElseThrow(() -> new DeskNotFoundException("Desk not found with id: "+deskId));
    }

    @Transactional
    public void updateDesk(Desk deskToUpdate) {
        deskRepository.save(deskToUpdate);
    }



    @Transactional
    public void checkNameConflict(Desk desk,Integer deskNumber) {

        if (!desk.getDeskNumber().equals(deskNumber)) {
            if (deskRepository.existsByDeskNumber(deskNumber)) {
                throw new DeskAlreadyExistsException("Desk already exists with name: " + deskNumber);
            }
        }
    }


    @Transactional(readOnly = true)
    public Page<Desk> getAllDesks(Pageable pageable, Saloon saloon) {
        return deskRepository.findBySaloon(saloon, pageable);
    }

    @Transactional
    public void deleteDeskById(Long deskId) {
        deskRepository.deleteById(deskId);
    }
}
