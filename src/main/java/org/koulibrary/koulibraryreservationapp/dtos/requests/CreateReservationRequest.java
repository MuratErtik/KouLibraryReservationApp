package org.koulibrary.koulibraryreservationapp.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationRequest {

    @NotNull(message = "Slot ID cannot be null")
    private Long slotId;

    @NotNull(message = "Desk ID cannot be null")
    private Long deskId;
}