# AuthStore Detailed Tests

This document specifies the individual test scenarios covered in `auth.spec.js` for testing Pinia's authentication store.

---

## 1. Initial State

### `user ban đầu là null`
**Scenario (Positive):** Verify that the active user object is null.
- **Expected Outcomes:**
  - `auth.user` is `null`

### `isAuthenticated = false khi chưa có token`
**Scenario (Positive):** Verify auth boolean starts as false.
- **Expected Outcomes:**
  - `auth.isAuthenticated` is `false`

### `userRole = null khi chưa đăng nhập`
**Scenario (Positive):** Verify that the user role remains unassigned.
- **Expected Outcomes:**
  - `auth.userRole` is `null`

---

## 2. Authentication Operations

### `đăng nhập thành công → lưu user và token`
**Scenario (Positive):** Successful login returns credentials and token.
- **Expected Outcomes:**
  - `auth.user` has non-null value
  - `auth.token` matches the backend JWT key

### `đăng nhập thành công → isAuthenticated = true`
**Scenario (Positive):** Verify authentication status transition.
- **Expected Outcomes:**
  - `auth.isAuthenticated` becomes `true`

### `đăng nhập thành công → token lưu vào localStorage`
**Scenario (Positive):** Verify persistence in localStorage.
- **Expected Outcomes:**
  - `localStorage.getItem('token')` equals the JWT key

### `chuẩn hóa role: ROLE_CUSTOMER → CUSTOMER`
**Scenario (Positive):** Normalize server role formats containing prefix `ROLE_`.
- **Expected Outcomes:**
  - `auth.userRole` is precisely `CUSTOMER`

### `chuẩn hóa role: ADMIN (không có prefix) → ADMIN`
**Scenario (Positive):** Normalize standard role formats without prefix.
- **Expected Outcomes:**
  - `auth.userRole` is precisely `ADMIN`

### `đăng nhập thất bại → lưu error message`
**Scenario (Negative):** Handle credential rejection.
- **Expected Outcomes:**
  - Store execution triggers rejection
  - `auth.error` is populated with error explanation (`"Sai mật khẩu"`)

### `đăng nhập thất bại → isAuthenticated = false`
**Scenario (Negative):** Verify state blocked.
- **Expected Outcomes:**
  - `auth.isAuthenticated` remains `false`

---

## 3. Registration Operations

### `đăng ký thành công → lưu user và token`
**Scenario (Positive):** Successful sign up persists locally.
- **Expected Outcomes:**
  - `auth.user` populated
  - `auth.token` stored

### `đăng ký thất bại → error được lưu`
**Scenario (Negative):** Registration failure handling.
- **Expected Outcomes:**
  - `auth.error` caches the error message

---

## 4. Session Termination

### `logout → user = null`
**Scenario (Positive):** Sign out clears user data.
- **Expected Outcomes:**
  - `auth.user` resets to `null`

### `logout → token bị xóa khỏi localStorage`
**Scenario (Positive):** Remove token cache.
- **Expected Outcomes:**
  - `localStorage.getItem('token')` returns `null`
