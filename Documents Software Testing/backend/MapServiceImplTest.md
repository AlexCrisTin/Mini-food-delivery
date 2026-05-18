# MapServiceImplTest Detailed Tests

## `shouldSearchAddressSuccessfully`

**Scenario (Positive):** Search Address Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `result`
  - State/Persistence Assertion: `1, result.size()`
  - State/Persistence Assertion: `"Hanoi, Vietnam", result.get(0).getDisplayName()`

## `shouldReturnEmptyListWhenGeocodingFails`

**Scenario (Negative):** Return Empty List When Geocoding Fails
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `result`
  - State/Persistence Assertion: `result.isEmpty()`

## `shouldGetRouteSuccessfully`

**Scenario (Positive):** Get Route Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `result`
  - State/Persistence Assertion: `1, result.getRoutes().size()`
  - State/Persistence Assertion: `2500.0, result.getRoutes().get(0).getDistance()`

## `shouldReturnNullWhenRoutingFails`

**Scenario (Negative):** Return Null When Routing Fails
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `result`
