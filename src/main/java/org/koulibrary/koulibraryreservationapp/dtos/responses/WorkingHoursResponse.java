package org.koulibrary.koulibraryreservationapp.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkingHoursResponse {

    private Long id;

    private Long libraryId;

    private String libraryName;

    private Long saloonId;
    private String saloonName;

    private DayOfWeek dayOfWeek;

    private LocalTime openingTime;
    private LocalTime closingTime;

    private Boolean isCurrentlyOpen;
}
