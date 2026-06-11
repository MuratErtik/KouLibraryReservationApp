package org.koulibrary.koulibraryreservationapp.dtos.requests;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelReservationRequest {

    @Size(max = 500, message = "The cancellation reason cannot exceed 500 characters")
    private String reason;
}