package org.koulibrary.koulibraryreservationapp.entities;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "libraries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Library {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Integer maxActiveReservationsPerUser;

    @Column(nullable = false)
    private Integer reservationWindowInDays;

    @Column(nullable = false)
    private Integer checkInTimeoutMinutes; //after the reservation times how many minutes later user must check in.

    @Column(nullable = false)
    private Integer checkpointIntervalMinutes;//how many minutes later user must scan the checkpoint in a row.

    @Column(nullable = false)
    private Integer checkpointGraceMinutes; //after the checkpoints ready how many minutes later user must scan the checkpoint.

    @Column(nullable = false)
    private Integer penaltyBlockDays;


}
