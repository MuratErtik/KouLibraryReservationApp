package org.koulibrary.koulibraryreservationapp.dtos.requests;

import jakarta.validation.constraints.*;

public record ResetPasswordRequest(
        @NotBlank String email,
        @NotBlank String code,
        @NotBlank @Size(min = 8, max = 72) String newPassword) {}
