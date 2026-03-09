package org.koulibrary.koulibraryreservationapp.dtos.requests;


import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLibraryWorkingHourRequest {

    @NotNull(message = "Day of week cannot be null")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Opening time cannot be null")
    private LocalTime openingTime;

    @NotNull(message = "Closing time cannot be null")
    private LocalTime closingTime;

}
