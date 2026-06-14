package org.koulibrary.koulibraryreservationapp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.dtos.requests.ChangePasswordRequest;
import org.koulibrary.koulibraryreservationapp.dtos.requests.CreateAdminRequest;
import org.koulibrary.koulibraryreservationapp.dtos.requests.UpdateMeRequest;
import org.koulibrary.koulibraryreservationapp.dtos.requests.UpdateUserStatusRequest;
import org.koulibrary.koulibraryreservationapp.dtos.responses.PageResponse;
import org.koulibrary.koulibraryreservationapp.dtos.responses.UserResponse;
import org.koulibrary.koulibraryreservationapp.services.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.koulibrary.koulibraryreservationapp.configs.RestApisConf.USERCONTROLLER;

@RestController
@RequestMapping(USERCONTROLLER)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(userService.getByKeycloakId(jwt.getSubject()));
    }

    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> getAll(
            @PageableDefault(size = 10, sort = "studentIdNumber") Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        return ResponseEntity.ok(userService.updateStatus(id, request.getStatus()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/admins")
    public ResponseEntity<String> createAdmin(@Valid @RequestBody CreateAdminRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createAdmin(request));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateMe(@AuthenticationPrincipal Jwt jwt,
                                                 @Valid @RequestBody UpdateMeRequest req) {
        return ResponseEntity.ok(userService.updateMe(jwt.getSubject(), req));
    }

    @PostMapping("/me/password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal Jwt jwt,
                                               @Valid @RequestBody ChangePasswordRequest req) {
        userService.changePassword(jwt.getSubject(), req.currentPassword(), req.newPassword());
        return ResponseEntity.noContent().build();
    }
}
