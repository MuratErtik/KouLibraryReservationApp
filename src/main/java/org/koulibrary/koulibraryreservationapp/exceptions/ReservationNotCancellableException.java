package org.koulibrary.koulibraryreservationapp.exceptions;

public class ReservationNotCancellableException extends RuntimeException {
    public ReservationNotCancellableException(String message) {
        super(message);
    }
}
