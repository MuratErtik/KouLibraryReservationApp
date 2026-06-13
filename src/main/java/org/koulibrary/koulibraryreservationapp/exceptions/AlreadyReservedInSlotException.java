package org.koulibrary.koulibraryreservationapp.exceptions;

public class AlreadyReservedInSlotException extends RuntimeException {
    public AlreadyReservedInSlotException(String message) {
        super(message);
    }
}
