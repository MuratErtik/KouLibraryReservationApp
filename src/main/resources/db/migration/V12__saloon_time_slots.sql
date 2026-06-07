-- 1. Önce bağımlı FK'ları düşür
ALTER TABLE reservations DROP CONSTRAINT IF EXISTS fk_res_slot;
ALTER TABLE waitlist DROP CONSTRAINT IF EXISTS fk_waitlist_slot;

-- 2. Artık tablo drop edilebilir
DROP TABLE IF EXISTS library_time_slots;

-- 3. Sequence
CREATE SEQUENCE IF NOT EXISTS saloon_time_slots_seq
    START WITH 1
    INCREMENT BY 50;

-- 4. Yeni tablo
CREATE TABLE saloon_time_slots (
                                   id            BIGINT      NOT NULL DEFAULT nextval('saloon_time_slots_seq'),
                                   saloon_id     BIGINT      NOT NULL,
                                   date          DATE        NOT NULL,
                                   start_time    TIME        NOT NULL,
                                   end_time      TIME        NOT NULL,
                                   is_available  BOOLEAN     NOT NULL DEFAULT TRUE,

                                   CONSTRAINT pk_saloon_time_slots PRIMARY KEY (id),
                                   CONSTRAINT uq_saloon_date_start UNIQUE (saloon_id, date, start_time),
                                   CONSTRAINT fk_saloon_time_slots_saloon FOREIGN KEY (saloon_id) REFERENCES saloon(id)
);

-- 5. Saloon tablosuna kolon ekle
ALTER TABLE saloon
    ADD COLUMN slot_duration_minutes INTEGER;

-- 6. Reservations FK güncelle
ALTER TABLE reservations
    ADD CONSTRAINT fk_res_slot
        FOREIGN KEY (slot_id) REFERENCES saloon_time_slots(id);

-- 7. Waitlist FK güncelle
ALTER TABLE waitlist
    ADD CONSTRAINT fk_waitlist_slot
        FOREIGN KEY (slot_id) REFERENCES saloon_time_slots(id);

-- 8. Index
CREATE INDEX idx_saloon_time_slots_saloon_date
    ON saloon_time_slots(saloon_id, date);