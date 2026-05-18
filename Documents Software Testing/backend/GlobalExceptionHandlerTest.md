# GlobalExceptionHandlerTest Detailed Tests

## `shouldHandleResourceNotFound`

**Scenario (Positive):** Handle Resource Not Found

- **Expected Outcomes:**
  - State/Persistence Assertion: `HttpStatus.NOT_FOUND, response.getStatusCode()`
  - State/Persistence Assertion: `response.getBody()`
  - State/Persistence Assertion: `response.getBody().isSuccess()`
  - State/Persistence Assertion: `response.getBody().getMessage().contains("User not found with id : '1'")`
  - State/Persistence Assertion: `"RESOURCE_NOT_FOUND", response.getBody().getErrorCode()`

## `shouldHandleAppException`

**Scenario (Positive):** Handle App Exception

- **Expected Outcomes:**
  - State/Persistence Assertion: `HttpStatus.BAD_REQUEST, response.getStatusCode()`
  - State/Persistence Assertion: `response.getBody()`
  - State/Persistence Assertion: `response.getBody().isSuccess()`
  - State/Persistence Assertion: `"Business error", response.getBody().getMessage()`
  - State/Persistence Assertion: `"BUSINESS_ERROR", response.getBody().getErrorCode()`

## `shouldHandleAccessDenied`

**Scenario (Positive):** Handle Access Denied

- **Expected Outcomes:**
  - State/Persistence Assertion: `HttpStatus.FORBIDDEN, response.getStatusCode()`
  - State/Persistence Assertion: `response.getBody()`
  - State/Persistence Assertion: `response.getBody().isSuccess()`
  - State/Persistence Assertion: `response.getBody().getMessage().contains("Access denied")`
  - State/Persistence Assertion: `"FORBIDDEN", response.getBody().getErrorCode()`

## `shouldHandleOptimisticLockingFailure`

**Scenario (Negative):** Handle Optimistic Locking Failure
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `HttpStatus.CONFLICT, response.getStatusCode()`
  - State/Persistence Assertion: `response.getBody()`
  - State/Persistence Assertion: `response.getBody().isSuccess()`
  - State/Persistence Assertion: `response.getBody().getMessage().contains("modified by another user")`
  - State/Persistence Assertion: `"CONCURRENCY_FAILURE", response.getBody().getErrorCode()`

## `shouldHandleAppExceptionWithNullStatus`

**Scenario (Positive):** Handle App Exception With Null Status

- **Expected Outcomes:**
  - State/Persistence Assertion: `HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()`
  - State/Persistence Assertion: `response.getBody()`
  - State/Persistence Assertion: `response.getBody().isSuccess()`
  - State/Persistence Assertion: `"Business error", response.getBody().getMessage()`
  - State/Persistence Assertion: `"BUSINESS_ERROR", response.getBody().getErrorCode()`

## `shouldHandleDataIntegrityViolation`

**Scenario (Positive):** Handle Data Integrity Violation

- **Expected Outcomes:**
  - State/Persistence Assertion: `HttpStatus.CONFLICT, response.getStatusCode()`
  - State/Persistence Assertion: `response.getBody()`
  - State/Persistence Assertion: `response.getBody().isSuccess()`
  - State/Persistence Assertion: `response.getBody().getMessage().contains("Database constraint violation")`
  - State/Persistence Assertion: `"DATABASE_ERROR", response.getBody().getErrorCode()`

## `shouldHandleGlobalException`

**Scenario (Positive):** Handle Global Exception

- **Expected Outcomes:**
  - State/Persistence Assertion: `HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()`
  - State/Persistence Assertion: `response.getBody()`
  - State/Persistence Assertion: `response.getBody().isSuccess()`
  - State/Persistence Assertion: `response.getBody().getMessage().contains("Unexpected error")`
  - State/Persistence Assertion: `"INTERNAL_SERVER_ERROR", response.getBody().getErrorCode()`

## `shouldHandleValidationExceptions`

**Scenario (Positive):** Handle Validation Exceptions

- **Expected Outcomes:**
  - State/Persistence Assertion: `HttpStatus.BAD_REQUEST, response.getStatusCode()`
  - State/Persistence Assertion: `response.getBody()`
  - State/Persistence Assertion: `response.getBody().isSuccess()`
  - State/Persistence Assertion: `"Validation failed", response.getBody().getMessage()`
  - State/Persistence Assertion: `"VALIDATION_ERROR", response.getBody().getErrorCode()`
  - State/Persistence Assertion: `response.getBody().getData()`
