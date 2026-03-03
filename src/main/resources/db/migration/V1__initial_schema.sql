CREATE SEQUENCE IF NOT EXISTS users_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE users (
                       id BIGINT NOT NULL,
                       version BIGINT,
                       user_role VARCHAR(255),
                       user_status VARCHAR(255),
                       student_id_number VARCHAR(255) NOT NULL,
                       first_name VARCHAR(255),
                       last_name VARCHAR(255),
                       email VARCHAR(255),
                       CONSTRAINT pk_users PRIMARY KEY (id),
                       CONSTRAINT uc_users_student_id_number UNIQUE (student_id_number)
);

CREATE SEQUENCE IF NOT EXISTS saloon_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE saloon (
                        id BIGINT NOT NULL,
                        name VARCHAR(255) NOT NULL,
                        status VARCHAR(255) NOT NULL, -- RoomStatus Enum (ACTIVE, CLOSED vb.)
                        floor INTEGER,
                        capacity INTEGER,
                        opening_time TIME WITHOUT TIME ZONE,
                        closing_time TIME WITHOUT TIME ZONE,
                        CONSTRAINT pk_saloon PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS qr_codes_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE qr_codes (
                          id BIGINT NOT NULL,
                          code VARCHAR(255) NOT NULL,
                          status VARCHAR(255) NOT NULL, -- QRCodeStatus (ACTIVE, INACTIVE, REVOKED)
                          created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                          revoked_at TIMESTAMP WITHOUT TIME ZONE,
                          CONSTRAINT pk_qr_codes PRIMARY KEY (id),
                          CONSTRAINT uc_qr_codes_code UNIQUE (code)
);


CREATE SEQUENCE IF NOT EXISTS desks_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE desks (
                       id BIGINT NOT NULL,
                       desk_number INTEGER NOT NULL,
                       status VARCHAR(255) NOT NULL,        -- DeskStatus (FREE, OCCUPIED, RESERVED vb.)
                       policy VARCHAR(255) NOT NULL,        -- DeskPolicy (RESERVABLE, WALK_IN vb.)
                       has_power_socket BOOLEAN NOT NULL,
                       saloon_id BIGINT NOT NULL,
                       qr_code_id BIGINT,
                       CONSTRAINT pk_desks PRIMARY KEY (id),
                       CONSTRAINT fk_desk_saloon FOREIGN KEY (saloon_id) REFERENCES saloon (id),
                       CONSTRAINT fk_desk_qrcode FOREIGN KEY (qr_code_id) REFERENCES qr_codes (id),
                       CONSTRAINT uc_desk_room_number UNIQUE (saloon_id, desk_number),
                       CONSTRAINT uc_desks_qr_code_id UNIQUE (qr_code_id)
);



CREATE SEQUENCE IF NOT EXISTS reservations_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE reservations (
                              id BIGINT NOT NULL,
                              user_id BIGINT NOT NULL,
                              desk_id BIGINT NOT NULL,
                              start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                              end_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                              check_in_time TIMESTAMP WITHOUT TIME ZONE,
                              last_checkpoint_time TIMESTAMP WITHOUT TIME ZONE,
                              status VARCHAR(255) NOT NULL, -- PENDING, ACTIVE, COMPLETED, CANCELLED
                              version BIGINT,
                              CONSTRAINT pk_reservations PRIMARY KEY (id),
                              CONSTRAINT fk_res_user FOREIGN KEY (user_id) REFERENCES users (id),
                              CONSTRAINT fk_res_desk FOREIGN KEY (desk_id) REFERENCES desks (id)
);

CREATE INDEX idx_res_user ON reservations (user_id);
CREATE INDEX idx_res_desk ON reservations (desk_id);
CREATE INDEX idx_res_status ON reservations (status);



CREATE SEQUENCE IF NOT EXISTS penalties_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE penalties (
                           id BIGINT NOT NULL,
                           user_id BIGINT NOT NULL,
                           reservation_id BIGINT,
                           reason VARCHAR(255) NOT NULL,    -- PenaltyReason Enum
                           status VARCHAR(255) NOT NULL,    -- PenaltyStatus Enum
                           start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                           end_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                           description VARCHAR(500),
                           CONSTRAINT pk_penalties PRIMARY KEY (id),
                           CONSTRAINT fk_penalty_user FOREIGN KEY (user_id) REFERENCES users (id),
                           CONSTRAINT fk_penalty_reservation FOREIGN KEY (reservation_id) REFERENCES reservations (id)
);

CREATE INDEX idx_penalty_user ON penalties (user_id);
CREATE INDEX idx_penalty_status ON penalties (status);



CREATE SEQUENCE IF NOT EXISTS checkpoints_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE checkpoints (
                             id BIGINT NOT NULL,
                             reservation_id BIGINT NOT NULL,
                             type VARCHAR(255) NOT NULL,      -- CheckpointType (QR_CODE, MANUAL vb.)
                             result VARCHAR(255) NOT NULL,    -- CheckpointResult (SUCCESS, TIMEOUT, FAILED)
                             created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                             responded_at TIMESTAMP WITHOUT TIME ZONE,
                             CONSTRAINT pk_checkpoints PRIMARY KEY (id),
                             CONSTRAINT fk_checkpoint_reservation FOREIGN KEY (reservation_id) REFERENCES reservations (id)
);

CREATE INDEX idx_checkpoint_reservation ON checkpoints (reservation_id);
CREATE INDEX idx_checkpoint_time ON checkpoints (created_at);





