package org.koulibrary.koulibraryreservationapp.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.koulibrary.koulibraryreservationapp.domains.SaloonStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaloonResponse {
    private Long id;

    private String name;

    private Integer floor;

    private Integer capacity;

    private SaloonStatus status;

    private Long libraryId;
    private String libraryName;

    //private Integer currentDeskCount;
}
