# AuthControllerTest Detailed Tests

## `shouldLoginSuccessfully`

**Scenario (Positive):** Login Successfully

- **Expected Outcomes:**
  - Returns HTTP Status: `isOk`

## `shouldRegisterSuccessfully`

**Scenario (Positive):** Register Successfully

- **Expected Outcomes:**
  - Returns HTTP Status: `isOk`

## `shouldRefreshTokenSuccessfully`

**Scenario (Positive):** Refresh Token Successfully

- **Expected Outcomes:**
  - Returns HTTP Status: `isOk`

## `shouldFailLoginWhenValidationFails`

**Scenario (Negative):** Fail Login When Validation Fails
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - Returns HTTP Status: `isBadRequest`
