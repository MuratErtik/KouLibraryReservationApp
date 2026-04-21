package org.koulibrary.koulibraryreservationapp.exceptions;

public class DeskNotFoundException extends RuntimeException {
    public DeskNotFoundException(String message) {
        super(message);
    }
}
