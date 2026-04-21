package org.koulibrary.koulibraryreservationapp.exceptions;

public class ClosureDoesNotBelongToThisSaloon extends RuntimeException {
    public ClosureDoesNotBelongToThisSaloon(String message) {
        super(message);
    }
}
