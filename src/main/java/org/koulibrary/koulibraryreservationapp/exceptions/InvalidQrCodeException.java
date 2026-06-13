package org.koulibrary.koulibraryreservationapp.exceptions;

public class InvalidQrCodeException extends RuntimeException {
    public InvalidQrCodeException(String message) {
        super(message);
    }
}
