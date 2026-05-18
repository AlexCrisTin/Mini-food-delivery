# RestaurantServiceImplTest Detailed Tests

## `shouldSearchRestaurantsSuccessfully`

**Scenario (Positive):** Search Restaurants Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `response`
  - State/Persistence Assertion: `1, response.getItems().size()`

## `shouldGetRestaurantDetailSuccessfully`

**Scenario (Positive):** Get Restaurant Detail Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `response`

## `shouldThrowExceptionWhenRestaurantNotFound`

**Scenario (Negative):** Throw Exception When Restaurant Not Found
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `ResourceNotFoundException.class, () -> restaurantService.getRestaurantDetail(restaurantId)`

## `shouldThrowExceptionWhenRestaurantIsDeleted`

**Scenario (Negative):** Throw Exception When Restaurant Is Deleted
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `ResourceNotFoundException.class, () -> restaurantService.getRestaurantDetail(restaurantId)`

## `shouldCreateRestaurantSuccessfully`

**Scenario (Positive):** Create Restaurant Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `response`

## `shouldUpdateRestaurantSuccessfully`

**Scenario (Positive):** Update Restaurant Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `response`

## `shouldThrowExceptionWhenUpdatingOtherOwnerRestaurant`

**Scenario (Security/Negative):** Throw Exception When Updating Other Owner Restaurant
**Security Validation:** Verifies IDOR protection, Role-Based Access Control, or unauthorized state mutation prevention.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> restaurantService.updateRestaurant(999L, restaurantId, request)`

## `shouldDeleteRestaurantSuccessfully`

**Scenario (Positive):** Delete Restaurant Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `restaurant.getIsDeleted()`

## `shouldThrowExceptionWhenDeletingOtherOwnerRestaurant`

**Scenario (Security/Negative):** Throw Exception When Deleting Other Owner Restaurant
**Security Validation:** Verifies IDOR protection, Role-Based Access Control, or unauthorized state mutation prevention.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> restaurantService.deleteRestaurant(999L, restaurantId)`

## `shouldGetAllCategories`

**Scenario (Positive):** Get All Categories

- **Expected Outcomes:**
  - State/Persistence Assertion: `1, response.size()`

## `shouldApproveRestaurantSuccessfully`

**Scenario (Positive):** Approve Restaurant Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `restaurant.getIsApproved()`

## `shouldGetMyRestaurantsFilteringDeleted`

**Scenario (Positive):** Get My Restaurants Filtering Deleted

- **Expected Outcomes:**
  - State/Persistence Assertion: `1, response.size()`

## `shouldCreateRestaurantWithoutCategory`

**Scenario (Positive):** Create Restaurant Without Category

- **Expected Outcomes:**
  - State/Persistence Assertion: `restaurantService.createRestaurant(ownerId, request)`

## `shouldUpdateRestaurantWithNewCategory`

**Scenario (Positive):** Update Restaurant With New Category

- **Expected Outcomes:**
  - State/Persistence Assertion: `newCat, restaurant.getCategory()`

## `shouldThrowExceptionWhenCreateRestaurantUserNotFound`

**Scenario (Negative):** Throw Exception When Create Restaurant User Not Found
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `ResourceNotFoundException.class, () -> restaurantService.createRestaurant(ownerId, new RestaurantRequest())`

## `shouldThrowExceptionWhenCreateRestaurantCategoryNotFound`

**Scenario (Negative):** Throw Exception When Create Restaurant Category Not Found
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `ResourceNotFoundException.class, () -> restaurantService.createRestaurant(ownerId, request)`

## `shouldThrowExceptionWhenUpdateRestaurantNotFound`

**Scenario (Negative):** Throw Exception When Update Restaurant Not Found
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `ResourceNotFoundException.class, () -> restaurantService.updateRestaurant(ownerId, restaurantId, new RestaurantReques...

## `shouldSearchRestaurantsWithDefaultSorting`

**Scenario (Positive):** Search Restaurants With Default Sorting

- **Expected Outcomes:**
  - Verifies dependency interaction: `restaurantRepository.searchRestaurants`
