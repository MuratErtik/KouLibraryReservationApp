CREATE TABLE saloon_closures (
                                 id              BIGINT PRIMARY KEY,
                                 version         BIGINT       NOT NULL DEFAULT 0,
                                 saloon_id       BIGINT       NOT NULL REFERENCES saloon(id),
                                 start_date_time TIMESTAMP    NOT NULL,
                                 end_date_time   TIMESTAMP    NOT NULL,
                                 reason          VARCHAR(255)  -- 'RENOVATION', 'EVENT', 'MAINTENANCE'
);