package org.koulibrary.koulibraryreservationapp.exceptions;

public class LibrariesTableIsEmptyException extends RuntimeException {
    public LibrariesTableIsEmptyException(String message) {
        super(message);
    }
}
