CREATE SEQUENCE IF NOT EXISTS library_time_slots_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE library_time_slots (
                                    id BIGINT NOT NULL DEFAULT nextval('library_time_slots_seq'),
                                    library_id BIGINT NOT NULL,
                                    day_of_week VARCHAR(20) NOT NULL,
                                    start_time TIME NOT NULL,
                                    end_time TIME NOT NULL,
                                    is_available BOOLEAN NOT NULL DEFAULT TRUE,
                                    CONSTRAINT pk_library_time_slots PRIMARY KEY (id),
                                    CONSTRAINT fk_library_time_slots_library FOREIGN KEY (library_id) REFERENCES libraries (id),
                                    CONSTRAINT uk_library_day_start UNIQUE (library_id, day_of_week, start_time)
);

CREATE INDEX idx_library_time_slots_lookup ON library_time_slots (library_id, day_of_week, is_available);

