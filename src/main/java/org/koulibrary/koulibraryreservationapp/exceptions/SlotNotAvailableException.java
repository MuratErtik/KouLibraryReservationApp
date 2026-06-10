package org.koulibrary.koulibraryreservationapp.exceptions;

public class SlotNotAvailableException extends RuntimeException {
    public SlotNotAvailableException(String message) {
        super(message);
    }
}
