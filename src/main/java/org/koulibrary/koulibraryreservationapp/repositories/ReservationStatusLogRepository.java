package org.koulibrary.koulibraryreservationapp.repositories;

import org.koulibrary.koulibraryreservationapp.entities.ReservationStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationStatusLogRepository extends JpaRepository<ReservationStatusLog, Long> {
}
