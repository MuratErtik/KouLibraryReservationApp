package org.koulibrary.koulibraryreservationapp.domains;

public enum PenaltyStatus {
    ACTIVE,
    EXPIRED,
    APPEALED,       // itiraz bekliyor
    REVOKED         // admin tarafından kaldırıldı
}
