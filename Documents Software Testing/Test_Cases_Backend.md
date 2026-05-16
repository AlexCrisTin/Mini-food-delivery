# Detailed Test Cases - Backend

## 1. Authentication Service (`AuthService`)

| ID | Case | Expected Result |
|:---|:---|:---|
| AUTH-001 | Successful Login | Returns JWT, Refresh Token, user info, role CUSTOMER |
| AUTH-002 | Login with Invalid Credentials | Returns 401 UNAUTHORIZED, increments failed attempts |
| AUTH-003 | Registration with Existing Email | Returns 400 BAD_REQUEST (EMAIL_EXISTS) |
| AUTH-004 | Registration with Weak Password | Returns 400 BAD_REQUEST (Validation Error) |
| AUTH-005 | Token Refresh (Valid) | Returns new JWT and same Refresh Token |
| AUTH-006 | Token Refresh (Expired/Invalid) | Returns 403 FORBIDDEN |
| AUTH-007 | Account Lockout (5 Failed Attempts) | Account locked for 15 mins, returns 401 |
| AUTH-008 | Login during Lockout Period | Returns 401 with lockout message |
| AUTH-009 | Login after Lockout Expires | Successful login, attempts reset to 0 |

## 2. Restaurant & Menu Management

| ID | Case | Expected Result |
|:---|:---|:---|
| RM-001 | Create Restaurant (Owner) | Restaurant created, status PENDING |
| RM-002 | Update Restaurant (Not Owner) | Returns 403 FORBIDDEN |
| RM-003 | Soft Delete Restaurant | `is_deleted` set to true, not found in public search |
| RM-004 | Add Menu Item to Category | Item added, price/image validated |
| RM-005 | Add Item to Another Restaurant's Category | Returns 400 BAD_REQUEST (INVALID_CATEGORY) |
| RM-006 | Search Restaurant by Category | Returns only restaurants in that category |
| RM-007 | Search with Keywords and Pagination | Returns matching PageResponse with filtered items |
| RM-008 | Get Deleted Restaurant Detail | Returns 404 RESOURCE_NOT_FOUND |
| RM-009 | Update Menu Item (Unauthorized) | Returns 403 FORBIDDEN (UNAUTHORIZED_RESTAURANT_ACCESS) |
| RM-010 | Add Menu Category (Owner) | Category created with default sortOrder 0 |

## 3. Order Management

| ID | Case | Expected Result |
|:---|:---|:---|
| ORD-001 | Create Order (Successful) | Fee calculated, OrderItems snapshotted, Status PENDING |
| ORD-002 | Map API Failure during Order | Order created using default delivery fee fallback |
| ORD-003 | Transition PENDING -> CONFIRMED | Valid (Owner only) |
| ORD-004 | Transition SHIPPING -> CANCELLED | Returns 400 BAD_REQUEST (INVALID_TRANSITION) |
| ORD-005 | View Order Details (Unauthorized) | Returns 403 FORBIDDEN |
| ORD-006 | Concurrent Update (Optimistic Lock) | Second update fails with 409 CONCURRENCY_FAILURE |

## 4. Delivery & Tracking

| ID | Case | Expected Result |
|:---|:---|:---|
| DEL-001 | Auto-create Assignment on READY | Event triggered, UNASSIGNED assignment created |
| DEL-002 | Shipper Self-Assigns Order | Assignment status -> ASSIGNED, Order -> SHIPPING |
| DEL-003 | Mark Delivered without COD | Returns 400 BAD_REQUEST (COD_NOT_COLLECTED) |
| DEL-004 | Update Location (WebSocket) | Location persisted, broadcast to /topic/order/{id} |
| DEL-005 | Access Location (No active order) | Returns 403 FORBIDDEN |

## 5. Admin & Workflows

| ID | Case | Expected Result |
|:---|:---|:---|
| ADM-001 | Approve Owner Request | User role -> OWNER, Restaurant created automatically |
| ADM-002 | Reject Owner Request | Request status -> REJECTED, Notification sent to user |
| ADM-003 | Generate System-wide Revenue Report | Returns aggregated data by restaurant |
| ADM-004 | Export Report to CSV | Returns valid CSV file stream |
| ADM-005 | Change User Role (Admin) | User role updated in database |
| ADM-006 | Submit Duplicate Owner Request | Returns 400 BAD_REQUEST (PENDING_REQUEST_EXISTS) |
| ADM-007 | Process Already Processed Request | Returns 400 BAD_REQUEST (REQUEST_ALREADY_PROCESSED) |
| ADM-008 | Approve Shipper Request | User role -> SHIPPER, ShipperLocation created (offline) |

## 6. Real-time Notifications

| ID | Case | Expected Result |
|:---|:---|:---|
| NOT-001 | Order Status Change Notification | Notification entity created for Customer |
| NOT-002 | Mark Notification as Read | `is_read` flag set to true |
| NOT-003 | Mark All Notifications Read | All user's notifications updated |

## 7. Security Ownership Validation (IDOR Protection)

| ID | Case | Expected Result |
|:---|:---|:---|
| SEC-001 | Delete Address (Not Owner) | Returns 403 FORBIDDEN (UNAUTHORIZED_ADDRESS_ACCESS) |
| SEC-002 | Update Order Status (Random User) | Returns 403 FORBIDDEN |
| SEC-003 | Mark Notification Read (Not Recipient) | Returns 403 FORBIDDEN |
| SEC-004 | View Shipper Location (No Active Order) | Returns 403 FORBIDDEN |
