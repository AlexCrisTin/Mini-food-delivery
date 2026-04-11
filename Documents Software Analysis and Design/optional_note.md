# Optional note for later use

## Topping/Modifiers for food

```sql

CREATE TABLE item_option_groups (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    menu_item_id    BIGINT NOT NULL,
    
    name            VARCHAR(100) NOT NULL,
    is_required     BOOLEAN NOT NULL DEFAULT FALSE, -- Bắt buộc chọn không? (VD: Size là bắt buộc)
    min_choices     INT NOT NULL DEFAULT 0,         -- Số lượng chọn tối thiểu
    max_choices     INT NOT NULL DEFAULT 1,         -- Số lượng chọn tối đa (Topping có thể cho chọn nhiều)
    
    CONSTRAINT fk_option_groups_menu_item FOREIGN KEY (menu_item_id) REFERENCES menu_items(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_option_groups_item ON item_option_groups(menu_item_id);

CREATE TABLE item_options (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_id            BIGINT NOT NULL,
    
    name                VARCHAR(100) NOT NULL,
    additional_price    DECIMAL(12,2) NOT NULL DEFAULT 0,
    is_available        BOOLEAN DEFAULT TRUE,
    
    CONSTRAINT fk_item_options_group FOREIGN KEY (group_id) REFERENCES item_option_groups(id) ON DELETE CASCADE,
    CONSTRAINT chk_item_options_price CHECK (additional_price >= 0)
) ENGINE=InnoDB;

CREATE INDEX idx_item_options_group ON item_options(group_id);

CREATE TABLE order_item_options (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_item_id   BIGINT NOT NULL,
    option_id       BIGINT, 
    
    group_name      VARCHAR(100) NOT NULL,  -- Lưu cứng: "Thêm Topping"
    option_name     VARCHAR(100) NOT NULL,  -- Lưu cứng: "Trân châu trắng"
    price           DECIMAL(12,2) NOT NULL, -- Lưu cứng: 7000.00
    
    CONSTRAINT fk_order_item_options_item   FOREIGN KEY (order_item_id) REFERENCES order_items(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_item_options_option FOREIGN KEY (option_id)     REFERENCES item_options(id) ON DELETE SET NULL,
    
    CONSTRAINT chk_order_item_options_price CHECK (price >= 0)
) ENGINE=InnoDB;

CREATE INDEX idx_order_item_options_item ON order_item_options(order_item_id);
```
