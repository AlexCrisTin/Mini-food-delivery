# JwtUtilsTest Detailed Tests

## `shouldGenerateAndValidateToken`

**Scenario (Positive):** Generate And Validate Token

- **Expected Outcomes:**
  - State/Persistence Assertion: `token`
  - State/Persistence Assertion: `jwtUtils.validateToken(token)`
  - State/Persistence Assertion: `username, jwtUtils.getUsernameFromToken(token)`

## `shouldFailForInvalidToken`

**Scenario (Negative):** Fail For Invalid Token
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `jwtUtils.validateToken(invalidToken)`
