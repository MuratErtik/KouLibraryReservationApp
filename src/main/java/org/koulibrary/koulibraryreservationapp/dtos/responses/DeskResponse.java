package org.koulibrary.koulibraryreservationapp.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.koulibrary.koulibraryreservationapp.domains.DeskPolicy;
import org.koulibrary.koulibraryreservationapp.domains.DeskStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeskResponse {
    private Long id;

    private Integer deskNumber;

    private DeskStatus status;

    private DeskPolicy policy;

    private Boolean hasPowerSocket;

    // Saloon Info
    private Long saloonId;
    private String saloonName;

    private Long libraryId;
    private String libraryName;

    // QR Code Info
    private Long qrCodeId;
}
