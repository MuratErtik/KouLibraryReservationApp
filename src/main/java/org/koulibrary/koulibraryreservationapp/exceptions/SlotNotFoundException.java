package org.koulibrary.koulibraryreservationapp.exceptions;

public class SlotNotFoundException extends RuntimeException {
    public SlotNotFoundException(String message) {
        super(message);
    }
}
