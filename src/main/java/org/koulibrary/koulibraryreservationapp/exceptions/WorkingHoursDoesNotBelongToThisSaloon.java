package org.koulibrary.koulibraryreservationapp.exceptions;

public class WorkingHoursDoesNotBelongToThisSaloon extends RuntimeException {
    public WorkingHoursDoesNotBelongToThisSaloon(String message) {
        super(message);
    }
}
