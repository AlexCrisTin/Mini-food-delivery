# OrderStore Detailed Tests

This document specifies the individual test scenarios covered in `order.spec.js` for testing Pinia's order status store.

---

## 1. Initial Parameters

### `orders ban đầu là mảng rỗng`
**Scenario (Positive):** Clean init array.
- **Expected Outcomes:**
  - `order.orders` is `[]`

### `currentOrder ban đầu là null`
**Scenario (Positive):** No active tracking.
- **Expected Outcomes:**
  - `order.currentOrder` is `null`

---

## 2. Server Fetch Operations

### `tải danh sách đơn thành công`
**Scenario (Positive):** Updates local orders cache.
- **Expected Outcomes:**
  - `order.orders` holds returned transactions list
  - `orders[0].id` is correctly mapped

### `thất bại → lưu error`
**Scenario (Negative):** API exception handling.
- **Expected Outcomes:**
  - `order.error` stores rejection string

---

## 3. Operations & Lifecycle

### `lọc đơn đang xử lý (PENDING, SHIPPING...)`
**Scenario (Positive):** Active orders computed properties filtering.
- **Expected Outcomes:**
  - Returns only items with statuses `PENDING` and `SHIPPING`
  - Ignores `DELIVERED` transactions

### `tạo đơn → thêm đầu danh sách và set currentOrder`
**Scenario (Positive):** Placing a new order.
- **Expected Outcomes:**
  - `order.orders[0].id` holds the new order ID
  - `order.currentOrder` is updated with the returned payload

### `hủy đơn → cập nhật status trong orders và currentOrder`
**Scenario (Positive):** Order cancellation updates.
- **Expected Outcomes:**
  - `order.orders[0].status` transitions to `"CANCELLED"`
  - `order.currentOrder.status` transitions to `"CANCELLED"`
