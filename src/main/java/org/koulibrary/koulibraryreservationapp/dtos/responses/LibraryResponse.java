package org.koulibrary.koulibraryreservationapp.dtos.responses;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.koulibrary.koulibraryreservationapp.entities.LibraryWorkingHours;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryResponse {

    private Long id;

    private String name;

    private String description;

    private String address;

    private Integer maxActiveReservationsPerUser;

    private Integer reservationWindowInDays;

    private Integer checkInTimeoutMinutes; //after the reservation times how many minutes later user must check in.

    private Integer checkpointIntervalMinutes;//how many minutes later user must scan the checkpoint in a row.

    private Integer checkpointGraceMinutes; //after the checkpoints ready how many minutes later user must scan the checkpoint.

    private Integer penaltyBlockDays;

    private Integer slotDurationMinutes;

    private List<LibraryWorkingHoursResponseForLibrary> workingHours;



}
