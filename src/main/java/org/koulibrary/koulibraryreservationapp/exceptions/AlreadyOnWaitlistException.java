package org.koulibrary.koulibraryreservationapp.exceptions;

public class AlreadyOnWaitlistException extends RuntimeException {
    public AlreadyOnWaitlistException(String message) {
        super(message);
    }
}
