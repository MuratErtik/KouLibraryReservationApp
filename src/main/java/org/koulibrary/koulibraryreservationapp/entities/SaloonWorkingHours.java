package org.koulibrary.koulibraryreservationapp.entities;




import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;


@Entity
@Table(
        name = "saloon_working_hours",
        uniqueConstraints = @UniqueConstraint(columnNames = {"saloon_id", "day_of_week"})
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SaloonWorkingHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saloon_id", nullable = false)
    private Saloon saloon;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime openingTime;

    @Column(nullable = false)
    private LocalTime closingTime;

    @Column(nullable = false)
    private Boolean isClosed;
}


/**
 * Salona özel çalışma saatleri override.
 * Eğer bu tabloda kayıt varsa, kütüphanenin saatlerini ezer.
 * Kayıt yoksa sistem library_working_hours'a düşer.
 *
 * Servis katmanında öncelik sırası:
 *   1. SaloonWorkingHours (varsa)
 *   2. LibraryWorkingHours (fallback)
 */
