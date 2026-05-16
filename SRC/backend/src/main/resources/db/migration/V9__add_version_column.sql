-- V9: Add version column for optimistic locking to delivery_assignments and orders

ALTER TABLE delivery_assignments ADD COLUMN version INT NOT NULL DEFAULT 0;
ALTER TABLE orders ADD COLUMN version INT NOT NULL DEFAULT 0;
