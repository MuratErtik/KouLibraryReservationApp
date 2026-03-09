CREATE TABLE notifications (
                               id BIGSERIAL PRIMARY KEY,
                               user_id BIGINT NOT NULL,
                               type VARCHAR(50) NOT NULL,
                               title VARCHAR(255) NOT NULL,
                               body TEXT NOT NULL,
                               is_read BOOLEAN NOT NULL DEFAULT FALSE,
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               read_at TIMESTAMP,
                               reservation_id BIGINT,
                               penalty_id BIGINT,
                               waitlist_id BIGINT,
                               CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users (id),
                               CONSTRAINT fk_notifications_reservation FOREIGN KEY (reservation_id) REFERENCES reservations (id),
                               CONSTRAINT fk_notifications_penalty FOREIGN KEY (penalty_id) REFERENCES penalties (id),
                               CONSTRAINT fk_notifications_waitlist FOREIGN KEY (waitlist_id) REFERENCES waitlist (id)
);

CREATE INDEX idx_notifications_user_read ON notifications (user_id, is_read);