package org.koulibrary.koulibraryreservationapp.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"library_id", "dayOfWeek"}
                )
        }
)
public class LibraryWorkingHours {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library_id", nullable = false)
    private Library library;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "opening_time", columnDefinition = "TIME", nullable = false)
    private LocalTime openingTime;

    @Column(name = "closing_time", columnDefinition = "TIME", nullable = false)
    private LocalTime closingTime;

    private Boolean isClosed;


}
