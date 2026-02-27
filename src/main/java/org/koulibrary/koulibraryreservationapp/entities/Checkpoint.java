package org.koulibrary.koulibraryreservationapp.entities;

import jakarta.persistence.*;
import lombok.*;
import org.koulibrary.koulibraryreservationapp.domains.CheckpointResult;
import org.koulibrary.koulibraryreservationapp.domains.CheckpointType;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "checkpoints",
        indexes = {
                @Index(name = "idx_checkpoint_reservation", columnList = "reservation_id"),
                @Index(name = "idx_checkpoint_time", columnList = "createdAt")
        }
)
public class Checkpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CheckpointType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CheckpointResult result;


    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime respondedAt;
}
