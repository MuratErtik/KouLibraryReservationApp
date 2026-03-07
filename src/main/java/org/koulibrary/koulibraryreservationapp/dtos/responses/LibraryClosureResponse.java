package org.koulibrary.koulibraryreservationapp.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryClosureResponse {

    private Long id;


    private Long libraryId;
    private String libraryName;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String reason;

    private boolean isActive;
}
