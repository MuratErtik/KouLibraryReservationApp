package org.koulibrary.koulibraryreservationapp.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.koulibrary.koulibraryreservationapp.domains.WaitlistStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaitlistResponse {
    private Long id;
    private Long slotId;
    private String saloonName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private WaitlistStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime notifiedAt;
    private LocalDateTime expiresAt;
}