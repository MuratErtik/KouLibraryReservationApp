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
public class LibraryWorkingHoursResponseForLibrary {

    private Long id;

    private DayOfWeek dayOfWeek;

    private LocalTime openingTime;
    private LocalTime closingTime;


}
