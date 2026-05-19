# DeliveryServiceImplTest Detailed Tests

## `shouldCreateUnassignedAssignment`

**Scenario (Positive):** Create Unassigned Assignment

- **Expected Outcomes:**
  - Verifies dependency interaction: `deliveryAssignmentRepository.save`

## `shouldNotCreateUnassignedAssignmentIfAlreadyExists`

**Scenario (Positive):** Not Create Unassigned Assignment If Already Exists

- **Expected Outcomes:**
  - Implicit success (Completes execution without throwing exceptions).

## `shouldAssignShipperSuccessfully`

**Scenario (Positive):** Assign Shipper Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `response`
  - State/Persistence Assertion: `OrderStatus.SHIPPING.name(), order.getStatus()`
  - Verifies dependency interaction: `orderRepository.save`
  - Verifies dependency interaction: `deliveryAssignmentRepository.save`

## `shouldThrowExceptionWhenAssigningNonShipperRole`

**Scenario (Negative):** Throw Exception When Assigning Non Shipper Role
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> deliveryService.assignShipper(shipperId, "ROLE_SHIPPER", request)`
  - State/Persistence Assertion: `HttpStatus.BAD_REQUEST, exception.getStatus()`
  - State/Persistence Assertion: `"INVALID_ROLE", exception.getErrorCode()`

## `shouldMarkOrderAsPickedUpSuccessfully`

**Scenario (Positive):** Mark Order As Picked Up Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `DeliveryAssignmentStatus.PICKED_UP.name(), assignment.getStatus()`
  - State/Persistence Assertion: `assignment.getPickedUpAt()`
  - Verifies dependency interaction: `deliveryAssignmentRepository.save`

## `shouldThrowExceptionWhenMarkingPickedUpUnauthorized`

**Scenario (Security/Negative):** Throw Exception When Marking Picked Up Unauthorized
**Security Validation:** Verifies IDOR protection, Role-Based Access Control, or unauthorized state mutation prevention.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> deliveryService.markPickedUp(999L, orderId, request)`
  - State/Persistence Assertion: `HttpStatus.FORBIDDEN, exception.getStatus()`

## `shouldMarkOrderAsDeliveredSuccessfully`

**Scenario (Positive):** Mark Order As Delivered Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `DeliveryAssignmentStatus.DELIVERED.name(), assignment.getStatus()`
  - State/Persistence Assertion: `OrderStatus.DELIVERED.name(), order.getStatus()`
  - State/Persistence Assertion: `order.getIsPaid()`
  - State/Persistence Assertion: `assignment.getDeliveredAt()`
  - Verifies dependency interaction: `deliveryAssignmentRepository.save`
  - Verifies dependency interaction: `orderRepository.save`

## `shouldThrowExceptionWhenMarkingDeliveredWithoutCod`

**Scenario (Negative):** Throw Exception When Marking Delivered Without Cod
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> deliveryService.markDelivered(shipperId, orderId, request)`
  - State/Persistence Assertion: `HttpStatus.BAD_REQUEST, exception.getStatus()`
  - State/Persistence Assertion: `"COD_NOT_COLLECTED", exception.getErrorCode()`

## `shouldCreateNewLocationWhenUpdatingLocationForFirstTime`

**Scenario (Positive):** Create New Location When Updating Location For First Time

- **Expected Outcomes:**
  - Verifies dependency interaction: `shipperLocationRepository.save`

## `shouldUpdateExistingLocationWhenUpdatingLocation`

**Scenario (Positive):** Update Existing Location When Updating Location

- **Expected Outcomes:**
  - State/Persistence Assertion: `new BigDecimal("11.0"), existingLocation.getLatitude()`
  - State/Persistence Assertion: `existingLocation.getIsOnline()`
  - Verifies dependency interaction: `shipperLocationRepository.save`

## `shouldGetShipperLocationSuccessfullyForCustomer`

**Scenario (Positive):** Get Shipper Location Successfully For Customer

- **Expected Outcomes:**
  - State/Persistence Assertion: `response`
  - State/Persistence Assertion: `new BigDecimal("1.0"), response.getLatitude()`

## `shouldThrowExceptionWhenGettingShipperLocationUnauthorized`

**Scenario (Security/Negative):** Throw Exception When Getting Shipper Location Unauthorized
**Security Validation:** Verifies IDOR protection, Role-Based Access Control, or unauthorized state mutation prevention.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> deliveryService.getShipperLocation(shipperId, 99L)`

