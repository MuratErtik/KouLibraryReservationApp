package org.koulibrary.koulibraryreservationapp.domains;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum ReservationStatus {
    PENDING,    // reservation done check in waiting
    ACTIVE,     // QR done season has started
    SUSPENDED,  // checkpoint has omitted
    CANCELLED,
    COMPLETED,
    PENALIZED,
    NO_SHOW; // check in fail

    public static final Set<ReservationStatus> OCCUPYING =
            Collections.unmodifiableSet(EnumSet.of(PENDING, ACTIVE, SUSPENDED));
}