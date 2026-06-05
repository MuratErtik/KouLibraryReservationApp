package org.koulibrary.koulibraryreservationapp.repositories;

import org.koulibrary.koulibraryreservationapp.domains.SaloonStatus;
import org.koulibrary.koulibraryreservationapp.entities.Library;
import org.koulibrary.koulibraryreservationapp.entities.Saloon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaloonRepository extends JpaRepository<Saloon, Long> {

    Boolean existsByLibraryAndFloorAndName(Library library, Integer floor, String name);

    Page<Saloon> findByLibrary(Library library, Pageable pageable);
    
    boolean existsByLibraryIdAndFloorAndNameAndIdNot(Long libraryId, Integer floorToCheck, String nameToCheck, Long saloonId);

    List<Saloon> findAllByStatus(SaloonStatus status);
}
