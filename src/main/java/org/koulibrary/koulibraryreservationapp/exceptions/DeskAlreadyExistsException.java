package org.koulibrary.koulibraryreservationapp.exceptions;

public class DeskAlreadyExistsException extends RuntimeException {
    public DeskAlreadyExistsException(String message) {
        super(message);
    }
}