## `shouldGetAvailableDeliveries`

**Scenario (Positive):** Get Available Deliveries

- **Expected Outcomes:**
  - State/Persistence Assertion: `1, result.size()`

## `shouldGetMyDeliveries`

**Scenario (Positive):** Get My Deliveries

- **Expected Outcomes:**
  - State/Persistence Assertion: `1, result.size()`

## `shouldGetByOrderId`

**Scenario (Positive):** Get By Order Id

- **Expected Outcomes:**
  - State/Persistence Assertion: `response`

## `shouldAssignShipperAsAdmin`

**Scenario (Positive):** Assign Shipper As Admin

- **Expected Outcomes:**
  - Verifies dependency interaction: `userRepository.findById`

## `shouldAssignShipperSelfWhenRequesterIsShipper`

**Scenario (Positive):** Assign Shipper Self When Requester Is Shipper

- **Expected Outcomes:**
  - Verifies dependency interaction: `userRepository.findById`

## `shouldThrowExceptionWhenMarkingPickedUpInvalidState`

**Scenario (Negative):** Throw Exception When Marking Picked Up Invalid State
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> deliveryService.markPickedUp(shipperId, orderId, new MarkPickupRequest())`

## `shouldThrowExceptionWhenMarkingDeliveredInvalidState`

**Scenario (Negative):** Throw Exception When Marking Delivered Invalid State
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> deliveryService.markDelivered(shipperId, orderId, new MarkDeliveredRequest())`

## `shouldAllowAdminToViewShipperLocation`

**Scenario (Positive):** Allow Admin To View Shipper Location

- **Expected Outcomes:**
  - State/Persistence Assertion: `deliveryService.getShipperLocation(shipperId, 99L)`

## `shouldAllowShipperToViewOwnLocation`

**Scenario (Positive):** Allow Shipper To View Own Location

- **Expected Outcomes:**
  - State/Persistence Assertion: `deliveryService.getShipperLocation(shipperId, shipperId)`

## `shouldAllowAdminToViewAssignment`

**Scenario (Positive):** Allow Admin To View Assignment

- **Expected Outcomes:**
  - State/Persistence Assertion: `deliveryService.getByOrderId(orderId, 99L)`

## `shouldAllowCustomerToViewAssignment`

**Scenario (Positive):** Allow Customer To View Assignment

- **Expected Outcomes:**
  - State/Persistence Assertion: `deliveryService.getByOrderId(orderId, customer.getId())`

## `shouldThrowExceptionWhenViewingAssignmentUnauthorized`

**Scenario (Security/Negative):** Throw Exception When Viewing Assignment Unauthorized
**Security Validation:** Verifies IDOR protection, Role-Based Access Control, or unauthorized state mutation prevention.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> deliveryService.getByOrderId(orderId, 999L)`

## `shouldThrowExceptionWhenOrderNotFoundInAssign`

**Scenario (Negative):** Throw Exception When Order Not Found In Assign
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `ResourceNotFoundException.class, () -> deliveryService.assignShipper(shipperId, Role.ROLE_SHIPPER, new AssignShipperR...

## `shouldThrowExceptionWhenShipperNotFoundInUpdateLocation`

**Scenario (Negative):** Throw Exception When Shipper Not Found In Update Location
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `ResourceNotFoundException.class, () -> deliveryService.updateLocation(shipperId, new ShipperLocationUpdateRequest())`
