package org.koulibrary.koulibraryreservationapp.dtos.requests;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLibraryClosureRequest {


    @NotNull(message = "Start date and time cannot be null")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDateTime startDateTime;

    @NotNull(message = "End date and time cannot be null")
    private LocalDateTime endDateTime;

    @NotBlank(message = "Reason for closure is required")
    @Size(min = 5, max = 255, message = "Reason must be between 5 and 255 characters")
    private String reason;
}
