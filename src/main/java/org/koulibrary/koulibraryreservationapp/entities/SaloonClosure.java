package org.koulibrary.koulibraryreservationapp.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "saloon_closures", indexes = {
        @Index(name = "idx_closure_saloon_id", columnList = "saloon_id")
})
public class SaloonClosure {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "saloon_closure_seq")
    @SequenceGenerator(name = "saloon_closure_seq", sequenceName = "saloon_closure_id_seq", allocationSize = 1)
    private Long id;

    @Version
    @Column(nullable = false)
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saloon_id", nullable = false)
    private Saloon saloon;

    @Column(name = "start_date_time", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime endDateTime;

    @Column(nullable = false)
    private String reason;
}
