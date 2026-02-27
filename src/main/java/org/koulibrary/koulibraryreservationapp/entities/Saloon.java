package org.koulibrary.koulibraryreservationapp.entities;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.koulibrary.koulibraryreservationapp.domains.RoomStatus;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Saloon {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status;

    private Integer floor;

    private Integer capacity;

    private LocalTime openingTime;

    private LocalTime closingTime;

    @OneToMany(mappedBy = "saloon", cascade = CascadeType.ALL)
    private Set<Desk> tables;
}
