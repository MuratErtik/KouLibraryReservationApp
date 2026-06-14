package org.koulibrary.koulibraryreservationapp.dtos.requests;

import jakarta.validation.constraints.NotBlank;

public record VerifyEmailRequest(@NotBlank String email, @NotBlank String code) {}

