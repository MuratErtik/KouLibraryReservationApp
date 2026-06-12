package org.koulibrary.koulibraryreservationapp.repositories;


import jakarta.validation.constraints.NotBlank;
import org.koulibrary.koulibraryreservationapp.domains.DeskStatus;
import org.koulibrary.koulibraryreservationapp.entities.Desk;
import org.koulibrary.koulibraryreservationapp.entities.Saloon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DeskRepository extends JpaRepository<Desk, Long> , JpaSpecificationExecutor<Desk> {
    boolean existsByDeskNumberAndSaloon(Integer deskNumber, Saloon saloon);

    boolean existsByDeskNumber(Integer deskNumber);

    Page<Desk> findBySaloon(Saloon saloon, Pageable pageable);

    Set<Desk> findBySaloon(Saloon saloon);

    Long countBySaloonIdAndStatus(Long saloonId, DeskStatus status);

    Long countBySaloonIdAndStatusNot(Long saloonId, DeskStatus deskStatus);

    List<Desk> findBySaloonId(Long saloonId);

    @Query("SELECT d FROM Desk d JOIN d.qrCode q WHERE q.code = :code")
    Optional<Desk> findByQrCodeCode(@Param("code") String code);
}




