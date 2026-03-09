package org.koulibrary.koulibraryreservationapp.entities;

import jakarta.persistence.*;
import lombok.*;
import org.koulibrary.koulibraryreservationapp.domains.DeskPolicy;
import org.koulibrary.koulibraryreservationapp.domains.DeskStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "desks",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"saloon_id", "desk_number"})
        })
public class Desk {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Version
    private Long version;

    @Column(name = "desk_number", nullable = false)
    private Integer deskNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeskPolicy policy;

    @Column(nullable = false)
    private boolean hasPowerSocket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saloon_id", nullable = false)
    private Saloon saloon;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "library_id", nullable = false)
//    private Library library;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "qr_code_id", unique = true)
    private QrCode qrCode;

    @Transient
    public Library getLibrary() {
        return saloon != null ? saloon.getLibrary() : null;
    }
}
