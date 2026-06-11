package org.koulibrary.koulibraryreservationapp.repositories;

import org.koulibrary.koulibraryreservationapp.domains.ReservationStatus;
import org.koulibrary.koulibraryreservationapp.entities.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("SELECT r FROM Reservation r WHERE r.slot.id = :slotId AND r.status IN :statuses")
    List<Reservation> findBySlotIdAndStatusIn(@Param("slotId") Long slotId,
                                              @Param("statuses") Collection<ReservationStatus> statuses);


    @Query("SELECT r.slot.id, COUNT(r) FROM Reservation r " +
            "WHERE r.slot.id IN :slotIds AND r.status IN :statuses GROUP BY r.slot.id")
    List<Object[]> countReservedBySlotIds(@Param("slotIds") Collection<Long> slotIds,
                                          @Param("statuses") Collection<ReservationStatus> statuses);

    @Query("SELECT r.desk.id FROM Reservation r WHERE r.slot.id = :slotId AND r.status IN :statuses")
    Set<Long> findReservedDeskIds(@Param("slotId") Long slotId,
                                  @Param("statuses") Collection<ReservationStatus> statuses);

    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
            "WHERE r.desk.id = :deskId AND r.slot.id = :slotId AND r.status IN :statuses")
    Boolean existsByDeskIdAndSlotIdAndStatusIn(@Param("deskId") Long deskId,
                                               @Param("slotId") Long slotId,
                                               @Param("statuses") Collection<ReservationStatus> statuses);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.user.id = :userId AND r.status IN :statuses")
    Long countByUserIdAndStatusIn(@Param("userId") Long userId,
                                  @Param("statuses") Collection<ReservationStatus> statuses);


    @Query("SELECT COUNT(r) > 0 FROM Reservation r " +
            "WHERE r.user.id = :userId AND r.slot.id = :slotId AND r.status IN :statuses")
    Boolean existsByUserIdAndSlotIdAndStatusIn(@Param("userId") Long userId,
                                               @Param("slotId") Long slotId,
                                               @Param("statuses") Collection<ReservationStatus> statuses);


    @Query(value = "SELECT r FROM Reservation r " +
            "JOIN FETCH r.desk " +
            "JOIN FETCH r.slot s " +
            "JOIN FETCH s.saloon sa " +
            "JOIN FETCH sa.library " +
            "WHERE r.user.id = :userId",
            countQuery = "SELECT COUNT(r) FROM Reservation r WHERE r.user.id = :userId")
    Page<Reservation> findByUserIdWithDetails(@Param("userId") Long userId, Pageable pageable);

    // ReservationRepository
    @Query("SELECT r FROM Reservation r " +
            "JOIN FETCH r.desk " +
            "JOIN FETCH r.slot s JOIN FETCH s.saloon sa JOIN FETCH sa.library " +
            "WHERE r.id = :id")
    Optional<Reservation> findByIdWithDetails(@Param("id") Long id);
}
