package org.koulibrary.koulibraryreservationapp.services;

import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.domains.UserRole;
import org.koulibrary.koulibraryreservationapp.domains.UserStatus;
import org.koulibrary.koulibraryreservationapp.dtos.requests.RegisterRequest;
import org.koulibrary.koulibraryreservationapp.dtos.responses.RegisterResponse;
import org.koulibrary.koulibraryreservationapp.entities.User;
import org.koulibrary.koulibraryreservationapp.exceptions.UserAlreadyExistsException;
import org.koulibrary.koulibraryreservationapp.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final KeycloakAdminService keycloakAdminService;

    @Transactional
    public RegisterResponse register(RegisterRequest req) {

        if (userRepository.existsByStudentIdNumber(req.getStudentIdNumber())) {
            throw new UserAlreadyExistsException("Student id already registered: " + req.getStudentIdNumber());
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered: " + req.getEmail());
        }

        String keycloakId = keycloakAdminService.createUser(
                req.getStudentIdNumber(),
                req.getEmail(),
                req.getFirstName(),
                req.getLastName(),
                req.getPassword()
        );

        try {
            User user = User.builder()
                    .keycloakId(keycloakId)
                    .studentIdNumber(req.getStudentIdNumber())
                    .firstName(req.getFirstName())
                    .lastName(req.getLastName())
                    .email(req.getEmail())
                    .userRole(UserRole.USER)
                    .userStatus(UserStatus.ACTIVE)
                    .build();

            User saved = userRepository.save(user);

            return new RegisterResponse(saved.getId(), saved.getStudentIdNumber(), "Registered successfully");

        } catch (RuntimeException ex) {
            //for consistency between db and keycloak
            keycloakAdminService.deleteUser(keycloakId);
            throw ex;
        }
    }
}