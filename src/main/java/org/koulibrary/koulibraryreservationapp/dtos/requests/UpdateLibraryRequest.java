package org.koulibrary.koulibraryreservationapp.dtos.requests;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLibraryRequest {

    @Size(min = 3, max = 100, message = "Library name must be between 3 and 100 characters")
    private String name;

    private String description;

    private String address;

    @Min(value = 1, message = "User must have at least 1 active reservation capacity")
    private Integer maxActiveReservationsPerUser;

    @Min(value = 1, message = "Reservation window must be at least 1 day")
    private Integer reservationWindowInDays;

    @Positive(message = "Check-in timeout must be a positive number")
    private Integer checkInTimeoutMinutes;

    @Min(value = 15, message = "Checkpoint interval must be at least 15 minutes")
    private Integer checkpointIntervalMinutes;

    @Positive(message = "Checkpoint grace minutes must be a positive number")
    private Integer checkpointGraceMinutes;

    @Min(value = 1, message = "Penalty block days must be at least 1 day")
    private Integer penaltyBlockDays;

}
