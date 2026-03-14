package org.koulibrary.koulibraryreservationapp.entities;
import jakarta.persistence.*;
import lombok.*;
import java.time.DayOfWeek;
import java.time.LocalTime;


@Entity
@Table(
        name = "library_time_slots",
        uniqueConstraints = @UniqueConstraint(columnNames = {"library_id", "day_of_week", "start_time"})
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibraryTimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library_id", nullable = false)
    private Library library;


    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
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
