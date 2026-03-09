package org.koulibrary.koulibraryreservationapp.repositories;

import org.koulibrary.koulibraryreservationapp.entities.Library;
import org.koulibrary.koulibraryreservationapp.entities.Saloon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaloonRepository extends JpaRepository<Saloon, Long> {

    Boolean existsByLibraryAndFloorAndName(Library library, Integer floor, String name);
}
