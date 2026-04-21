package org.koulibrary.koulibraryreservationapp.exceptions;

public class ClosureDoesNotBelongToThisLibrary extends RuntimeException {
    public ClosureDoesNotBelongToThisLibrary(String message) {
        super(message);
    }
}
