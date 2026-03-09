
CREATE TABLE reservation_status_logs (
                                         id BIGSERIAL PRIMARY KEY,
                                         reservation_id BIGINT NOT NULL,
                                         changed_by BIGINT,
                                         from_status VARCHAR(50),
                                         to_status VARCHAR(50) NOT NULL,
                                         reason VARCHAR(100),
                                         note VARCHAR(500),
                                         changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         CONSTRAINT fk_res_status_logs_reservation FOREIGN KEY (reservation_id) REFERENCES reservations (id),
                                         CONSTRAINT fk_res_status_logs_user FOREIGN KEY (changed_by) REFERENCES users (id)
);

CREATE INDEX idx_res_status_logs_reservation_id ON reservation_status_logs (reservation_id);




CREATE SEQUENCE IF NOT EXISTS reservations_seq START WITH 1 INCREMENT BY 50;

ALTER TABLE reservations ADD COLUMN IF NOT EXISTS slot_id BIGINT;
ALTER TABLE reservations ADD COLUMN IF NOT EXISTS cancellation_reason VARCHAR(500);
ALTER TABLE reservations ALTER COLUMN reservation_time SET DEFAULT CURRENT_TIMESTAMP;

-- 3. Add Foreign Key for LibraryTimeSlot
ALTER TABLE reservations
    ADD CONSTRAINT fk_res_slot
        FOREIGN KEY (slot_id) REFERENCES library_time_slots (id);



-- 5. Add indexes for performance
CREATE INDEX idx_checkpoint_reservation_id ON checkpoints (reservation_id);
CREATE INDEX idx_res_slot_id ON reservations (slot_id);




ALTER TABLE libraries
    ADD COLUMN IF NOT EXISTS slot_duration_minutes INTEGER NOT NULL DEFAULT 60;



