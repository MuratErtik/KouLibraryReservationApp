package org.koulibrary.koulibraryreservationapp.dtos.requests;

import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(@NotBlank String email) {}
