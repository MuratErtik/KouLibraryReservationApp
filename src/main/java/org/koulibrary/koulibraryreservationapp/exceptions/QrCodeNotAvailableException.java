package org.koulibrary.koulibraryreservationapp.exceptions;

public class QrCodeNotAvailableException extends RuntimeException {
    public QrCodeNotAvailableException(String message) {
        super(message);
    }
}
