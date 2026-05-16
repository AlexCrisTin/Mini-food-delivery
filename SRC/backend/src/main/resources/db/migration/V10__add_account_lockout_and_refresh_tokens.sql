-- Add account lockout fields to users table
ALTER TABLE users
ADD COLUMN failed_login_attempts INT DEFAULT 0 NOT NULL,
ADD COLUMN account_locked_until TIMESTAMP NULL;

-- Create refresh_tokens table
CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(500) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
