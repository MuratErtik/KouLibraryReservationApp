package org.koulibrary.koulibraryreservationapp.dtos.responses;


import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaloonClosureResponse {

    private Long id;


    private Long libraryId;
    private String libraryName;

    private Long saloonId;
    private String saloonName;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String reason;

    private boolean isActive;
}

