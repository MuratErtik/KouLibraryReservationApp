package org.koulibrary.koulibraryreservationapp.exceptions;

public class DeskAlreadyReservedException extends RuntimeException {
    public DeskAlreadyReservedException(String message) {
        super(message);
    }
}
