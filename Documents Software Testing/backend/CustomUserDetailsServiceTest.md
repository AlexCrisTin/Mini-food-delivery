# CustomUserDetailsServiceTest Detailed Tests

## `shouldLoadUserByUsernameSuccessfully`

**Scenario (Positive):** Load User By Username Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `userDetails`
  - State/Persistence Assertion: `email, userDetails.getUsername()`
  - State/Persistence Assertion: `"ROLE_CUSTOMER", userDetails.getAuthorities().iterator().next().getAuthority()`

## `shouldThrowExceptionWhenUserNotFoundByUsername`

**Scenario (Negative):** Throw Exception When User Not Found By Username
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `UsernameNotFoundException.class, () -> { customUserDetailsService.loadUserByUsername(email`
