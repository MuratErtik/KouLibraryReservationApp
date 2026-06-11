package org.koulibrary.koulibraryreservationapp.exceptions;

public class ReservationDoesNotBelongToUserException extends RuntimeException {
    public ReservationDoesNotBelongToUserException(String message) {
        super(message);
    }
}
