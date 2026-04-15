package org.koulibrary.koulibraryreservationapp.managers;

import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.entities.Desk;

import org.koulibrary.koulibraryreservationapp.exceptions.DeskAlreadyExistsException;
import org.koulibrary.koulibraryreservationapp.repositories.DeskRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DeskManager {

    private final DeskRepository deskRepository;

    @Transactional
    public Desk saveDesk(Desk desk) {

        if(deskRepository.existsByDeskNumberAndSaloon(desk.getDeskNumber(), desk.getSaloon())) {
            throw new DeskAlreadyExistsException("Desk already exists with saloon number: " + desk.getSaloon()+" and saloon number: " + desk.getDeskNumber());
        }
        return deskRepository.save(desk);

    }
}
