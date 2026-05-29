# Detailed Test Cases - Frontend

This document provides a comprehensive and detailed breakdown of the frontend test cases, covering both **Vitest Unit/Component Tests** and **Cypress End-to-End (E2E) Tests**. Each test case specifies the exact preconditions, actions to be taken, and the precise expected results to ensure clear verifiability.

---

## 1. Authentication State & Operations (`AuthStore`)

### AUTH-FE-001: Initial State Initialization
- **Description**: Verifies that the authentication store initializes with clean/empty values when no token exists.
- **Preconditions**: Local storage is empty. Pinia is active.
- **Action**: Instantiate `useAuthStore()`.
- **Expected Result**:
  - `user` is `null`.
  - `isAuthenticated` is `false`.
  - `userRole` is `null`.
  - `isLoading` is `false`.
  - `error` is `null`.

### AUTH-FE-002: Successful Login (Customer Role)
- **Description**: Verifies that successful credential submission sets authentication state, caches the JWT, and normalizes roles.
- **Preconditions**: `authService.login` is mocked to resolve with user info (`ROLE_CUSTOMER`) and token.
- **Action**: Call `authStore.login("customer@test.com", "password123")`.
- **Expected Result**:
  - `token` is set to the returned mock JWT.
  - `isAuthenticated` is `true`.
  - `localStorage.getItem('token')` equals the cached JWT.
  - `user.role` normalized from `ROLE_CUSTOMER` to `CUSTOMER`.
  - `isLoading` resets to `false`.

### AUTH-FE-003: Login with Normalization (Admin Role)
- **Description**: Verifies that roles without a standard prefix (e.g. `ADMIN`) are correctly preserved and normalized.
- **Preconditions**: `authService.login` mocked to return `ADMIN` role.
- **Action**: Call `authStore.login("admin@test.com", "adminpass")`.
- **Expected Result**:
  - `userRole` is exactly `ADMIN`.

### AUTH-FE-004: Failed Login Handling
- **Description**: Verifies that an authentication failure sets the store error state and blocks access.
- **Preconditions**: `authService.login` mocked to reject with `Error("Sai mật khẩu")`.
- **Action**: Call `authStore.login("wrong@test.com", "wrong")`.
- **Expected Result**:
  - Store execution throws an error.
  - `error` state is populated with `"Sai mật khẩu"`.
  - `isAuthenticated` remains `false`.

### AUTH-FE-005: Successful Registration
- **Description**: Verifies register operations successfully populate credentials and tokens.
- **Preconditions**: `authService.register` mocked to resolve successfully with a new token and customer data.
- **Action**: Call `authStore.register({ email: "new@test.com", password: "password" })`.
- **Expected Result**:
  - `token` is cached, and `isAuthenticated` is set to `true`.
  - `error` remains `null`.

### AUTH-FE-006: Successful Logout
- **Description**: Verifies state cleanup upon user sign-out.
- **Preconditions**: User is logged in; token is saved in localStorage.
- **Action**: Call `authStore.logout()`.
- **Expected Result**:
  - `user` and `token` are set to `null`.
  - `isAuthenticated` is `false`.
  - Token is deleted from local storage.
  - Cart is cleared and user bound is reset.

---

## 2. Basket & Cart Operations (`CartStore`)

### CART-FE-001: Initial State
- **Description**: Verifies cart initialization parameters.
- **Preconditions**: None.
- **Action**: Instantiate `useCartStore()`.
- **Expected Result**:
  - `items` array is empty.
  - `itemCount` is `0`.
  - `subtotal` is `0`.
  - `note` is `""`.

### CART-FE-002: Add New Item to Cart
- **Description**: Adding a food item creates a basket entry.
- **Preconditions**: Cart is empty.
- **Action**: Call `cartStore.addItem(mockItem)` where `mockItem = { id: 1, name: "Bún bò Huế", price: 45000, restaurantId: 10 }`.
- **Expected Result**:
  - `items` contains exactly 1 entry.
  - `items[0].quantity` is `1`.
  - `subtotal` matches item price (`45000`).

### CART-FE-003: Increment Existing Item
- **Description**: Adding an identical food item increments quantity rather than creating duplicate entries.
- **Preconditions**: `mockItem` already exists in `cartStore`.
- **Action**: Call `cartStore.addItem(mockItem)`.
- **Expected Result**:
  - `items` length remains `1`.
  - `items[0].quantity` increments to `2`.
  - `subtotal` updates to `90000`.

### CART-FE-004: Multi-Restaurant Conflict Handling
- **Description**: Enforces that users cannot add dishes from different restaurants to the same active cart without warning and confirmation.
- **Preconditions**: Cart contains `mockItem` from `restaurantId: 10`. `window.confirm` is stubbed to return `true` (user confirms replacement).
- **Action**: Call `cartStore.addItem(mockItemOtherRestaurant)` from `restaurantId: 99`.
- **Expected Result**:
  - `window.confirm` is triggered.
  - Cart is wiped, and the new item from restaurant 99 is added as the sole item.

### CART-FE-005: Increment & Decrement Quantity
- **Description**: Verifies quantity adjustment controls.
- **Preconditions**: Cart has `mockItem` with quantity `2`.
- **Action**: Call `cartStore.incrementQuantity(lineId)` followed by `cartStore.decrementQuantity(lineId)`.
- **Expected Result**:
  - Quantity goes to `3` then returns to `2`.
  - Decrementing when quantity is `1` leaves the item at `1` (or removes depending on boundary triggers).

