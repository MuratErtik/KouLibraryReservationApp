package org.koulibrary.koulibraryreservationapp.domains;

public enum StatusChangeReason {

    USER_CANCELLED,
    ADMIN_CANCELLED,
    NO_SHOW,
    CHECKPOINT_MISSED,
    SYSTEM_AUTO,
    MANUAL_OVERRIDE,
    APPEAL_APPROVED
}
