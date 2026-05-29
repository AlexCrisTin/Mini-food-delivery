# Views Unit Detailed Tests

This document specifies the individual component-level test scenarios covered in the Vue view spec files.

---

## 1. CartView Unit Tests (`CartView.spec.js`)

### `render được mà không bị lỗi`
**Scenario (Positive):** Verify successful rendering.
- **Expected Outcomes:**
  - Component mounts without throwing errors

### `hiển thị tiêu đề "Giỏ hàng"`
**Scenario (Positive):** Verify header copy.
- **Expected Outcomes:**
  - Renders element containing text `"Giỏ hàng"`

### `hiển thị trạng thái empty khi giỏ hàng rỗng`
**Scenario (Positive):** Blank basket template.
- **Expected Outcomes:**
  - Shows `"Giỏ hàng trống"` and prompts discovery

### `click "Khám phá ngay" gọi goBrowse()`
**Scenario (Positive):** Redirect button execution.
- **Expected Outcomes:**
  - Clicking redirect button calls router push to `/browse`

### `hiển thị tên món trong giỏ hàng`
**Scenario (Positive):** Populated cart item display.
- **Expected Outcomes:**
  - Shows product names (e.g. `"Bún bò Huế"`) in listings

### `hiển thị số lượng = 2`
**Scenario (Positive):** Quantity indicator.
- **Expected Outcomes:**
  - Renders input/element indicating item quantity is `2`

### `click nút + gọi increment(item)`
**Scenario (Positive):** Quantity control trigger.
- **Expected Outcomes:**
  - Click on `+` invokes store action to increment quantity

### `click nút xóa gọi removeItem(lineId)`
**Scenario (Positive):** Delete item control.
- **Expected Outcomes:**
  - Click on deletion icon calls store item removal

---

## 2. NotFoundView Unit Tests (`NotFoundView.spec.js`)

### `hiển thị mã 404 và thông báo`
**Scenario (Positive):** Renders error indicators.
- **Expected Outcomes:**
  - UI displays text `"404"` and `"Trang không tồn tại"`

### `nút "Về trang chủ" điều hướng về /`
**Scenario (Positive):** Redirect home.
- **Expected Outcomes:**
  - Redirection button click calls router push to `/`

---

## 3. OrderHistory Unit Tests (`OrderHistory.spec.js`)

### `hiển thị tiêu đề trang`
**Scenario (Positive):** Renders view title.
- **Expected Outcomes:**
  - Displays `"Lịch sử đơn hàng"` header

### `danh sách rỗng → thông báo chưa có đơn`
**Scenario (Positive):** Empty purchases ledger.
- **Expected Outcomes:**
  - Displays empty prompt when order history list is empty

### `hiển thị đơn hàng từ store`
**Scenario (Positive):** Populated history feed.
- **Expected Outcomes:**
  - Lists past order transactions with matching store details
