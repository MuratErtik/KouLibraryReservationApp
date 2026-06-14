package org.koulibrary.koulibraryreservationapp.dtos.requests;

import jakarta.validation.constraints.NotNull;

public record JoinWaitlistRequest(@NotNull Long slotId) {}

