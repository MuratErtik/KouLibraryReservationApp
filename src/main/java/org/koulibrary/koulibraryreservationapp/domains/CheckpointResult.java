package org.koulibrary.koulibraryreservationapp.domains;

public enum CheckpointResult {

    CONFIRMED,  //
    MISSED,     // time is up, no response
    FAILED,
    RESPONDED,
    LATE,           // grace period içinde cevapladı,
    EVICTED         // masa boşaltıldı
}
