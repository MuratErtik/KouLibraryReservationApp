package org.koulibrary.koulibraryreservationapp.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


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

    @Column(nullable = false)
    private Integer slotDurationMinutes;

    @OneToMany(mappedBy = "library", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LibraryWorkingHours> workingHours = new ArrayList<>();

    @OneToMany(mappedBy = "library", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LibraryClosures> closures = new ArrayList<>();

    @OneToMany(mappedBy = "library", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LibraryTimeSlot> timeSlots = new ArrayList<>();

    @OneToMany(mappedBy = "library", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Saloon> saloons = new ArrayList<>();


}
