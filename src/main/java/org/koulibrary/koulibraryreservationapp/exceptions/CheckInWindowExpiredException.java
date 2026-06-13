package org.koulibrary.koulibraryreservationapp.exceptions;

public class CheckInWindowExpiredException extends RuntimeException {
    public CheckInWindowExpiredException(String message) {
        super(message);
    }
}
