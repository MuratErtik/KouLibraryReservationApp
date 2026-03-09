package org.koulibrary.koulibraryreservationapp.dtos.requests;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.koulibrary.koulibraryreservationapp.domains.SaloonStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSaloonRequest {

    @Size(min = 2, max = 100, message = "Saloon name must be between 2 and 100 characters")
    private String name;

    private Integer floor;

    //the capacity of the saloon should not update unless tables add or remove!!!
//    @Min(value = 1, message = "Capacity must be at least 1")
//    private Integer capacity;

    @NotNull(message = "Invalid Status will not be accepted!")
    private SaloonStatus status;
}
