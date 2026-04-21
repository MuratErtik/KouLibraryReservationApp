package org.koulibrary.koulibraryreservationapp.exceptions;

public class SaloonDoesNotBelongToLibraryException extends RuntimeException {
    public SaloonDoesNotBelongToLibraryException(String message) {
        super(message);
    }
}
