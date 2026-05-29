# CartStore Detailed Tests

This document specifies the individual test scenarios covered in `cart.spec.js` for testing Pinia's cart state store.

---

## 1. Initial State

### `giỏ hàng ban đầu rỗng`
**Scenario (Positive):** Verify empty initialization.
- **Expected Outcomes:**
  - `cart.items` has length `0`

### `itemCount ban đầu là 0`
**Scenario (Positive):** Verify base item counter.
- **Expected Outcomes:**
  - `cart.itemCount` equals `0`

### `subtotal ban đầu là 0`
**Scenario (Positive):** Verify base sum totals.
- **Expected Outcomes:**
  - `cart.subtotal` equals `0`

---

## 2. Item Insertion & Basket Operations

### `thêm 1 món vào giỏ hàng rỗng`
**Scenario (Positive):** Populates items array.
- **Expected Outcomes:**
  - `cart.items` has length `1`
  - `cart.items[0].name` equals `"Bún bò Huế"`

### `thêm cùng món → tăng số lượng thay vì tạo dòng mới`
**Scenario (Positive):** Add duplicate items.
- **Expected Outcomes:**
  - `cart.items` length remains `1`
  - `cart.items[0].quantity` equals `2`

### `thêm món cùng nhà hàng nhưng khác size → tạo dòng mới`
**Scenario (Positive):** Differentiates items by variations.
- **Expected Outcomes:**
  - `cart.items` length becomes `2`

### `thêm món cùng nhà hàng nhưng khác ghi chú → tạo dòng mới`
**Scenario (Positive):** Differentiates items by customer notes.
- **Expected Outcomes:**
  - `cart.items` length becomes `2`

---

## 3. Cross-Restaurant Logic

### `thêm món từ nhà hàng khác → hiển thị xác nhận và xóa giỏ cũ`
**Scenario (Positive):** Replaces items and switches restaurants when confirmed.
- **Expected Outcomes:**
  - Confirmation triggers
  - Cart clears existing items from previous restaurant
  - Cart retains the newly added item from the new restaurant

---

## 4. Quantity Adjustments & Removal

### `incrementQuantity → tăng quantity của đúng item`
**Scenario (Positive):** Increases single item count.
- **Expected Outcomes:**
  - Target item `quantity` increases from `1` to `2`

### `decrementQuantity → giảm quantity`
**Scenario (Positive):** Decreases single item count.
- **Expected Outcomes:**
  - Target item `quantity` decreases from `2` to `1`

### `decrementQuantity khi quantity = 1 → không giảm nữa`
**Scenario (Positive):** Prevent negative or zero boundaries.
- **Expected Outcomes:**
  - Quantity remains `1`

### `removeItem → xóa hẳn item ra khỏi giỏ`
**Scenario (Positive):** Completely wipes line item.
- **Expected Outcomes:**
  - `cart.items` length decreases
  - Target item is no longer in array
