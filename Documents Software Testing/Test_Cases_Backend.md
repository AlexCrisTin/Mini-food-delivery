# Detailed Test Cases - Backend

This document provides a comprehensive and detailed breakdown of the backend test cases, organized by domain. Each test case specifies the exact preconditions, actions to be taken, and the precise expected results to ensure clear verifiability.

## 1. Authentication Service (`AuthService`)

### AUTH-001: Successful Login

- **Description**: Verifies that a registered user with correct credentials can successfully authenticate and receive a token pair.
- **Preconditions**: User exists in DB with `email`, hashed `password`, and `is_active=true`. `failed_login_attempts` is an arbitrary number.
- **Action**: POST `/api/auth/login` with valid `email` and `password`.
- **Expected Result**:
  - HTTP 200 OK.
  - Response body contains a valid JWT (`accessToken`), a `refreshToken`, and user details (role, email).
  - Database record for user resets `failed_login_attempts` to 0.
  - Database record for user has `account_locked_until` set to `null`.

### AUTH-002: Login with Invalid Credentials

- **Description**: Verifies that incorrect passwords are rejected and brute-force tracking increments.
- **Preconditions**: User exists in DB. `failed_login_attempts` is currently `N` (where N < 4).
- **Action**: POST `/api/auth/login` with valid `email` but incorrect `password`.
- **Expected Result**:
  - HTTP 401 UNAUTHORIZED.
  - Error code `AUTH_FAILED`.
  - Database record for user increments `failed_login_attempts` to `N+1`.

### AUTH-003: Registration with Existing Email

- **Description**: Prevents multiple accounts from using the same email address.
- **Preconditions**: User already exists in DB with `email=test@test.com`.
- **Action**: POST `/api/auth/register` with `email=test@test.com` and a valid payload.
- **Expected Result**:
  - HTTP 400 BAD_REQUEST.
  - Error code `EMAIL_EXISTS`.
  - No new user is persisted to the database.

### AUTH-004: Registration with Weak Password

- **Description**: Enforces password complexity rules at the API layer.
- **Preconditions**: None.
- **Action**: POST `/api/auth/register` with `password="123"`.
- **Expected Result**:
  - HTTP 400 BAD_REQUEST.
  - Validation error indicating password length/complexity requirements.

### AUTH-005: Token Refresh (Valid)

- **Description**: Allows a user to obtain a new JWT using a valid refresh token without re-entering credentials.
- **Preconditions**: User has previously logged in. A valid, unexpired `RefreshToken` exists in the DB linked to the user.
- **Action**: POST `/api/auth/refresh` with the valid `refreshToken`.
- **Expected Result**:
  - HTTP 200 OK.
  - Response contains a new JWT (`accessToken`).
  - The same `refreshToken` is returned (or rotated, depending on exact config).

### AUTH-006: Token Refresh (Expired/Invalid)

- **Description**: Prevents obtaining a new JWT if the refresh token is expired or manipulated.
- **Preconditions**: A `RefreshToken` exists in the DB but its `expiryDate` is in the past.
- **Action**: POST `/api/auth/refresh` with the expired token.
- **Expected Result**:
  - HTTP 403 FORBIDDEN.
  - Error code `EXPIRED_REFRESH_TOKEN`.
  - The expired token is deleted from the DB.

### AUTH-007: Account Lockout (5 Failed Attempts)

- **Description**: Protects accounts against brute-force guessing by locking them out after 5 failures.
- **Preconditions**: User exists in DB with `failed_login_attempts=4`.
- **Action**: POST `/api/auth/login` with incorrect password.
- **Expected Result**:
  - HTTP 401 UNAUTHORIZED (or 403 depending on exact error mapping).
  - Error code `ACCOUNT_LOCKED`.
  - Database record updates `failed_login_attempts` to 5.
  - Database record updates `account_locked_until` to `NOW() + 15 minutes`.

### AUTH-008: Login during Lockout Period

- **Description**: Prevents login attempts even with correct credentials if the account is currently locked.
- **Preconditions**: User exists in DB with `account_locked_until` set to a future time.
- **Action**: POST `/api/auth/login` with correct `email` and correct `password`.
- **Expected Result**:
  - HTTP 401/403 with error code `ACCOUNT_LOCKED`.
  - Spring Security AuthenticationManager is *never* invoked.

