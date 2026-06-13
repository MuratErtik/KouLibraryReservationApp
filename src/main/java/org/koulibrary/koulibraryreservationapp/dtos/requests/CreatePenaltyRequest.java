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
public class CreatePenaltyRequest {
    @NotNull
    private Long userId;
    @NotNull @Min(1) private Integer days;
    @Size(max = 500) private String description;
}