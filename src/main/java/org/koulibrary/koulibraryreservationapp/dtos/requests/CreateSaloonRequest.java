package org.koulibrary.koulibraryreservationapp.dtos.requests;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSaloonRequest {

    @NotBlank(message = "Saloon name cannot be blank")
    @Size(min = 2, max = 100, message = "Saloon name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Floor information is required")
    private Integer floor;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;


    //  set as default  SaloonStatus.AVAILABLE

}
