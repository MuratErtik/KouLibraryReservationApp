package org.koulibrary.koulibraryreservationapp.dtos.responses;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLibraryResponse {

    private String message;

    private Long id;

}