### CART-FE-006: Remove Item
- **Description**: Wipes a specific line item.
- **Preconditions**: Cart has `mockItem`.
- **Action**: Call `cartStore.removeItem(lineId)`.
- **Expected Result**:
  - Item is deleted from `items` array.
  - `subtotal` resets to `0`.

---

## 3. Order Caching & Management (`OrderStore`)

### ORD-FE-001: Fetch User Orders
- **Description**: Verifies fetching orders from the server.
- **Preconditions**: `orderService.getByUser` mocked to return list of orders.
- **Action**: Call `orderStore.fetchUserOrders()`.
- **Expected Result**:
  - `orders` state populated with fetched items.
  - `isLoading` changes from `true` to `false`.

### ORD-FE-002: Active Orders Filter
- **Description**: Filters ongoing orders from completed or cancelled ones.
- **Preconditions**: `orders` has items with status `PENDING`, `DELIVERED`, and `SHIPPING`.
- **Action**: Access getter `orderStore.activeOrders`.
- **Expected Result**:
  - Returns only items with `PENDING` and `SHIPPING` statuses.
  - `DELIVERED` status item is excluded.

### ORD-FE-003: Order Cancellation
- **Description**: Cancels order on server and updates local states.
- **Preconditions**: `orderService.cancel` mocked to return cancelled order status.
- **Action**: Call `orderStore.cancelOrder(101)`.
- **Expected Result**:
  - Corresponding order in `orders` list transitions to status `CANCELLED`.
  - `currentOrder.status` updates to `CANCELLED`.

---

## 4. Notifications (`NotificationStore`)

### NTF-FE-001: Initial State & Push
- **Description**: Pushing a toast appends it correctly.
- **Preconditions**: None.
- **Action**: Call `notificationStore.pushNotification({ title: "New Order", message: "Success" })`.
- **Expected Result**:
  - `items` has length `1`.
  - Default notification type falls back to `SYSTEM`.
  - `unreadCount` is `1`.

### NTF-FE-002: Mark Read & Clear
- **Description**: Transitioning unread statuses and clearing list.
- **Preconditions**: Notifications exist.
- **Action**: Call `markAsRead(id)` or `markAllAsRead()` or `clearNotifications()`.
- **Expected Result**:
  - `markAsRead` decrements `unreadCount`.
  - `clearNotifications` empties the `items` array completely.

---

## 5. UI Component Specifications (Unit specs)

### COMP-FE-001: CartView Rendering
- **Description**: Verifies component renders correctly for both empty and populated states.
- **Preconditions**: Mock Pinia store states.
- **Action**: Mount `CartView.vue`.
- **Expected Result**:
  - Renders a "Giỏ hàng" header.
  - Displays "Giỏ hàng trống" and a "Khám phá ngay" redirection button when empty.
  - Displays restaurant name, item details, quantity controls, and total subtotal when populated.

### COMP-FE-002: NotFoundView Redirect
- **Description**: Verifies 404 handler and redirection.
- **Preconditions**: None.
- **Action**: Mount `NotFoundView.vue` and click the "Về trang chủ" button.
- **Expected Result**:
  - Renders a 404 message.
  - Router pushes redirection path `/`.

---

## 6. Frontend End-to-End (E2E) Journeys (Cypress)

### E2E-FE-001: User Login & Registration Flow (`auth.cy.js`)
- **Description**: Complete signup and login portal simulation.
- **Preconditions**: Backend auth endpoints are stubbed/mocked.
- **Action**: 
  1. Visit `/`
  2. Click "Đăng nhập" button to open Modal.
  3. Swap tabs to "Đăng ký".
  4. Submit valid registration fields.
  5. Enter credentials on login side and submit.
- **Expected Result**:
  - Unmatched passwords on registration throw UI validation errors.
  - Duplicate registration email yields a 409 error message from backend stub.
  - Successful login caches JWT in localStorage, redirects user to customer `/browse` panel.

### E2E-FE-002: Product Discovery & Filtering (`browse.cy.js`)
- **Description**: Verifies browsing, search, and category filter behaviors.
- **Preconditions**: User logged in; `/browse` page visited.
- **Action**:
  1. Input `"Phở"` in the search bar.
  2. Click category tag (e.g. "Bún/Mỳ").
- **Expected Result**:
  - Search bar popover displays items with matching names.
  - Inexistent searches render an empty/no-result feedback.
  - Category tags successfully filter out-of-category restaurant listings.

### E2E-FE-003: Basket Manipulation & Checkout (`cart.cy.js`)
- **Description**: Interactive shopping cart E2E test.
- **Preconditions**: User is logged in; cart has items.
- **Action**:
  1. Visit `/cart`.
  2. Click `+` and `-` quantity adjust buttons.
  3. Add items to exceed `100,000 ₫`.
- **Expected Result**:
  - Quantity values decrement and increment responsively.
  - Exceeding the `100,000 ₫` threshold dynamically computes and displays a `20,000 ₫` discount amount.
  - Total checkout calculation reflects `subtotal + deliveryFee - discountAmount`.

### E2E-FE-004: Live Order Tracking (`orders.cy.js`)
- **Description**: Subscribes and displays real-time order progression.
- **Preconditions**: Order exists in database with shipping status.
- **Action**: Visit `/orders/502/tracking`.
- **Expected Result**:
  - Order card displays specific transaction ID `#502`.
  - Active progress node highlight correctly targets "Đang giao" (SHIPPING).
  - Leaflet map initializes ready to receive driver WebSocket signals.
