# Cypress E2E Detailed Tests

This document specifies the end-to-end integration flows simulated by Cypress E2E scripts (`SRC/frontend/cypress/e2e/*.cy.js`).

---

## 1. Authentication E2E flows (`auth.cy.js`)

### `để trống email và mật khẩu → không gọi API`
**Scenario (Negative):** Prevent empty submissions.
- **Expected Outcomes:** Clicking "Đăng nhập" without filling forms blocks submission; authentication endpoints are not hit.

### `nhập email sai định dạng → hiện lỗi`
**Scenario (Negative):** Enforces email schema validations.
- **Expected Outcomes:** Browser inputs trigger native HTML5 feedback, or custom invalid form warnings are shown.

### `API trả về 200 → chuyển hướng sau đăng nhập`
**Scenario (Positive):** Successful login session.
- **Expected Outcomes:** Submitting valid inputs triggers mock API response, saves token, and redirects path to `/browse`.

### `mật khẩu xác nhận không khớp → hiện lỗi`
**Scenario (Negative):** Registration passwords match check.
- **Expected Outcomes:** Signup action is blocked; UI prompts mismatched passcode error.

### `đăng ký thành công → mock 201 → chuyển trang`
**Scenario (Positive):** Registering a new account.
- **Expected Outcomes:** Post request triggers success state and redirects path.

---

## 2. Product Discovery & Browsing E2E flows (`browse.cy.js`)

### `tải trang /browse thành công`
**Scenario (Positive):** Renders page shell.
- **Expected Outcomes:** Renders layout view successfully.

### `hiển thị món ăn sau khi API trả về`
**Scenario (Positive):** Renders mock dish cards.
- **Expected Outcomes:** Displays restaurants feed containing item data populated from stubbed APIs.

### `tìm kiếm "Phở" → chỉ hiển thị kết quả liên quan`
**Scenario (Positive):** Filter by search string.
- **Expected Outcomes:** typing search queries dynamically updates popover suggestions to match target food names.

---

## 3. Cart & Basket Manipulation E2E flows (`cart.cy.js`)

### `hiển thị thông báo giỏ hàng trống`
**Scenario (Positive):** Empty basket indicator.
- **Expected Outcomes:** Shows cart details indicating "0 món" and displays empty prompts.

### `hiển thị subtotal đúng: 45.000 × 2 = 90.000 ₫`
**Scenario (Positive):** Real-time calculations.
- **Expected Outcomes:** Adjusting item count inputs recalculates the item total on-screen instantly.

### `hiển thị tạm tính khi subtotal >= 100.000 ₫`
**Scenario (Positive):** Discount trigger boundaries.
- **Expected Outcomes:** Reaching or exceeding threshold activates the discount details row on the UI page.

---

## 4. Landing Page E2E flows (`home.cy.js`)

### `hiển thị logo / thương hiệu "Giao Đồ Ăn"`
**Scenario (Positive):** Branding visibility.
- **Expected Outcomes:** Verifies landing elements show branding copy.

### `click "Đăng nhập" → modal hiện ra`
**Scenario (Positive):** Interactive auth popup.
- **Expected Outcomes:** Clicking nav buttons mounts the authentication modal successfully.

---

## 5. NotFound Redirection E2E flows (`not-found.cy.js`)

### `truy cập URL không tồn tại → hiển thị 404`
**Scenario (Negative):** Dead link handling.
- **Expected Outcomes:** Visited page displays "404" and "Trang không tồn tại" messages.

### `click "Về trang chủ" → quay về /`
**Scenario (Positive):** Return to home navigation.
- **Expected Outcomes:** Button click triggers redirection to the base landing page.

---

## 6. Orders Ledger E2E flows (`orders.cy.js`)

### `hiển thị danh sách đơn từ API`
**Scenario (Positive):** Render past orders list.
- **Expected Outcomes:** Lists historical transactions, displaying mock restaurant names and pricing properly.

### `hiển thị bước trạng thái "Đang giao"`
**Scenario (Positive):** Track ongoing progress.
- **Expected Outcomes:** Tracking view maps mock order statuses (e.g. `SHIPPING`) to high-priority visual markers.
