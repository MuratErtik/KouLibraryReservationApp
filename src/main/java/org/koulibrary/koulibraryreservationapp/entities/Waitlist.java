package org.koulibrary.koulibraryreservationapp.entities;



import jakarta.persistence.*;
import lombok.*;
import org.koulibrary.koulibraryreservationapp.domains.WaitlistStatus;

import java.time.LocalDateTime;


@Entity
@Table(name = "waitlist")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Waitlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "desk_id", nullable = false)
    private Desk desk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    private LibraryTimeSlot slot;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime requestedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WaitlistStatus status;

    private LocalDateTime notifiedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;
}



/**
 * [NEW] Masa doluyken bekleme listesi.
 *
 * Akış:
 *   1. Kullanıcı dolu masayı seçer → Waitlist kaydı WAITING olarak oluşur.
 *   2. Masa boşalınca (rezervasyon COMPLETED/CANCELLED) sıradaki kayıt NOTIFIED olur.
 *   3. Kullanıcıya bildirim gönderilir, expiresAt süresi içinde rezervasyon yapabilir.
 *   4. Rezervasyon yapılırsa → CONVERTED, yapılmazsa → EXPIRED.
 */