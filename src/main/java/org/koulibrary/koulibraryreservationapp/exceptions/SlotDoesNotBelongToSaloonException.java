package org.koulibrary.koulibraryreservationapp.exceptions;

public class SlotDoesNotBelongToSaloonException extends RuntimeException {
    public SlotDoesNotBelongToSaloonException(String message) {
        super(message);
    }
}
