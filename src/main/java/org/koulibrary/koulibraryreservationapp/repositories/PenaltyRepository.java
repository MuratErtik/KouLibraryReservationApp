package org.koulibrary.koulibraryreservationapp.repositories;

import org.koulibrary.koulibraryreservationapp.domains.PenaltyStatus;
import org.koulibrary.koulibraryreservationapp.entities.Penalty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface PenaltyRepository extends JpaRepository<Penalty, Long> {
    @Query("SELECT COUNT(p) > 0 FROM Penalty p " +
            "WHERE p.user.id = :userId AND p.status = :status AND p.endTime > :now")
    boolean existsActivePenalty(@Param("userId") Long userId,
                                @Param("status") PenaltyStatus status,
                                @Param("now") LocalDateTime now);}
