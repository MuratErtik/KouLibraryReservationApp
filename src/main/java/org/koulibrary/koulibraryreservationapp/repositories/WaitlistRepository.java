package org.koulibrary.koulibraryreservationapp.repositories;

import org.koulibrary.koulibraryreservationapp.domains.WaitlistStatus;
import org.koulibrary.koulibraryreservationapp.entities.Waitlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface WaitlistRepository extends JpaRepository<Waitlist, Long> {

    Boolean existsByUserIdAndSlotIdAndStatusIn(Long userId, Long slotId, Collection<WaitlistStatus> statuses);
    Optional<Waitlist> findByUserIdAndSlotIdAndStatusIn(Long userId, Long slotId, Collection<WaitlistStatus> statuses);
    Long countByUserIdAndStatusIn(Long userId, Collection<WaitlistStatus> statuses);

    Page<Waitlist> findByUserId(Long userId, Pageable pageable);
    Optional<Waitlist> findByIdAndUserId(Long id, Long userId);

    // job
    List<Waitlist> findByStatusInAndExpiresAtLessThanEqual(Collection<WaitlistStatus> statuses, LocalDateTime now);

    @Query("SELECT DISTINCT w.slot.id FROM Waitlist w WHERE w.status = :status")
    List<Long> findSlotIdsByStatus(@Param("status") WaitlistStatus status);

    @Query("SELECT w FROM Waitlist w JOIN FETCH w.user " +
            "WHERE w.slot.id = :slotId AND w.status IN :statuses ORDER BY w.requestedAt ASC")
    List<Waitlist> findBySlotIdAndStatusInOrderByRequestedAt(@Param("slotId") Long slotId,
                                                             @Param("statuses") Collection<WaitlistStatus> statuses);
}
