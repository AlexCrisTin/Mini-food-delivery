# OwnerRequestControllerTest Detailed Tests

## `shouldSubmitRequestSuccessfully`

**Scenario (Positive):** Submit Request Successfully

- **Expected Outcomes:**
  - Returns HTTP Status: `isCreated`

## `shouldReturnBadRequestWhenSubmittingInvalidRequest`

**Scenario (Negative):** Return Bad Request When Submitting Invalid Request
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - Returns HTTP Status: `isBadRequest`

## `shouldGetMyRequestsSuccessfully`

**Scenario (Positive):** Get My Requests Successfully

- **Expected Outcomes:**
  - Returns HTTP Status: `isOk`

## `shouldGetPendingRequestsAsAdminSuccessfully`

**Scenario (Positive):** Get Pending Requests As Admin Successfully

- **Expected Outcomes:**
  - Returns HTTP Status: `isOk`

## `shouldReturnForbiddenWhenGettingPendingRequestsAsUser`

**Scenario (Security/Negative):** Return Forbidden When Getting Pending Requests As User
**Security Validation:** Verifies IDOR protection, Role-Based Access Control, or unauthorized state mutation prevention.

- **Expected Outcomes:**
  - Returns HTTP Status: `isForbidden`

## `shouldProcessRequestAsAdminSuccessfully`

**Scenario (Positive):** Process Request As Admin Successfully

- **Expected Outcomes:**
  - Returns HTTP Status: `isOk`

## `shouldReturnForbiddenWhenProcessingRequestAsUser`

**Scenario (Security/Negative):** Return Forbidden When Processing Request As User
**Security Validation:** Verifies IDOR protection, Role-Based Access Control, or unauthorized state mutation prevention.

- **Expected Outcomes:**
  - Returns HTTP Status: `isForbidden`
