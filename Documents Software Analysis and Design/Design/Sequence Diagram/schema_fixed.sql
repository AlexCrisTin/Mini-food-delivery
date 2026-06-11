-- ============================================================
--  mini_food_db  –  Schema hoàn chỉnh (đã vá 4 vấn đề)
--  Thay đổi so với bản gốc:
--    [FIX-1] delivery_assignments.order_id  → bỏ UNIQUE (1-n)
--    [ADD-2] Bảng payments                  → ánh xạ lớp ThanhToan
--    [ADD-3] Bảng coupons                   → quản lý mã giảm giá
--    [ADD-4] orders thêm coupon_id + discount_amount
--    [ADD-5] Bảng restaurant_payment_methods → UC-2.3
-- ============================================================

CREATE DATABASE IF NOT EXISTS mini_food_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE mini_food_db;

-- ------------------------------------------------------------
-- 1. users
-- ------------------------------------------------------------
CREATE TABLE users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    full_name   VARCHAR(100) NOT NULL,
    phone       VARCHAR(15)  UNIQUE,
    avatar_url  VARCHAR(500),

    role        VARCHAR(50)  NOT NULL DEFAULT 'USER',
                -- Giá trị hợp lệ: USER / OWNER / SHIPPER / ADMIN

    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    is_deleted  BOOLEAN      NOT NULL DEFAULT FALSE,

    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE INDEX idx_users_role_active ON users(role, is_active);


