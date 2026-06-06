package org.koulibrary.koulibraryreservationapp.repositories;

import org.koulibrary.koulibraryreservationapp.domains.ReservationStatus;
import org.koulibrary.koulibraryreservationapp.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("SELECT r FROM Reservation r WHERE r.slot.id = :slotId AND r.status IN :statuses")
    List<Reservation> findBySlotIdAndStatusIn(@Param("slotId") Long slotId,
                                              @Param("statuses") Collection<ReservationStatus> statuses);

    
}