### AUTH-009: Login after Lockout Expires

- **Description**: Restores access automatically once the lockout period has elapsed.
- **Preconditions**: User exists in DB with `account_locked_until` set to a past time and `failed_login_attempts=5`.
- **Action**: POST `/api/auth/login` with correct `email` and correct `password`.
- **Expected Result**:
  - HTTP 200 OK.
  - Valid token pair returned.
  - Database record resets `failed_login_attempts` to 0 and `account_locked_until` to `null`.

---

## 2. Restaurant & Menu Management

### RM-001: Create Restaurant (Owner)

- **Description**: Verifies that a user with the OWNER role can create a new restaurant profile.
- **Preconditions**: Authenticated user has `Role.ROLE_OWNER`.
- **Action**: POST `/api/restaurants` with valid restaurant details.
- **Expected Result**:
  - HTTP 201 CREATED.
  - Restaurant is persisted to DB with `is_approved=false` (PENDING state).
  - Response contains the created restaurant ID.

### RM-002: Update Restaurant (Not Owner)

- **Description**: IDOR protection to ensure owners can only edit their own restaurants.
- **Preconditions**: User A (Owner) owns Restaurant X. User B is authenticated.
- **Action**: User B sends PUT/PATCH to `/api/restaurants/{id_of_X}`.
- **Expected Result**:
  - HTTP 403 FORBIDDEN.
  - Error code `UNAUTHORIZED_RESTAURANT_ACCESS`.

### RM-003: Soft Delete Restaurant

- **Description**: Ensures restaurants are logically deleted rather than physically removed to preserve order history.
- **Preconditions**: User A owns Restaurant X.
- **Action**: User A sends DELETE to `/api/restaurants/{id_of_X}`.
- **Expected Result**:
  - HTTP 204 NO_CONTENT.
  - Database record for Restaurant X has `is_deleted=true`.
  - Restaurant X no longer appears in public GET `/api/restaurants` searches.

### RM-004: Add Menu Item to Category

- **Description**: Verifies menu items can be added to valid categories within a restaurant.
- **Preconditions**: Restaurant X has Category Y. User A owns Restaurant X.
- **Action**: POST `/api/menus/items` with valid payload referencing Category Y.
- **Expected Result**:
  - HTTP 201 CREATED.
  - Item is persisted and linked to Category Y.

### RM-005: Add Item to Another Restaurant's Category

- **Description**: Prevents adding items to categories that belong to a different restaurant.
- **Preconditions**: User A owns Restaurant X. Restaurant Z (owned by User B) has Category W.
- **Action**: User A sends POST `/api/menus/items` referencing Category W (or referencing Restaurant X but Category W).
- **Expected Result**:
  - HTTP 400 BAD_REQUEST or 403 FORBIDDEN.
  - Error code `INVALID_CATEGORY`.

### RM-006: Search Restaurant by Category

- **Description**: Validates the public API's filtering capabilities.
- **Preconditions**: Multiple approved restaurants exist in various categories.
- **Action**: GET `/api/restaurants?categoryId={id}`.
- **Expected Result**:
  - HTTP 200 OK.
  - Returned list contains *only* restaurants matching the `categoryId` and `is_approved=true` and `is_deleted=false`.

### RM-007: Search with Keywords and Pagination

- **Description**: Validates pagination and text search logic.
- **Preconditions**: 15 restaurants exist with the word "Pizza" in their name.
- **Action**: GET `/api/restaurants?keyword=Pizza&page=0&size=10`.
- **Expected Result**:
  - HTTP 200 OK.
  - Response is a `PageResponse` containing exactly 10 items.
  - `totalElements` is 15, `totalPages` is 2.

### RM-008: Get Deleted Restaurant Detail

- **Description**: Ensures soft-deleted restaurants cannot be accessed directly by customers.
- **Preconditions**: Restaurant X exists but `is_deleted=true`.
- **Action**: GET `/api/restaurants/{id_of_X}`.
- **Expected Result**:
  - HTTP 404 NOT_FOUND.
  - Error code `RESOURCE_NOT_FOUND`.

---

## 3. Order Management

### ORD-001: Create Order (Successful)

