CREATE TABLE waitlist (
                          id BIGSERIAL PRIMARY KEY,
                          user_id BIGINT NOT NULL,
                          desk_id BIGINT NOT NULL,
                          slot_id BIGINT NOT NULL,
                          requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          status VARCHAR(50) NOT NULL,
                          notified_at TIMESTAMP,
                          expires_at TIMESTAMP NOT NULL,
                          CONSTRAINT fk_waitlist_user FOREIGN KEY (user_id) REFERENCES users (id),
                          CONSTRAINT fk_waitlist_desk FOREIGN KEY (desk_id) REFERENCES desks (id),
                          CONSTRAINT fk_waitlist_slot FOREIGN KEY (slot_id) REFERENCES library_time_slots (id)
);

-- Performance indexes for queue management and expiration cleanup
CREATE INDEX idx_waitlist_lookup ON waitlist (desk_id, slot_id, status);
CREATE INDEX idx_waitlist_expiration ON waitlist (expires_at) WHERE status = 'NOTIFIED';


-- 1. Add checkpoint_id column to penalties table
ALTER TABLE penalties
    ADD COLUMN IF NOT EXISTS checkpoint_id BIGINT;

-- 2. Add Foreign Key constraint
ALTER TABLE penalties
    ADD CONSTRAINT fk_penalties_checkpoint
        FOREIGN KEY (checkpoint_id) REFERENCES checkpoints (id);

-- 3. Add an index for quick lookup
CREATE INDEX idx_penalties_checkpoint_id ON penalties (checkpoint_id);