package org.koulibrary.koulibraryreservationapp.dtos.requests;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.koulibrary.koulibraryreservationapp.domains.DeskPolicy;
import org.koulibrary.koulibraryreservationapp.domains.DeskStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateDeskRequest {

    //@NotNull(message = "Desk number cannot be null.")
    @Positive(message = "Desk number must be a positive value.")
    private Integer deskNumber;

    //@NotNull(message = "Desk status is required.")
    private DeskStatus status;

    //@NotNull(message = "Desk policy is required.")
    private DeskPolicy policy;

    //@NotNull(message = "Power socket information must be specified.")
    private Boolean hasPowerSocket;
}
