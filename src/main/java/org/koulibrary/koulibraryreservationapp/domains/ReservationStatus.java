package org.koulibrary.koulibraryreservationapp.domains;

public enum ReservationStatus {
    PENDING,    // reservation done check in waiting
    ACTIVE,     // QR done season has started
    SUSPENDED,  // checkpoint has omitted
    CANCELLED,
    COMPLETED,
    NO_SHOW // check in fail
}
