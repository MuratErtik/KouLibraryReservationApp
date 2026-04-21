package org.koulibrary.koulibraryreservationapp.exceptions;

public class DeskDoesNotBelongToSaloonException extends RuntimeException {
    public DeskDoesNotBelongToSaloonException(String message) {
        super(message);
    }
}
