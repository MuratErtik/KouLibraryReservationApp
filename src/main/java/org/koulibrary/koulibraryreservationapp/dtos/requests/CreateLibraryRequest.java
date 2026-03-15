package org.koulibrary.koulibraryreservationapp.dtos.requests;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLibraryRequest {

    @NotBlank(message = "Library name cannot be blank")
    @Size(min = 3, max = 100, message = "Library name must be between 3 and 100 characters")
    private String name;

    private String description;

    @NotBlank(message = "Address cannot be blank")
    private String address;

    @NotNull(message = "Maximum active reservations per user is required")
    @Min(value = 1, message = "User must have at least 1 active reservation capacity")
    private Integer maxActiveReservationsPerUser;

    @NotNull(message = "Reservation window in days is required")
    @Min(value = 1, message = "Reservation window must be at least 1 day")
    private Integer reservationWindowInDays;

    @NotNull(message = "Check-in timeout minutes is required")
    @Positive(message = "Check-in timeout must be a positive number")
    private Integer checkInTimeoutMinutes;

    @NotNull(message = "Checkpoint interval minutes is required")
    @Min(value = 15, message = "Checkpoint interval must be at least 15 minutes")
    private Integer checkpointIntervalMinutes;

    @NotNull(message = "Checkpoint grace minutes is required")
    @Positive(message = "Checkpoint grace minutes must be a positive number")
    private Integer checkpointGraceMinutes;

    @NotNull(message = "Penalty block days is required")
    @Min(value = 1, message = "Penalty block days must be at least 1 day")
    private Integer penaltyBlockDays;

    @NotNull(message = "Slot duration minutes is required")
    @Positive(message = "Slot duration timeout must be a positive number")
    private Integer slotDurationMinutes;
}