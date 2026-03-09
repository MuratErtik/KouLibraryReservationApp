package org.koulibrary.koulibraryreservationapp.exceptions;

public class EndDateCannotBeBeforeStartDateException extends RuntimeException {
    public EndDateCannotBeBeforeStartDateException(String message) {
        super(message);
    }
}
