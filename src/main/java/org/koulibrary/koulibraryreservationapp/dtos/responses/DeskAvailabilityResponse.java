package org.koulibrary.koulibraryreservationapp.dtos.responses;

import lombok.*;
import org.koulibrary.koulibraryreservationapp.domains.DeskAvailability;
import org.koulibrary.koulibraryreservationapp.domains.DeskPolicy;
import org.koulibrary.koulibraryreservationapp.domains.DeskStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeskAvailabilityResponse {

    Long deskId;
    Integer deskNumber;
    Boolean hasPowerSocket;
    DeskPolicy policy;
    //DeskStatus status;
    DeskAvailability availability;


}
