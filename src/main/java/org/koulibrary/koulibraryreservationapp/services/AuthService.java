package org.koulibrary.koulibraryreservationapp.services;

import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.domains.UserRole;
import org.koulibrary.koulibraryreservationapp.domains.UserStatus;
import org.koulibrary.koulibraryreservationapp.domains.VerificationCodeType;
import org.koulibrary.koulibraryreservationapp.dtos.requests.RegisterRequest;
import org.koulibrary.koulibraryreservationapp.dtos.responses.RegisterResponse;
import org.koulibrary.koulibraryreservationapp.entities.User;
import org.koulibrary.koulibraryreservationapp.exceptions.InvalidSchoolEmailException;
import org.koulibrary.koulibraryreservationapp.exceptions.InvalidVerificationCodeException;
import org.koulibrary.koulibraryreservationapp.exceptions.UserAlreadyExistsException;
import org.koulibrary.koulibraryreservationapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final KeycloakAdminService keycloakAdminService;

    private final VerificationService verificationService;

    @Value("${app.allowed-email-domain:kocaeli.edu.tr}")
    private String allowedDomain;

    @Transactional
    public RegisterResponse register(RegisterRequest req) {

        String email = req.getEmail() == null ? "" : req.getEmail().trim().toLowerCase();
        validateSchoolEmail(email, req.getStudentIdNumber());

        if (userRepository.existsByStudentIdNumber(req.getStudentIdNumber())) {
            throw new UserAlreadyExistsException("Student id already registered: " + req.getStudentIdNumber());
        }
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email already registered: " + email);
        }

        String keycloakId = keycloakAdminService.createUser(
                req.getStudentIdNumber(), email, req.getFirstName(),
                req.getLastName(), req.getPassword(), "USER");

        try {
            User user = User.builder()
                    .keycloakId(keycloakId)
                    .studentIdNumber(req.getStudentIdNumber())
                    .firstName(req.getFirstName())
                    .lastName(req.getLastName())
                    .email(email)
                    .userRole(UserRole.USER)
                    .userStatus(UserStatus.PENDING_VERIFICATION)
                    .build();

            User saved = userRepository.save(user);

            verificationService.issueAndSend(saved, VerificationCodeType.EMAIL_VERIFICATION); // send code

            return new RegisterResponse(saved.getId(), saved.getStudentIdNumber(),
                    "Registered. A verification code has been sent to your school email.");

        } catch (RuntimeException ex) {
            keycloakAdminService.deleteUser(keycloakId);
            throw ex;
        }
    }

    @Transactional
    public void verifyEmail(String email, String code) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new InvalidVerificationCodeException("Invalid or expired code"));

        if (user.getUserStatus() == UserStatus.ACTIVE) return; // idempotent

        verificationService.consumeOrThrow(user, VerificationCodeType.EMAIL_VERIFICATION, code);
        user.setUserStatus(UserStatus.ACTIVE);
    }

    @Transactional
    public void resendVerification(String email) {
        User user = userRepository.findByEmail(email.trim().toLowerCase()).orElse(null);
        if (user == null || user.getUserStatus() != UserStatus.PENDING_VERIFICATION) return; // silent (no enumeration)
        verificationService.issueAndSend(user, VerificationCodeType.EMAIL_VERIFICATION);
    }

    private void validateSchoolEmail(String email, String studentIdNumber) {
        String[] parts = email.split("@", -1);
        boolean validFormat = parts.length == 2
                && parts[0].matches("\\d{9}")
                && parts[1].equals(allowedDomain);
        if (!validFormat) {
            throw new InvalidSchoolEmailException(
                    "Email must be a 9-digit student number @" + allowedDomain);
        }
        if (!parts[0].equals(studentIdNumber)) {
            throw new InvalidSchoolEmailException("Email prefix must match your student number");
        }
    }

    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email.trim().toLowerCase()).orElse(null);
        if (user == null) return; // always succeed → no email enumeration
        verificationService.issueAndSend(user, VerificationCodeType.PASSWORD_RESET);
    }

    @Transactional
    public void resetPassword(String email, String code, String newPassword) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new InvalidVerificationCodeException("Invalid or expired code"));

        verificationService.consumeOrThrow(user, VerificationCodeType.PASSWORD_RESET, code);

        keycloakAdminService.resetPassword(user.getKeycloakId(), newPassword);
        keycloakAdminService.logoutAllSessions(user.getKeycloakId());
    }
}