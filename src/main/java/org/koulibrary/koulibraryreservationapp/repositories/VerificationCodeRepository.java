package org.koulibrary.koulibraryreservationapp.repositories;

import org.koulibrary.koulibraryreservationapp.domains.VerificationCode;
import org.koulibrary.koulibraryreservationapp.domains.VerificationCodeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    Optional<VerificationCode>
    findTopByUserIdAndTypeAndConsumedAtIsNullOrderByCreatedAtDesc(Long userId, VerificationCodeType type);

    @Modifying
    @Query("UPDATE VerificationCode v SET v.consumedAt = :now " +
            "WHERE v.user.id = :userId AND v.type = :type AND v.consumedAt IS NULL")
    void consumeAllActive(@Param("userId") Long userId,
                          @Param("type") VerificationCodeType type,
                          @Param("now") LocalDateTime now);
}
