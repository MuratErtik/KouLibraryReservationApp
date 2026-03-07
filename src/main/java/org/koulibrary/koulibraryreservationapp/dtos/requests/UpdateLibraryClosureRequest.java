package org.koulibrary.koulibraryreservationapp.dtos.requests;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLibraryClosureRequest {

    @FutureOrPresent(message = "Updated start date must be today or in the future")
    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    @Size(min = 5, max = 255, message = "Reason must be between 5 and 255 characters")
    private String reason;
}
