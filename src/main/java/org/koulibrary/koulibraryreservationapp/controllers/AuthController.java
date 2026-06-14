package org.koulibrary.koulibraryreservationapp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.dtos.requests.*;
import org.koulibrary.koulibraryreservationapp.dtos.responses.RegisterResponse;
import org.koulibrary.koulibraryreservationapp.dtos.responses.TokenResponse;
import org.koulibrary.koulibraryreservationapp.services.AuthService;
import org.koulibrary.koulibraryreservationapp.services.KeycloakAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.koulibrary.koulibraryreservationapp.configs.RestApisConf.AUTHCONTROLLER;

@RestController
@RequestMapping(AUTHCONTROLLER)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final KeycloakAuthService keycloakAuthService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(keycloakAuthService.login(req.getUsername(), req.getPassword()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest req) {
        return ResponseEntity.ok(keycloakAuthService.refresh(req.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest req) {
        keycloakAuthService.logout(req.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@Valid @RequestBody VerifyEmailRequest req) {
        authService.verifyEmail(req.email(), req.code());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerification(@Valid @RequestBody ResendVerificationRequest req) {
        authService.resendVerification(req.email());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest req) {
        authService.forgotPassword(req.email());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest req) {
        authService.resetPassword(req.email(), req.code(), req.newPassword());
        return ResponseEntity.noContent().build();
    }
}
