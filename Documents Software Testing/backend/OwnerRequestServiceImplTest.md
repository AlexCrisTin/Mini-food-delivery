# OwnerRequestServiceImplTest Detailed Tests

## `shouldSubmitRequestSuccessfully`

**Scenario (Positive):** Submit Request Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `response`
  - State/Persistence Assertion: `OwnerRequestStatus.PENDING, response.getStatus()`
  - Verifies dependency interaction: `ownerRequestRepository.save`

## `shouldThrowExceptionWhenPendingRequestExists`

**Scenario (Negative):** Throw Exception When Pending Request Exists
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> ownerRequestService.submitRequest(userId, submission)`

## `shouldProcessApprovalSuccessfully`

**Scenario (Positive):** Process Approval Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `OwnerRequestStatus.APPROVED, response.getStatus()`
  - State/Persistence Assertion: `Role.ROLE_OWNER, user.getRole()`
  - Verifies dependency interaction: `restaurantRepository.save`
  - Verifies dependency interaction: `userRepository.save`

## `shouldProcessRejectionSuccessfully`

**Scenario (Positive):** Process Rejection Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `OwnerRequestStatus.REJECTED, response.getStatus()`
  - State/Persistence Assertion: `Role.ROLE_OWNER, user.getRole()`

## `shouldThrowExceptionWhenProcessingAlreadyProcessedRequest`

**Scenario (Negative):** Throw Exception When Processing Already Processed Request
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> ownerRequestService.processRequest(requestId, approval)`

## `shouldThrowExceptionWhenUserNotFound`

**Scenario (Negative):** Throw Exception When User Not Found
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `ResourceNotFoundException.class, () -> ownerRequestService.submitRequest(userId, new OwnerRequestSubmission())`

## `shouldThrowExceptionWhenRequestNotFound`

**Scenario (Negative):** Throw Exception When Request Not Found
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `ResourceNotFoundException.class, () -> ownerRequestService.processRequest(requestId, new OwnerRequestApproval())`

## `shouldGetAllPendingRequests`

**Scenario (Positive):** Get All Pending Requests

- **Expected Outcomes:**
  - State/Persistence Assertion: `1, result.size()`

## `shouldGetUserRequests`

**Scenario (Positive):** Get User Requests

- **Expected Outcomes:**
  - State/Persistence Assertion: `1, result.size()`
