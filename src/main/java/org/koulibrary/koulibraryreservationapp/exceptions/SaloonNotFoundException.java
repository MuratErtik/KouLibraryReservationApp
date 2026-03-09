package org.koulibrary.koulibraryreservationapp.exceptions;

public class SaloonNotFoundException extends RuntimeException {
    public SaloonNotFoundException(String message) {
        super(message);
    }
}