-- ------------------------------------------------------------
-- 2. addresses
-- ------------------------------------------------------------
CREATE TABLE addresses (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT       NOT NULL,
    label           VARCHAR(50),
    address_line    VARCHAR(500) NOT NULL,
    latitude        DECIMAL(10,8),
    longitude       DECIMAL(11,8),
    is_default      BOOLEAN      NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_addresses_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_addresses_user ON addresses(user_id);


-- ------------------------------------------------------------
-- 3. restaurant_categories
-- ------------------------------------------------------------
CREATE TABLE restaurant_categories (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    icon_url    VARCHAR(500)
) ENGINE=InnoDB;


-- ------------------------------------------------------------
-- 4. restaurants
-- ------------------------------------------------------------
CREATE TABLE restaurants (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id        BIGINT       NOT NULL,
    category_id     BIGINT,

    name            VARCHAR(200) NOT NULL,
    description     TEXT,
    phone           VARCHAR(15),

    address         VARCHAR(500) NOT NULL,
    latitude        DECIMAL(10,8),
    longitude       DECIMAL(11,8),

    image_url       VARCHAR(500),
    opening_time    TIME,
    closing_time    TIME,

    is_open         BOOLEAN DEFAULT TRUE,
    is_approved     BOOLEAN DEFAULT FALSE,
    is_deleted      BOOLEAN DEFAULT FALSE,

    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_restaurants_owner    FOREIGN KEY (owner_id)    REFERENCES users(id),
    CONSTRAINT fk_restaurants_category FOREIGN KEY (category_id) REFERENCES restaurant_categories(id)
) ENGINE=InnoDB;

CREATE INDEX idx_restaurants_owner    ON restaurants(owner_id);
CREATE INDEX idx_restaurants_category ON restaurants(category_id);
CREATE INDEX idx_restaurants_approved ON restaurants(is_approved);


-- ------------------------------------------------------------
-- 5. categories  (danh mục món ăn của từng nhà hàng)
-- ------------------------------------------------------------
CREATE TABLE categories (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id   BIGINT       NOT NULL,
    name            VARCHAR(100) NOT NULL,
    sort_order      INT DEFAULT 0,

    CONSTRAINT fk_categories_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_categories_restaurant ON categories(restaurant_id);


-- ------------------------------------------------------------
-- 6. menu_items
-- ------------------------------------------------------------
CREATE TABLE menu_items (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id   BIGINT       NOT NULL,
    category_id     BIGINT,

    name            VARCHAR(200) NOT NULL,
    description     TEXT,
    price           DECIMAL(12,2) NOT NULL,
    image_url       LONGTEXT,

    is_available    BOOLEAN DEFAULT TRUE,
    is_deleted      BOOLEAN DEFAULT FALSE,

    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_menu_items_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE,
    CONSTRAINT fk_menu_items_category   FOREIGN KEY (category_id)   REFERENCES categories(id)  ON DELETE SET NULL,
    CONSTRAINT chk_menu_items_price     CHECK (price >= 0)
) ENGINE=InnoDB;

CREATE INDEX idx_menu_items_restaurant ON menu_items(restaurant_id);
CREATE INDEX idx_menu_items_category   ON menu_items(category_id);


-- ------------------------------------------------------------
-- [ADD-3]  7. coupons  –  quản lý mã giảm giá (Admin tạo)
-- ------------------------------------------------------------
CREATE TABLE coupons (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    code             VARCHAR(50)   NOT NULL UNIQUE,
                     -- Mã ký tự, ví dụ: SUMMER20

    type             VARCHAR(20)   NOT NULL,
                     -- FIXED: giảm số tiền cố định
                     -- PERCENT: giảm theo phần trăm

    value            DECIMAL(12,2) NOT NULL,
                     -- Số tiền (FIXED) hoặc % (PERCENT)

    min_order_value  DECIMAL(12,2) NOT NULL DEFAULT 0,
                     -- Giá trị đơn hàng tối thiểu để áp dụng

    max_discount     DECIMAL(12,2),
                     -- Giới hạn số tiền giảm tối đa (dùng cho PERCENT)

    usage_limit      INT,
                     -- Giới hạn số lần dùng toàn hệ thống; NULL = không giới hạn

    used_count       INT           NOT NULL DEFAULT 0,

    is_active        BOOLEAN       NOT NULL DEFAULT TRUE,

    expires_at       TIMESTAMP     NULL,
                     -- NULL = không có hạn sử dụng

    created_at       TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_coupon_value      CHECK (value > 0),
    CONSTRAINT chk_coupon_type       CHECK (type IN ('FIXED', 'PERCENT')),
    CONSTRAINT chk_coupon_percent    CHECK (type <> 'PERCENT' OR value <= 100)
) ENGINE=InnoDB;


-- ------------------------------------------------------------
-- [ADD-4]  8. orders  –  thêm coupon_id + discount_amount
-- ------------------------------------------------------------
CREATE TABLE orders (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT        NOT NULL,
    restaurant_id       BIGINT        NOT NULL,

    delivery_address    VARCHAR(500)  NOT NULL,
    delivery_lat        DECIMAL(10,8),
    delivery_lng        DECIMAL(11,8),

    subtotal            DECIMAL(12,2) NOT NULL,
    delivery_fee        DECIMAL(12,2) NOT NULL DEFAULT 0,

    -- [ADD-4] Mã giảm giá áp dụng cho đơn hàng này
    coupon_id           BIGINT,
    discount_amount     DECIMAL(12,2) NOT NULL DEFAULT 0,
                        -- Số tiền thực tế được giảm (đã tính toán tại lúc đặt)

    total_amount        DECIMAL(12,2) NOT NULL,
                        -- = subtotal + delivery_fee - discount_amount

    payment_method      VARCHAR(50)   NOT NULL DEFAULT 'COD',
    status              VARCHAR(50)   NOT NULL DEFAULT 'Chờ xác nhận',
                        -- Giá trị hợp lệ: Chờ xác nhận / Đã xác nhận /
                        --   Đang chuẩn bị / Sẵn sàng giao / Đang giao hàng /
                        --   Đã giao thành công / Giao thất bại / Đã hủy

    note                VARCHAR(500),

    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_orders_user       FOREIGN KEY (user_id)       REFERENCES users(id),
    CONSTRAINT fk_orders_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id),
    CONSTRAINT fk_orders_coupon     FOREIGN KEY (coupon_id)     REFERENCES coupons(id) ON DELETE SET NULL,

    CONSTRAINT chk_orders_subtotal  CHECK (subtotal       >= 0),
    CONSTRAINT chk_orders_fee       CHECK (delivery_fee   >= 0),
    CONSTRAINT chk_orders_discount  CHECK (discount_amount >= 0),
    CONSTRAINT chk_orders_total     CHECK (total_amount   >= 0)
) ENGINE=InnoDB;

CREATE INDEX idx_orders_user       ON orders(user_id);
CREATE INDEX idx_orders_status     ON orders(status);
CREATE INDEX idx_orders_user_time  ON orders(user_id, created_at);
CREATE INDEX idx_orders_coupon     ON orders(coupon_id);


-- ------------------------------------------------------------
-- 9. order_items
-- ------------------------------------------------------------
CREATE TABLE order_items (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id        BIGINT        NOT NULL,
    menu_item_id    BIGINT,

    item_name       VARCHAR(200)  NOT NULL,
    item_price      DECIMAL(12,2) NOT NULL,
    quantity        INT           NOT NULL,
    subtotal        DECIMAL(12,2) NOT NULL,
    note            VARCHAR(500),

    CONSTRAINT fk_order_items_order     FOREIGN KEY (order_id)     REFERENCES orders(id)     ON DELETE CASCADE,
    CONSTRAINT fk_order_items_menu_item FOREIGN KEY (menu_item_id) REFERENCES menu_items(id) ON DELETE SET NULL,

    CONSTRAINT chk_order_items_quantity CHECK (quantity  > 0),
    CONSTRAINT chk_order_items_price    CHECK (item_price >= 0),
    CONSTRAINT chk_order_items_subtotal CHECK (subtotal   >= 0)
) ENGINE=InnoDB;

CREATE INDEX idx_order_items_order ON order_items(order_id);


-- ------------------------------------------------------------
-- 10. order_status_history
-- ------------------------------------------------------------
CREATE TABLE order_status_history (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id    BIGINT       NOT NULL,
    status      VARCHAR(50)  NOT NULL,
    changed_by  BIGINT,
    note        VARCHAR(500),
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_osh_order FOREIGN KEY (order_id)   REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_osh_user  FOREIGN KEY (changed_by) REFERENCES users(id)  ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE INDEX idx_order_status_history_order ON order_status_history(order_id);


-- ------------------------------------------------------------
-- [FIX-1]  11. delivery_assignments
--   Bỏ UNIQUE trên order_id → hỗ trợ 1-n
--   (một đơn có thể có nhiều lượt phân công nếu shipper từ chối)
--   Thêm is_active để xác định lượt phân công hiện tại
-- ------------------------------------------------------------
CREATE TABLE delivery_assignments (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,

    order_id        BIGINT       NOT NULL,
                    -- Không có UNIQUE – nhiều shipper có thể từ chối trước khi có người nhận

    shipper_id      BIGINT       NOT NULL,

    status          VARCHAR(50)  NOT NULL DEFAULT 'ASSIGNED',
                    -- ASSIGNED / REJECTED / PICKED_UP / DELIVERED / FAILED

    is_active       BOOLEAN      NOT NULL DEFAULT TRUE,
                    -- Chỉ một bản ghi is_active=TRUE trên mỗi order_id tại một thời điểm;
                    -- khi shipper từ chối → is_active=FALSE, tạo bản ghi mới

    picked_up_at    TIMESTAMP NULL,
    delivered_at    TIMESTAMP NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_da_order   FOREIGN KEY (order_id)   REFERENCES orders(id),
    CONSTRAINT fk_da_shipper FOREIGN KEY (shipper_id) REFERENCES users(id)
) ENGINE=InnoDB;

CREATE INDEX idx_delivery_order   ON delivery_assignments(order_id);
CREATE INDEX idx_delivery_shipper ON delivery_assignments(shipper_id);
CREATE INDEX idx_delivery_active  ON delivery_assignments(order_id, is_active);


-- ------------------------------------------------------------
-- 12. shipper_locations
-- ------------------------------------------------------------
CREATE TABLE shipper_locations (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    shipper_id  BIGINT        NOT NULL UNIQUE,
    latitude    DECIMAL(10,8) NOT NULL,
    longitude   DECIMAL(11,8) NOT NULL,
    is_online   BOOLEAN DEFAULT FALSE,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_shipper_locations_shipper FOREIGN KEY (shipper_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;


-- ------------------------------------------------------------
-- [ADD-2]  13. payments  –  ánh xạ lớp ThanhToan trong Class Diagram
-- ------------------------------------------------------------
CREATE TABLE payments (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id        BIGINT        NOT NULL UNIQUE,
                    -- Mỗi đơn hàng có đúng một bản ghi thanh toán (1-1)

    method          VARCHAR(50)   NOT NULL,
                    -- COD / ZALOPAY / BANK_TRANSFER ...

    amount          DECIMAL(12,2) NOT NULL,
                    -- Số tiền thực tế thanh toán

    status          VARCHAR(50)   NOT NULL DEFAULT 'PENDING',
                    -- PENDING / COMPLETED / FAILED / REFUNDED

    transaction_id  VARCHAR(100),
                    -- Mã giao dịch bên thứ ba (cổng thanh toán); NULL với COD

    paid_at         TIMESTAMP NULL,
                    -- Thời điểm thanh toán thành công

    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_payments_order  FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT chk_payments_amount CHECK (amount >= 0)
) ENGINE=InnoDB;

CREATE INDEX idx_payments_order ON payments(order_id);


-- ------------------------------------------------------------
-- [ADD-5]  14. restaurant_payment_methods  –  UC-2.3
-- ------------------------------------------------------------
CREATE TABLE restaurant_payment_methods (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id   BIGINT        NOT NULL,

    type            VARCHAR(50)   NOT NULL,
                    -- BANK / E_WALLET

    bank_name       VARCHAR(100),
                    -- Tên ngân hàng hoặc ví điện tử

    account_number  VARCHAR(50)   NOT NULL,
    account_holder  VARCHAR(100)  NOT NULL,

    is_default      BOOLEAN       NOT NULL DEFAULT FALSE,

    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_rpm_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE,
    CONSTRAINT chk_rpm_type      CHECK (type IN ('BANK', 'E_WALLET'))
) ENGINE=InnoDB;

CREATE INDEX idx_rpm_restaurant ON restaurant_payment_methods(restaurant_id);


-- ------------------------------------------------------------
-- 15. notifications
-- ------------------------------------------------------------
CREATE TABLE notifications (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    title       VARCHAR(200) NOT NULL,
    message     TEXT         NOT NULL,
    type        VARCHAR(50)  NOT NULL,
    is_read     BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_time ON notifications(created_at);
