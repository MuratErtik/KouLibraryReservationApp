package org.koulibrary.koulibraryreservationapp.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLibraryWorkingHoursRequest {
    private LocalTime openingTime;

    private LocalTime closingTime;

}
