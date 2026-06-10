package org.koulibrary.koulibraryreservationapp.exceptions;

public class MaxActiveReservationsReachedException extends RuntimeException {
    public MaxActiveReservationsReachedException(String message) {
        super(message);
    }
}
