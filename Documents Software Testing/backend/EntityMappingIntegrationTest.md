# EntityMappingIntegrationTest Detailed Tests

## `shouldCascadeSaveAddresses`

**Scenario (Positive):** Cascade Save Addresses

- **Expected Outcomes:**
  - State/Persistence Assertion: `savedUser.getId()`
  - State/Persistence Assertion: `1, savedUser.getAddresses().size()`
  - State/Persistence Assertion: `savedUser.getAddresses().get(0).getId()`

## `shouldThrowExceptionWhenDuplicateEmail`

**Scenario (Negative):** Throw Exception When Duplicate Email
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `DataIntegrityViolationException.class, () -> { userRepository.saveAndFlush(user2`
