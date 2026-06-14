package org.koulibrary.koulibraryreservationapp.repositories;

import jakarta.persistence.LockModeType;
import org.koulibrary.koulibraryreservationapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByKeycloakId(String keycloakId);

    Optional<User> findByStudentIdNumber(String studentIdNumber);

    Boolean existsByStudentIdNumber(String studentIdNumber);

    Boolean existsByEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u WHERE u.keycloakId = :keycloakId")
    Optional<User> findByKeycloakIdForUpdate(@Param("keycloakId") String keycloakId);

    Optional<User> findByEmail(String email);
}
