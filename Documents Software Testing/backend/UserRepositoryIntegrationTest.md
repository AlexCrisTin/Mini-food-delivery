# UserRepositoryIntegrationTest Detailed Tests

## `shouldReturnUserWhenSearchingByEmail`

**Scenario (Positive):** Return User When Searching By Email

- **Expected Outcomes:**
  - State/Persistence Assertion: `found.isPresent()`
  - State/Persistence Assertion: `"Repo Test", found.get().getFullName()`

## `shouldReturnTrueWhenEmailExists`

**Scenario (Positive):** Return True When Email Exists

- **Expected Outcomes:**
  - State/Persistence Assertion: `userRepository.existsByEmail("exists@example.com")`
  - State/Persistence Assertion: `userRepository.existsByEmail("not-exists@example.com")`

## `shouldReturnActiveUsersWhenSearchingByRole`

**Scenario (Positive):** Return Active Users When Searching By Role

- **Expected Outcomes:**
  - State/Persistence Assertion: `shippers.isEmpty()`
  - State/Persistence Assertion: `shippers.stream().anyMatch(u -> u.getEmail().equals("shipper1@example.com"))`
  - State/Persistence Assertion: `shippers.stream().noneMatch(u -> u.getEmail().equals("shipper2@example.com"))`
