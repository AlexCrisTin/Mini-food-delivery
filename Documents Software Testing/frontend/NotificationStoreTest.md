# NotificationStore Detailed Tests

This document specifies the individual test scenarios covered in `notification.spec.js` for testing Pinia's notification state store.

---

## 1. Initial State

### `ban đầu không có thông báo`
**Scenario (Positive):** Clean store init.
- **Expected Outcomes:**
  - `store.items` is empty
  - `store.unreadCount` is `0`

---

## 2. Operations

### `pushNotification → thêm vào đầu danh sách`
**Scenario (Positive):** Appends notification to cache.
- **Expected Outcomes:**
  - `store.items` has length `1`
  - Added notification has `isRead = false`
  - `store.unreadCount` increments to `1`

### `markAsRead → giảm unreadCount`
**Scenario (Positive):** Transition single item state to read.
- **Expected Outcomes:**
  - `store.unreadCount` decrements
  - Specific item `isRead` becomes `true`

### `markAllAsRead → unreadCount = 0`
**Scenario (Positive):** Wipes all unread markers.
- **Expected Outcomes:**
  - `store.unreadCount` drops to `0`
  - All items in `store.items` have `isRead = true`

### `clearNotifications → xóa toàn bộ`
**Scenario (Positive):** Purge all notification history.
- **Expected Outcomes:**
  - `store.items` is empty

### `giá trị mặc định khi payload thiếu trường`
**Scenario (Positive):** Graceful recovery for incomplete payload.
- **Expected Outcomes:**
  - Falls back to `type = "SYSTEM"`
  - Falls back to `title = "Thong bao"`
