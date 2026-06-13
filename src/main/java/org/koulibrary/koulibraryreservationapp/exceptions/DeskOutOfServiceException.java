package org.koulibrary.koulibraryreservationapp.exceptions;

public class DeskOutOfServiceException extends RuntimeException {
    public DeskOutOfServiceException(String message) {
        super(message);
    }
}
