package org.koulibrary.koulibraryreservationapp.entities;

import jakarta.persistence.*;
import lombok.*;
import org.koulibrary.koulibraryreservationapp.domains.PenaltyReason;
import org.koulibrary.koulibraryreservationapp.domains.PenaltyStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "penalties",
        indexes = {
                @Index(name = "idx_penalty_user", columnList = "user_id"),
                @Index(name = "idx_penalty_status", columnList = "status")
        }
)
public class Penalty {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;



    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PenaltyReason reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PenaltyStatus status;



    @Column(nullable = false, updatable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;



    @Column(length = 500)
    private String description;
}
