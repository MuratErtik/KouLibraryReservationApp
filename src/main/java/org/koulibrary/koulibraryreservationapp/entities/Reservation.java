package org.koulibrary.koulibraryreservationapp.entities;

import jakarta.persistence.*;
import lombok.*;
import org.koulibrary.koulibraryreservationapp.domains.ReservationStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "reservations",
        indexes = {
                @Index(name = "idx_res_user", columnList = "user_id"),
                @Index(name = "idx_res_desk", columnList = "desk_id"),
                @Index(name = "idx_res_status", columnList = "status")
        }
)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "desk_id", nullable = false)
    private Desk desk;


    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    private LocalDateTime checkInTime;

    private LocalDateTime lastCheckpointTime;

    private LocalDateTime reservationTime;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;


    @Version
    private Long version;
}
