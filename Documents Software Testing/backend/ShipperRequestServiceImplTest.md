# ShipperRequestServiceImplTest Detailed Tests

## `shouldSubmitRequestSuccessfully`

**Scenario (Positive):** Submit Request Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `response`
  - State/Persistence Assertion: `ShipperRequestStatus.PENDING, response.getStatus()`
  - Verifies dependency interaction: `shipperRequestRepository.save`

## `shouldThrowExceptionWhenPendingRequestExists`

**Scenario (Negative):** Throw Exception When Pending Request Exists
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> shipperRequestService.submitRequest(userId, submission)`

## `shouldProcessApprovalSuccessfully`

**Scenario (Positive):** Process Approval Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `ShipperRequestStatus.APPROVED, response.getStatus()`
  - State/Persistence Assertion: `Role.ROLE_SHIPPER, user.getRole()`
  - Verifies dependency interaction: `shipperLocationRepository.save`
  - Verifies dependency interaction: `userRepository.save`

## `shouldProcessRejectionSuccessfully`

**Scenario (Positive):** Process Rejection Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `ShipperRequestStatus.REJECTED, response.getStatus()`
  - State/Persistence Assertion: `Role.ROLE_SHIPPER, user.getRole()`

## `shouldThrowExceptionWhenProcessingAlreadyProcessedRequest`

**Scenario (Negative):** Throw Exception When Processing Already Processed Request
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> shipperRequestService.processRequest(requestId, approval)`

## `shouldThrowExceptionWhenUserNotFound`

**Scenario (Negative):** Throw Exception When User Not Found
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `ResourceNotFoundException.class, () -> shipperRequestService.submitRequest(userId, new ShipperRequestSubmission())`

## `shouldThrowExceptionWhenRequestNotFound`

**Scenario (Negative):** Throw Exception When Request Not Found
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `ResourceNotFoundException.class, () -> shipperRequestService.processRequest(requestId, new ShipperRequestApproval())`

## `shouldNotCreateLocationIfAlreadyExistsOnApproval`

**Scenario (Positive):** Not Create Location If Already Exists On Approval

- **Expected Outcomes:**
  - Implicit success (Completes execution without throwing exceptions).

## `shouldGetAllPendingRequests`

**Scenario (Positive):** Get All Pending Requests

- **Expected Outcomes:**
  - State/Persistence Assertion: `1, result.size()`

## `shouldGetUserRequests`

**Scenario (Positive):** Get User Requests

- **Expected Outcomes:**
  - State/Persistence Assertion: `1, result.size()`
