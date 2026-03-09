package org.koulibrary.koulibraryreservationapp.domains;

public enum CheckpointType {
    AUTO, // by system
    MANUAL, // by user
    CHECK_IN,       // first
    PERIODIC,       //
    CHECK_OUT,      //
    MISSED,         // user does not response
    SYSTEM_EVICT    // sistem tarafından zorla sonlandırıldı
}
