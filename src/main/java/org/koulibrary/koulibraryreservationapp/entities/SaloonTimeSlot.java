package org.koulibrary.koulibraryreservationapp.entities;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;


@Entity
@Table(
        name = "saloon_time_slots",
        uniqueConstraints = @UniqueConstraint(columnNames = {"saloon_id", "date", "start_time"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaloonTimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saloon_id", nullable = false)
    private Saloon saloon;


//    @Enumerated(EnumType.STRING)
//    @Column(name = "day_of_week", nullable = false)
//    private DayOfWeek dayOfWeek;


    //instead of day of week
    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "start_time", columnDefinition = "TIME",nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", columnDefinition = "TIME",nullable = false)
    private LocalTime endTime;

    //If this field is equal to false, then it is close to making a new appointment
    //using WHERE is_available = true filter at reservation queries
    @Column(nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;

}

/**
 * Kütüphane için önceden tanımlanmış zaman bloklarını temsil eder.
 *
 *
 *   - Rezervasyon sistemi serbest timestamp yerine bu slotlara bağlı çalışır.
 *   - Çakışma kontrolü basitleşir: aynı desk + aynı slot = max 1 rezervasyon.
 *   - UI tarafında "boş slot seç" akışı doğrudan bu tablodan beslenir.
 *
 *
 *   - Library kaydedildiğinde veya slotDurationMinutes güncellendiğinde,
 *     LibraryTimeSlotGeneratorService her gün için slotları otomatik üretir.
 *   - Örn: openingTime=09:00, closingTime=21:00, slotDuration=120 dk
 *     → 09:00-11:00, 11:00-13:00, 13:00-15:00, 15:00-17:00,
 *       17:00-19:00, 19:00-21:00  (6 slot/gün)
 *
 * Özel kapatma:
 *   - isAvailable=false ile belirli bir slot manuel olarak devre dışı bırakılabilir.
 *   - Planlı etkinlik, temizlik vb. durumlar için kullanılır.
 */
