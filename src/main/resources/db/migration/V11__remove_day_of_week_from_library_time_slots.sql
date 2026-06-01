ALTER TABLE library_time_slots DROP CONSTRAINT uk_library_day_start;
DROP INDEX idx_library_time_slots_lookup;


ALTER TABLE library_time_slots DROP COLUMN day_of_week;
ALTER TABLE library_time_slots ADD COLUMN date DATE NOT NULL;

ALTER TABLE library_time_slots
    ADD CONSTRAINT uk_library_date_start UNIQUE (library_id, date, start_time);

CREATE INDEX idx_library_time_slots_lookup ON library_time_slots (library_id, date, is_available);