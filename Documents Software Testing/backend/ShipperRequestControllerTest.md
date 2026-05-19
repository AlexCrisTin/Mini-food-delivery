# ShipperRequestControllerTest Detailed Tests

## `shouldSubmitRequestSuccessfully`

**Scenario (Positive):** Submit Request Successfully

- **Expected Outcomes:**
  - Returns HTTP Status: `isCreated`

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
