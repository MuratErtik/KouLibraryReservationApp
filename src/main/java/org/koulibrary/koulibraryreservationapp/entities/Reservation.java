package org.koulibrary.koulibraryreservationapp.entities;

import jakarta.persistence.*;
import lombok.*;
import org.koulibrary.koulibraryreservationapp.domains.ReservationStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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


    @Version
    private Long version;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "desk_id", nullable = false)
    private Desk desk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    private LibraryTimeSlot slot;


    // it is equal to ReservationDate+HourOfSlot
    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;



    private LocalDateTime checkInTime;

    private LocalDateTime lastCheckpointTime;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime reservationTime=LocalDateTime.now();


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(length = 500)
    private String cancellationReason;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Checkpoint> checkpoints = new ArrayList<>();

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReservationStatusLog> statusLogs = new ArrayList<>();


}
