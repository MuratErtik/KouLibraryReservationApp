
CREATE SEQUENCE IF NOT EXISTS libraries_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE libraries (
                           id BIGINT NOT NULL,
                           version BIGINT,
                           name VARCHAR(255) NOT NULL,
                           description TEXT,
                           address VARCHAR(255) NOT NULL,
                           max_active_reservations_per_user INTEGER NOT NULL,
                           reservation_window_in_days INTEGER NOT NULL,
                           check_in_timeout_minutes INTEGER NOT NULL,
                           checkpoint_interval_minutes INTEGER NOT NULL,
                           checkpoint_grace_minutes INTEGER NOT NULL,
                           penalty_block_days INTEGER NOT NULL,
                           CONSTRAINT pk_libraries PRIMARY KEY (id),
                           CONSTRAINT uc_libraries_name UNIQUE (name)
);





CREATE SEQUENCE IF NOT EXISTS library_working_hours_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE library_working_hours (
                                       id BIGINT NOT NULL,
                                       version BIGINT,
                                       library_id BIGINT NOT NULL,
                                       day_of_week VARCHAR(255) NOT NULL,
                                       opening_time TIME WITHOUT TIME ZONE NOT NULL,
                                       closing_time TIME WITHOUT TIME ZONE NOT NULL,
                                       is_closed BOOLEAN,
                                       CONSTRAINT pk_library_working_hours PRIMARY KEY (id),
                                       CONSTRAINT fk_working_hours_library FOREIGN KEY (library_id) REFERENCES libraries (id),
                                       CONSTRAINT uc_library_day UNIQUE (library_id, day_of_week)
);


CREATE SEQUENCE IF NOT EXISTS library_closures_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE library_closures (
                                  id BIGINT NOT NULL,
                                  version BIGINT,
                                  library_id BIGINT NOT NULL,
                                  start_date_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                  end_date_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                  reason VARCHAR(255) NOT NULL,
                                  CONSTRAINT pk_library_closures PRIMARY KEY (id),
                                  CONSTRAINT fk_closure_library FOREIGN KEY (library_id) REFERENCES libraries (id)
);

CREATE INDEX idx_closure_library_id ON library_closures (library_id);

ALTER TABLE qr_codes ADD COLUMN version BIGINT DEFAULT 0;


ALTER TABLE desks ADD COLUMN version BIGINT DEFAULT 0;
ALTER TABLE desks ADD COLUMN library_id BIGINT;

ALTER TABLE desks ADD CONSTRAINT fk_desk_library
    FOREIGN KEY (library_id) REFERENCES libraries (id);


ALTER TABLE desks DROP CONSTRAINT IF EXISTS desks_saloon_id_desk_number_key;

ALTER TABLE desks ADD CONSTRAINT uc_saloon_desk_number UNIQUE (saloon_id, desk_number);

ALTER TABLE reservations ADD COLUMN reservation_time TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE saloon DROP COLUMN IF EXISTS opening_time;
ALTER TABLE saloon DROP COLUMN IF EXISTS closing_time;
ALTER TABLE saloon ADD COLUMN library_id BIGINT;

ALTER TABLE saloon ADD CONSTRAINT fk_saloon_library
    FOREIGN KEY (library_id) REFERENCES libraries (id);

CREATE INDEX idx_saloon_library_id ON saloon (library_id);
