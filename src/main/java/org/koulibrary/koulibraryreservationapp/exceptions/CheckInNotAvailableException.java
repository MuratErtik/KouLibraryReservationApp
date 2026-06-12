package org.koulibrary.koulibraryreservationapp.exceptions;

public class CheckInNotAvailableException extends RuntimeException {
    public CheckInNotAvailableException(String message) {
        super(message);
    }
}
