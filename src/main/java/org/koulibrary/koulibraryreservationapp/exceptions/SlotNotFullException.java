package org.koulibrary.koulibraryreservationapp.exceptions;

public class SlotNotFullException extends RuntimeException {
    public SlotNotFullException(String message) {
        super(message);
    }
}
