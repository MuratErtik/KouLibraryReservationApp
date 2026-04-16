package org.koulibrary.koulibraryreservationapp.repositories;


import org.koulibrary.koulibraryreservationapp.entities.Desk;
import org.koulibrary.koulibraryreservationapp.entities.Saloon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeskRepository extends JpaRepository<Desk, Long> {
    boolean existsByDeskNumberAndSaloon(Integer deskNumber, Saloon saloon);

    boolean existsByDeskNumber(Integer deskNumber);

    Page<Desk> findBySaloon(Saloon saloon, Pageable pageable);
}