- **Description**: Validates the end-to-end order creation process, including distance calculation and snapshotting.
- **Preconditions**: Valid Customer user. Valid Restaurant. Valid MenuItems. External Map API is reachable.
- **Action**: POST `/api/orders` with cart items and delivery coordinates.
- **Expected Result**:
  - HTTP 201 CREATED.
  - Order is persisted with `status=PENDING`.
  - Delivery fee is dynamically calculated via MapService.
  - OrderItems are created in the DB representing a snapshot of the menu items at that exact time.

### ORD-002: Map API Failure during Order

- **Description**: Ensures the system remains resilient if the external Map API goes down.
- **Preconditions**: MapService is mocked to throw an exception or timeout.
- **Action**: POST `/api/orders`.
- **Expected Result**:
  - HTTP 201 CREATED.
  - Order is successfully created.
  - Delivery fee falls back to the configured system default (e.g., 15000 VND).
  - A warning is logged, but the user is not impacted.

### ORD-003: Transition PENDING -> CONFIRMED

- **Description**: Verifies that a restaurant owner can accept an incoming order.
- **Preconditions**: Order exists in `PENDING` state. User is the Owner of the respective restaurant.
- **Action**: PATCH `/api/orders/{id}/status` with `status=CONFIRMED`.
- **Expected Result**:
  - HTTP 200 OK.
  - Order status updates to `CONFIRMED` in the DB.
  - An `OrderStatusHistory` record is created noting the transition and the actor.

### ORD-004: Transition SHIPPING -> CANCELLED

- **Description**: Enforces the business rule that orders in transit cannot be cancelled.
- **Preconditions**: Order exists in `SHIPPING` state.
- **Action**: PATCH `/api/orders/{id}/status` with `status=CANCELLED`.
- **Expected Result**:
  - HTTP 400 BAD_REQUEST.
  - Error code `INVALID_TRANSITION`.

### ORD-005: View Order Details (Unauthorized)

- **Description**: IDOR protection for order privacy.
- **Preconditions**: User A (Customer) placed Order X. User B is authenticated.
- **Action**: User B sends GET `/api/orders/{id_of_X}`.
- **Expected Result**:
  - HTTP 403 FORBIDDEN.

### ORD-006: Concurrent Update (Optimistic Lock)

- **Description**: Prevents race conditions (e.g., Owner and Customer trying to cancel the order at the exact same millisecond).
- **Preconditions**: Order X exists with `@Version` = 1.
- **Action**: Two concurrent requests attempt to update Order X. Request 1 succeeds and bumps Version to 2. Request 2 submits with Version 1.
- **Expected Result**:
  - Request 1: HTTP 200 OK.
  - Request 2: HTTP 409 CONFLICT (GlobalExceptionHandler catches `ObjectOptimisticLockingFailureException`).

---

## 4. Delivery & Tracking

### DEL-001: Auto-create Assignment on READY

- **Description**: Verifies the Event-Driven architecture decoupling orders from delivery logic.
- **Preconditions**: Order is in `CONFIRMED` state.
- **Action**: Owner transitions order to `READY`.
- **Expected Result**:
  - `OrderReadyEvent` is published.
  - `TransactionalEventListener` intercepts the event *after commit*.
  - A `DeliveryAssignment` is automatically created in the DB with status `UNASSIGNED`.

### DEL-002: Shipper Self-Assigns Order

- **Description**: Allows a shipper to claim an available delivery.
- **Preconditions**: `DeliveryAssignment` exists with status `UNASSIGNED`. User has `ROLE_SHIPPER`.
- **Action**: POST `/api/delivery/assign` with the order ID.
- **Expected Result**:
  - HTTP 200 OK.
  - Assignment status updates to `ASSIGNED`.
  - Linked Order status automatically updates to `SHIPPING`.

### DEL-003: Mark Delivered without COD

- **Description**: Enforces the collection of Cash on Delivery before finalizing an order.
- **Preconditions**: Order is `SHIPPING`. Payment method is `CASH`.
- **Action**: Shipper sends PATCH `/api/delivery/{id}/deliver` with `codCollected=false`.
- **Expected Result**:
  - HTTP 400 BAD_REQUEST.
  - Error code `COD_NOT_COLLECTED`.
  - Order and Assignment statuses remain unchanged.

### DEL-004: Update Location (WebSocket)

