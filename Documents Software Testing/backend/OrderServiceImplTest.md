# OrderServiceImplTest Detailed Tests

## `shouldCreateOrderWithDistanceBasedFee`

**Scenario (Positive):** Create Order With Distance Based Fee

- **Expected Outcomes:**
  - State/Persistence Assertion: `result`
  - Verifies dependency interaction: `orderRepository.save`

## `shouldFallbackToDefaultFeeWhenMapServiceFails`

**Scenario (Negative):** Fallback To Default Fee When Map Service Fails
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `result`

## `shouldUseDefaultFeeWhenRouteIsNull`

**Scenario (Positive):** Use Default Fee When Route Is Null

- **Expected Outcomes:**
  - Verifies dependency interaction: `orderRepository.save`

## `shouldUseDefaultFeeWhenRoutesListIsEmpty`

**Scenario (Positive):** Use Default Fee When Routes List Is Empty

- **Expected Outcomes:**
  - Verifies dependency interaction: `orderRepository.save`

## `shouldGetOrderSummaryForAdmin`

**Scenario (Positive):** Get Order Summary For Admin

- **Expected Outcomes:**
  - State/Persistence Assertion: `result`

## `shouldAllowShipperToViewAssignedOrderSummary`

**Scenario (Positive):** Allow Shipper To View Assigned Order Summary

- **Expected Outcomes:**
  - State/Persistence Assertion: `result`

## `shouldThrowExceptionWhenShipperNotAssignedViewsOrder`

**Scenario (Negative):** Throw Exception When Shipper Not Assigned Views Order
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> orderService.getOrderSummary(100L, 3L)`

## `shouldThrowExceptionWhenUnassignedShipperViewsOrder`

**Scenario (Negative):** Throw Exception When Unassigned Shipper Views Order
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> orderService.getOrderSummary(100L, 3L)`

## `shouldThrowExceptionWhenUnauthorizedUserViewsOrder`

**Scenario (Security/Negative):** Throw Exception When Unauthorized User Views Order
**Security Validation:** Verifies IDOR protection, Role-Based Access Control, or unauthorized state mutation prevention.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> orderService.getOrderSummary(100L, 999L)`
  - State/Persistence Assertion: `"UNAUTHORIZED_ACCESS", exception.getErrorCode()`

## `shouldGetOrderHistory`

**Scenario (Positive):** Get Order History

- **Expected Outcomes:**
  - State/Persistence Assertion: `1, result.getItems().size()`

## `shouldUpdateOrderStatusSuccessfully`

**Scenario (Positive):** Update Order Status Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `OrderStatus.PREPARING.name(), order.getStatus()`
  - Verifies dependency interaction: `orderRepository.save`

## `shouldAllowAdminToUpdateStatus`

**Scenario (Positive):** Allow Admin To Update Status

- **Expected Outcomes:**
  - State/Persistence Assertion: `OrderStatus.CONFIRMED.name(), order.getStatus()`

## `shouldAllowCustomerToCancelOrder`

**Scenario (Positive):** Allow Customer To Cancel Order

- **Expected Outcomes:**
  - State/Persistence Assertion: `OrderStatus.CANCELLED.name(), order.getStatus()`

## `shouldThrowExceptionWhenWrongUserCancelsOrder`

**Scenario (Negative):** Throw Exception When Wrong User Cancels Order
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> orderService.updateOrderStatus(100L, 999L, request)`
  - State/Persistence Assertion: `"UNAUTHORIZED_ACCESS", exception.getErrorCode()`

## `shouldAllowRestaurantOwnerToRejectOrder`

**Scenario (Positive):** Allow Restaurant Owner To Reject Order

- **Expected Outcomes:**
  - State/Persistence Assertion: `OrderStatus.REJECTED.name(), order.getStatus()`

## `shouldAllowShipperToUpdateToShipping`

**Scenario (Positive):** Allow Shipper To Update To Shipping

- **Expected Outcomes:**
  - State/Persistence Assertion: `OrderStatus.SHIPPING.name(), order.getStatus()`

## `shouldAllowShipperToUpdateToDelivered`

**Scenario (Positive):** Allow Shipper To Update To Delivered

- **Expected Outcomes:**
  - State/Persistence Assertion: `OrderStatus.DELIVERED.name(), order.getStatus()`

## `shouldThrowExceptionWhenUnassignedShipperUpdatesToShipping`

**Scenario (Negative):** Throw Exception When Unassigned Shipper Updates To Shipping
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> orderService.updateOrderStatus(100L, 3L, request)`

## `shouldThrowExceptionWhenShipperUpdatesOtherShipperOrder`

**Scenario (Negative):** Throw Exception When Shipper Updates Other Shipper Order
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> orderService.updateOrderStatus(100L, 4L, request)`

## `shouldThrowExceptionWhenInvalidTransition`

**Scenario (Negative):** Throw Exception When Invalid Transition
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> orderService.updateOrderStatus(100L, 2L, request)`

## `shouldThrowExceptionWhenCancellingShippedOrder`

**Scenario (Negative):** Throw Exception When Cancelling Shipped Order
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> orderService.updateOrderStatus(100L, 1L, request)`
  - State/Persistence Assertion: `"INVALID_TRANSITION", exception.getErrorCode()`

## `shouldThrowExceptionWhenRejectingShippedOrder`

**Scenario (Negative):** Throw Exception When Rejecting Shipped Order
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> orderService.updateOrderStatus(100L, 2L, request)`

## `shouldThrowExceptionWhenCancellingDeliveredOrder`

**Scenario (Negative):** Throw Exception When Cancelling Delivered Order
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> orderService.updateOrderStatus(100L, 1L, request)`

## `shouldThrowExceptionWhenTransitioningFromTerminalState`

**Scenario (Negative):** Throw Exception When Transitioning From Terminal State
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> orderService.updateOrderStatus(100L, 99L, request)`

## `shouldThrowExceptionWhenStatusIsNull`

**Scenario (Negative):** Throw Exception When Status Is Null
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> orderService.updateOrderStatus(100L, 1L, request)`

## `shouldThrowExceptionWhenStatusIsInvalid`

**Scenario (Negative):** Throw Exception When Status Is Invalid
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> orderService.updateOrderStatus(100L, 1L, request)`

## `shouldPublishEventWhenOrderIsReady`

**Scenario (Positive):** Publish Event When Order Is Ready

- **Expected Outcomes:**
  - Verifies dependency interaction: `eventPublisher.publishEvent`

## `shouldGetOrderTracking`

**Scenario (Positive):** Get Order Tracking

- **Expected Outcomes:**
  - State/Persistence Assertion: `result`

## `shouldGetRestaurantOrders`

**Scenario (Positive):** Get Restaurant Orders

- **Expected Outcomes:**
  - State/Persistence Assertion: `1, result.size()`

## `shouldAllowAdminToGetRestaurantOrdersWithoutOwnershipCheck`

**Scenario (Positive):** Allow Admin To Get Restaurant Orders Without Ownership Check

- **Expected Outcomes:**
  - State/Persistence Assertion: `1, result.size()`

## `shouldThrowExceptionWhenNonOwnerGetsRestaurantOrders`

**Scenario (Security/Negative):** Throw Exception When Non Owner Gets Restaurant Orders
**Security Validation:** Verifies IDOR protection, Role-Based Access Control, or unauthorized state mutation prevention.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> orderService.getRestaurantOrders(10L, "PENDING", 99L, "OWNER")`
