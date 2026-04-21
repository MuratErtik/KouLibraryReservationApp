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
public class SaloonWorkingHoursResponse {

    private Long id;

    private Long libraryId;

    private String libraryName;

    private Long saloonId;
    private String saloonName;

    private DayOfWeek dayOfWeek;

    private LocalTime openingTime;
    private LocalTime closingTime;

    // if the library closes all day or has a closure
    private Boolean isCurrentlyOpen;
}
