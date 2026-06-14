package org.koulibrary.koulibraryreservationapp.services;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.koulibrary.koulibraryreservationapp.domains.UserStatus;
import org.koulibrary.koulibraryreservationapp.dtos.requests.CreateAdminRequest;
import org.koulibrary.koulibraryreservationapp.dtos.requests.UpdateMeRequest;
import org.koulibrary.koulibraryreservationapp.dtos.responses.PageResponse;
import org.koulibrary.koulibraryreservationapp.dtos.responses.UserResponse;
import org.koulibrary.koulibraryreservationapp.entities.User;
import org.koulibrary.koulibraryreservationapp.exceptions.InvalidCredentialsException;
import org.koulibrary.koulibraryreservationapp.exceptions.UserNotFoundException;
import org.koulibrary.koulibraryreservationapp.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final KeycloakAdminService keycloakAdminService;

    private final KeycloakAuthService keycloakAuthService;


    @Transactional(readOnly = true)
    public UserResponse getByKeycloakId(String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId)
                .map(this::toResponse)
                .orElseThrow(() -> new UserNotFoundException("User not found for Keycloak ID: " + keycloakId));
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .studentIdNumber(user.getStudentIdNumber())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .userRole(user.getUserRole())
                .userStatus(user.getUserStatus())
                .build();
    }


    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        List<UserResponse> content = users.getContent().stream().map(this::toResponse).toList();
        return PageResponse.<UserResponse>builder()
                .content(content)
                .pageNumber(users.getNumber())
                .pageSize(users.getSize())
                .totalElements(users.getTotalElements())
                .totalPages(users.getTotalPages())
                .isLast(users.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public UserResponse updateStatus(Long id, UserStatus newStatus) {
        User user = findOrThrow(id);
        user.setUserStatus(newStatus);
        keycloakAdminService.setEnabled(user.getKeycloakId(), newStatus == UserStatus.ACTIVE);
        return toResponse(user);
    }

    @Transactional
    public void softDelete(Long id) {
        User user = findOrThrow(id);
        user.setUserStatus(UserStatus.DELETED);
        keycloakAdminService.setEnabled(user.getKeycloakId(), false);
    }

    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id " + id));
    }



    public String createAdmin(CreateAdminRequest req) {
        keycloakAdminService.createUser(
                req.getUsername(), req.getEmail(), req.getFirstName(),
                req.getLastName(), req.getPassword(), "ADMIN");

        return "Admin created successfully with email: " + req.getEmail();
    }


    @Transactional
    public UserResponse updateMe(String keycloakSub, UpdateMeRequest req) {
        User user = userRepository.findByKeycloakId(keycloakSub)
                .orElseThrow(() -> new UserNotFoundException("User not found for Keycloak ID: " + keycloakSub));

        if (req.firstName() != null) user.setFirstName(req.firstName());
        if (req.lastName() != null)  user.setLastName(req.lastName());

        keycloakAdminService.updateProfile(user.getKeycloakId(), user.getFirstName(), user.getLastName());
        return toResponse(user);
    }

    @Transactional
    public void changePassword(String keycloakSub, String currentPassword, String newPassword) {
        User user = userRepository.findByKeycloakId(keycloakSub)
                .orElseThrow(() -> new UserNotFoundException("User not found for Keycloak ID: " + keycloakSub));

        // verify current password by attempting a login (username = studentIdNumber)
        try {
            keycloakAuthService.login(user.getStudentIdNumber(), currentPassword);
        } catch (InvalidCredentialsException ex) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        keycloakAdminService.resetPassword(user.getKeycloakId(), newPassword);
        keycloakAdminService.logoutAllSessions(user.getKeycloakId()); // force re-login everywhere
    }
}