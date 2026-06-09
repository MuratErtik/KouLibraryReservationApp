package org.koulibrary.koulibraryreservationapp.repositories;

import org.koulibrary.koulibraryreservationapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByKeycloakId(String keycloakId);

    Optional<User> findByStudentIdNumber(String studentIdNumber);

    Boolean existsByStudentIdNumber(String studentIdNumber);

    Boolean existsByEmail(String email);
}
