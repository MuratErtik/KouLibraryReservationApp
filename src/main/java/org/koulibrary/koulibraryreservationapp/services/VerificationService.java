package org.koulibrary.koulibraryreservationapp.services;

import lombok.RequiredArgsConstructor;
import org.koulibrary.koulibraryreservationapp.domains.AuthMessages;
import org.koulibrary.koulibraryreservationapp.domains.VerificationCode;
import org.koulibrary.koulibraryreservationapp.domains.VerificationCodeType;
import org.koulibrary.koulibraryreservationapp.dtos.responses.NotificationContent;
import org.koulibrary.koulibraryreservationapp.entities.User;
import org.koulibrary.koulibraryreservationapp.exceptions.InvalidVerificationCodeException;
import org.koulibrary.koulibraryreservationapp.repositories.VerificationCodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import static org.koulibrary.koulibraryreservationapp.configs.TimeConfig.APP_ZONE;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final VerificationCodeRepository repository;
    private final EmailService emailService;

    private static final int CODE_TTL_MINUTES = 15;
    private final SecureRandom random = new SecureRandom();

    @Transactional
    public void issueAndSend(User user, VerificationCodeType type) {
        LocalDateTime now = LocalDateTime.now(APP_ZONE);
        repository.consumeAllActive(user.getId(), type, now); // invalidate previous codes

        String code = String.format("%06d", random.nextInt(1_000_000));
        repository.save(VerificationCode.builder()
                .user(user).code(code).type(type)
                .expiresAt(now.plusMinutes(CODE_TTL_MINUTES))
                .createdAt(now)
                .build());

        NotificationContent c = (type == VerificationCodeType.EMAIL_VERIFICATION)
                ? AuthMessages.emailVerification(code, CODE_TTL_MINUTES)
                : AuthMessages.passwordReset(code, CODE_TTL_MINUTES);
        emailService.send(user.getEmail(), c.title(), c.body());
    }

    @Transactional
    public void consumeOrThrow(User user, VerificationCodeType type, String code) {
        LocalDateTime now = LocalDateTime.now(APP_ZONE);
        VerificationCode vc = repository
                .findTopByUserIdAndTypeAndConsumedAtIsNullOrderByCreatedAtDesc(user.getId(), type)
                .orElseThrow(() -> new InvalidVerificationCodeException("Invalid or expired code"));

        if (vc.getExpiresAt().isBefore(now))   throw new InvalidVerificationCodeException("Code expired");
        if (!vc.getCode().equals(code))        throw new InvalidVerificationCodeException("Invalid code");

        vc.setConsumedAt(now);
    }
}
