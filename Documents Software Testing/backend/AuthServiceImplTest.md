# AuthServiceImplTest Detailed Tests

## `shouldRegisterUserSuccessfully`

**Scenario (Positive):** Register User Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `response`
  - State/Persistence Assertion: `"Bearer", response.getTokenType()`
  - State/Persistence Assertion: `"mockJwt", response.getAccessToken()`
  - State/Persistence Assertion: `"mockRefreshToken", response.getRefreshToken()`

## `shouldThrowExceptionWhenRegistrationWithExistingEmail`

**Scenario (Negative):** Throw Exception When Registration With Existing Email
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> authService.register(request)`
  - State/Persistence Assertion: `"EMAIL_EXISTS", ex.getErrorCode()`

## `shouldLoginUserSuccessfully`

**Scenario (Positive):** Login User Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `response`
  - State/Persistence Assertion: `"mockJwt", response.getAccessToken()`
  - State/Persistence Assertion: `"mockRefreshToken", response.getRefreshToken()`
  - State/Persistence Assertion: `email, response.getEmail()`
  - State/Persistence Assertion: `"CUSTOMER", response.getRole()`
  - State/Persistence Assertion: `0, user.getFailedLoginAttempts()`
  - State/Persistence Assertion: `user.getAccountLockedUntil()`

## `shouldThrowExceptionWhenLoginWithInvalidCredentials`

**Scenario (Negative):** Throw Exception When Login With Invalid Credentials
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> authService.login(request)`
  - State/Persistence Assertion: `"AUTH_FAILED", ex.getErrorCode()`
  - State/Persistence Assertion: `1, user.getFailedLoginAttempts()`

## `shouldLockAccountAfterMaxFailedAttempts`

**Scenario (Negative):** Lock Account After Max Failed Attempts
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> authService.login(request)`
  - State/Persistence Assertion: `"ACCOUNT_LOCKED", ex.getErrorCode()`
  - State/Persistence Assertion: `5, user.getFailedLoginAttempts()`
  - State/Persistence Assertion: `user.getAccountLockedUntil()`

## `shouldThrowExceptionWhenLoginDuringLockoutPeriod`

**Scenario (Negative):** Throw Exception When Login During Lockout Period
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> authService.login(request)`
  - State/Persistence Assertion: `"ACCOUNT_LOCKED", ex.getErrorCode()`

## `shouldLoginSuccessfullyAfterLockoutExpires`

**Scenario (Positive):** Login Successfully After Lockout Expires

- **Expected Outcomes:**
  - State/Persistence Assertion: `response`
  - State/Persistence Assertion: `0, user.getFailedLoginAttempts()`
  - State/Persistence Assertion: `user.getAccountLockedUntil()`

## `shouldRefreshTokenSuccessfully`

**Scenario (Positive):** Refresh Token Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `response`
  - State/Persistence Assertion: `"newAccessToken", response.getAccessToken()`
  - State/Persistence Assertion: `"validRefreshToken", response.getRefreshToken()`

## `shouldThrowExceptionWhenRefreshTokenExpired`

**Scenario (Negative):** Throw Exception When Refresh Token Expired
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> authService.refreshToken(request)`
  - State/Persistence Assertion: `"EXPIRED_REFRESH_TOKEN", ex.getErrorCode()`

## `shouldThrowExceptionWhenLoginUserNotFound`

**Scenario (Negative):** Throw Exception When Login User Not Found
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> authService.login(request)`

## `shouldThrowExceptionWhenRefreshTokenNotFound`

**Scenario (Negative):** Throw Exception When Refresh Token Not Found
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> authService.refreshToken(request)`

## `shouldThrowExceptionWhenCreateRefreshTokenUserNotFound`

**Scenario (Negative):** Throw Exception When Create Refresh Token User Not Found
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> authService.register(request)`
