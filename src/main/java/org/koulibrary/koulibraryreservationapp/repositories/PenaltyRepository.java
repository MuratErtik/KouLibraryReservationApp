package org.koulibrary.koulibraryreservationapp.repositories;

import org.koulibrary.koulibraryreservationapp.domains.PenaltyStatus;
import org.koulibrary.koulibraryreservationapp.entities.Penalty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PenaltyRepository extends JpaRepository<Penalty, Long> {
    @Query("SELECT COUNT(p) > 0 FROM Penalty p " +
            "WHERE p.user.id = :userId AND p.status = :status AND p.endTime > :now")
    boolean existsActivePenalty(@Param("userId") Long userId,
                                @Param("status") PenaltyStatus status,
                                @Param("now") LocalDateTime now);


    @Query("SELECT COUNT(p) > 0 FROM Penalty p WHERE p.user.id = :userId " +
            "AND p.status IN :statuses AND p.endTime > :now")
    boolean existsBlockingPenalty(@Param("userId") Long userId,
                                  @Param("statuses") Collection<PenaltyStatus> statuses,
                                  @Param("now") LocalDateTime now);

    Page<Penalty> findByUserId(Long userId, Pageable pageable);

    @Query(value = "SELECT p FROM Penalty p JOIN FETCH p.user u " +
            "WHERE (:userId IS NULL OR u.id = :userId) AND (:status IS NULL OR p.status = :status)",
            countQuery = "SELECT COUNT(p) FROM Penalty p " +
                    "WHERE (:userId IS NULL OR p.user.id = :userId) AND (:status IS NULL OR p.status = :status)")
    Page<Penalty> findForAdmin(@Param("userId") Long userId,
                               @Param("status") PenaltyStatus status, Pageable pageable);

    @Query("SELECT p FROM Penalty p JOIN FETCH p.user WHERE p.id = :id")
    Optional<Penalty> findByIdWithUser(@Param("id") Long id);


    List<Penalty> findByStatusAndEndTimeLessThanEqual(PenaltyStatus status, LocalDateTime now);

}
