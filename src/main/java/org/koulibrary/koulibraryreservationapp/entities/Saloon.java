package org.koulibrary.koulibraryreservationapp.entities;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.koulibrary.koulibraryreservationapp.domains.SaloonStatus;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(indexes = {
        @Index(name = "idx_saloon_library_id", columnList = "library_id")
})
public class Saloon {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaloonStatus status;

    private Integer floor;

    private Integer capacity;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library_id", nullable = false)
    private Library library;

    //those fields, which is the below, will fetch from the library entity

    //private LocalTime openingTime;

    //private LocalTime closingTime;

    @OneToMany(mappedBy = "saloon", cascade = CascadeType.ALL)
    private Set<Desk> tables;
}
