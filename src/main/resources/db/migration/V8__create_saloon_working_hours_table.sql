CREATE TABLE saloon_working_hours (
                                      id BIGSERIAL PRIMARY KEY,
                                      saloon_id BIGINT NOT NULL,
                                      day_of_week VARCHAR(20) NOT NULL,
                                      opening_time TIME NOT NULL,
                                      closing_time TIME NOT NULL,
                                      is_closed BOOLEAN NOT NULL DEFAULT FALSE,
                                      CONSTRAINT fk_saloon_working_hours_saloon FOREIGN KEY (saloon_id) REFERENCES saloon (id),
                                      CONSTRAINT uk_saloon_day UNIQUE (saloon_id, day_of_week)
);

CREATE INDEX idx_saloon_working_hours_lookup ON saloon_working_hours (saloon_id, day_of_week);

