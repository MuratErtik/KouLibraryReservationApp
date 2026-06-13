package org.koulibrary.koulibraryreservationapp.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.koulibrary.koulibraryreservationapp.domains.PenaltyReason;
import org.koulibrary.koulibraryreservationapp.domains.PenaltyStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PenaltyResponse {
    private Long id;
    private PenaltyReason reason;
    private PenaltyStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;
    private Long reservationId;
    private boolean active;         // (ACTIVE|APPEALED) && endTime>now
    private Long userId;
    private String studentIdNumber;
    private String userFullName;
}
