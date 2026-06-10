CREATE UNIQUE INDEX uq_active_desk_slot
    ON reservations (desk_id, slot_id)
    WHERE status IN ('PENDING', 'ACTIVE', 'SUSPENDED');