package org.koulibrary.koulibraryreservationapp.exceptions;

public class WorkingHoursDoesNotBelongToThisLibrary extends RuntimeException {
    public WorkingHoursDoesNotBelongToThisLibrary(String message) {
        super(message);
    }
}
