package org.koulibrary.koulibraryreservationapp.entities;



import jakarta.persistence.*;
import lombok.*;
import org.koulibrary.koulibraryreservationapp.domains.ReservationStatus;
import org.koulibrary.koulibraryreservationapp.domains.StatusChangeReason;

import java.time.LocalDateTime;


@Entity
@Table(name = "reservation_status_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationStatusLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    // if it is null, that means the system changed something
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private User changedBy;

    @Enumerated(EnumType.STRING)
    private ReservationStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus toStatus;

    @Enumerated(EnumType.STRING)
    private StatusChangeReason reason;

    @Column(length = 500)
    private String note;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime changedAt = LocalDateTime.now();
}
