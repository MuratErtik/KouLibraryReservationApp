package org.koulibrary.koulibraryreservationapp.exceptions;

public class WaitlistNotFoundException extends RuntimeException {
    public WaitlistNotFoundException(String message) {
        super(message);
    }
}