- **Description**: Verifies real-time location tracking payload persistence.
- **Preconditions**: Shipper is assigned to an active order.
- **Action**: Shipper sends coordinates via STOMP/WebSocket to the server.
- **Expected Result**:
  - `ShipperLocation` table is updated with the new lat/lng.
  - The payload is broadcasted out to subscribers on `/topic/order/{id}`.

### DEL-005: Access Location (No active order)

- **Description**: Privacy protection for shippers; customers can only see shippers actively delivering to them.
- **Preconditions**: Shipper is online but NOT assigned to Customer A's order.
- **Action**: Customer A sends GET `/api/delivery/location/shipper/{id}`.
- **Expected Result**:
  - HTTP 403 FORBIDDEN.

---

## 5. Admin & Workflows

### ADM-001: Approve Owner Request

- **Description**: Admin workflow to onboard new restaurant partners.
- **Preconditions**: User has submitted an `OwnerRequest`. Status is `PENDING`.
- **Action**: Admin sends POST `/api/admin/requests/owner/{id}/approve`.
- **Expected Result**:
  - HTTP 200 OK.
  - Request status -> `APPROVED`.
  - User's role is updated to `ROLE_OWNER` in the DB.
  - A new `Restaurant` entity is automatically scaffolded for the user.
  - System notification is sent to the user.

### ADM-002: Reject Owner Request

- **Description**: Admin workflow to decline applications.
- **Preconditions**: `OwnerRequest` is `PENDING`.
- **Action**: Admin sends POST `/api/admin/requests/owner/{id}/reject` with a reason.
- **Expected Result**:
  - Request status -> `REJECTED`.
  - User role remains unchanged.
  - System notification is sent to the user including the rejection reason.

### ADM-003: Generate System-wide Revenue Report

- **Description**: Verifies aggregation queries in the repository layer.
- **Preconditions**: Multiple DELIVERED orders exist across different restaurants within a date range.
- **Action**: Admin sends GET `/api/admin/reports/summary?startDate=...&endDate=...`.
- **Expected Result**:
  - HTTP 200 OK.
  - Response contains accurate sum of `total_amount` grouped by parameters.

### ADM-004: Change User Role (Admin)

- **Description**: Verifies direct admin override of user roles.
- **Preconditions**: User A is `CUSTOMER`. Admin is authenticated.
- **Action**: Admin sends PATCH `/api/admin/users/{id_of_A}/role` with `role=ADMIN`.
- **Expected Result**:
  - HTTP 200 OK.
  - User A's role is updated in the DB.

### ADM-005: Submit Duplicate Owner Request

- **Description**: Prevents spamming the admin queue.
- **Preconditions**: User A already has a `PENDING` Owner Request.
- **Action**: User A sends POST `/api/requests/owner`.
- **Expected Result**:
  - HTTP 400 BAD_REQUEST.
  - Error code `PENDING_REQUEST_EXISTS`.

---

## 6. Real-time Notifications

### NOT-001: Order Status Change Notification

- **Description**: Validates that system actions trigger user notifications.
- **Preconditions**: Customer places an order. Owner CONFIRMs it.
- **Action**: The transition logic executes.
- **Expected Result**:
  - A `Notification` entity is created in the DB linked to the Customer.
  - If connected to WebSocket, the notification is pushed down the socket.

### NOT-002: Mark Notification as Read

- **Description**: Standard notification lifecycle.
- **Preconditions**: Customer has an unread notification X.
- **Action**: Customer sends PATCH `/api/notifications/{id_of_X}/read`.
- **Expected Result**:
  - HTTP 200 OK.
  - DB record for notification X has `is_read=true`.

---

## 7. Validation & Input Integrity

### VAL-001: Create Restaurant with Negative Latitude

- **Description**: Validates coordinate boundaries at the DTO level.
- **Preconditions**: None.
- **Action**: POST `/api/restaurants` with `latitude=-100.0`.
- **Expected Result**:
  - HTTP 400 BAD_REQUEST.
  - `@Valid` throws MethodArgumentNotValidException. Global handler returns structured validation error messages.

### VAL-002: Menu Item Price set to Zero

- **Description**: Business rule validation for pricing.
- **Preconditions**: Owner is authenticated.
- **Action**: POST `/api/menus/items` with `price=0`.
- **Expected Result**:
  - HTTP 400 BAD_REQUEST.
  - Field error specifically targeting the `price` field (must be > 0).
