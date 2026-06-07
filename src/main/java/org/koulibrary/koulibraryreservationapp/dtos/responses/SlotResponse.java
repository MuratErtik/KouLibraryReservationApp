package org.koulibrary.koulibraryreservationapp.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotResponse {

    Long slotId;
    LocalDate date;
    LocalTime startTime;
    LocalTime endTime;
    Boolean isAvailable;
    Long availableDeskCount;
}
