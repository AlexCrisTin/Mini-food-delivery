# RestaurantControllerTest Detailed Tests

## `shouldSearchRestaurantsSuccessfully`

**Scenario (Positive):** Search Restaurants Successfully

- **Expected Outcomes:**
  - Returns HTTP Status: `isOk`

## `shouldGetRestaurantDetailSuccessfully`

**Scenario (Positive):** Get Restaurant Detail Successfully

- **Expected Outcomes:**
  - Returns HTTP Status: `isOk`

## `shouldGetMyRestaurantsAsOwner`

**Scenario (Positive):** Get My Restaurants As Owner

- **Expected Outcomes:**
  - Returns HTTP Status: `isOk`

## `shouldFailToGetMyRestaurantsAsCustomer`

**Scenario (Negative):** Fail To Get My Restaurants As Customer
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - Returns HTTP Status: `isForbidden`

## `shouldCreateRestaurantAsOwner`

**Scenario (Positive):** Create Restaurant As Owner

- **Expected Outcomes:**
  - Returns HTTP Status: `isCreated`

## `shouldFailToCreateRestaurantAsCustomer`

**Scenario (Negative):** Fail To Create Restaurant As Customer
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - Returns HTTP Status: `isForbidden`

## `shouldUpdateRestaurantAsOwner`

**Scenario (Positive):** Update Restaurant As Owner

- **Expected Outcomes:**
  - Returns HTTP Status: `isOk`

## `shouldDeleteRestaurantAsOwner`

**Scenario (Positive):** Delete Restaurant As Owner

- **Expected Outcomes:**
  - Returns HTTP Status: `isNoContent`
