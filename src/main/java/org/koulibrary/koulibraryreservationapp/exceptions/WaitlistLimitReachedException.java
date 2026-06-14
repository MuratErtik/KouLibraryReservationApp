package org.koulibrary.koulibraryreservationapp.exceptions;

public class WaitlistLimitReachedException extends RuntimeException {
    public WaitlistLimitReachedException(String message) {
        super(message);
    }
}
