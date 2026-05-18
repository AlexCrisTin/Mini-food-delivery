# AdminServiceImplTest Detailed Tests

## `shouldApproveRestaurantSuccessfully`

**Scenario (Positive):** Approve Restaurant Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `restaurant.getIsApproved()`
  - Verifies dependency interaction: `restaurantRepository.save`
  - Verifies dependency interaction: `notificationService.createNotification`

## `shouldUpdateUserRole`

**Scenario (Positive):** Update User Role

- **Expected Outcomes:**
  - Verifies dependency interaction: `userService.updateUserRole`

## `shouldUpdateUserStatus`

**Scenario (Positive):** Update User Status

- **Expected Outcomes:**
  - Verifies dependency interaction: `userService.updateUserStatus`

## `shouldGetAllUsers`

**Scenario (Positive):** Get All Users

- **Expected Outcomes:**
  - State/Persistence Assertion: `1, result.size()`

## `shouldGetPendingRestaurants`

**Scenario (Positive):** Get Pending Restaurants

- **Expected Outcomes:**
  - State/Persistence Assertion: `1, result.size()`

## `shouldGetSystemStats`

**Scenario (Positive):** Get System Stats

- **Expected Outcomes:**
  - State/Persistence Assertion: `100L, stats.getTotalUsers()`
  - State/Persistence Assertion: `1000.0, stats.getTotalRevenue()`

## `shouldRejectRestaurantSuccessfully`

**Scenario (Positive):** Reject Restaurant Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `restaurant.getIsApproved()`
  - Verifies dependency interaction: `restaurantRepository.save`
  - Verifies dependency interaction: `notificationService.createNotification`
  - Verifies dependency interaction: `notificationService.createNotification`

## `shouldApproveRestaurantWithoutNote`

**Scenario (Positive):** Approve Restaurant Without Note

- **Expected Outcomes:**
  - State/Persistence Assertion: `restaurant.getIsApproved()`
  - Verifies dependency interaction: `notificationService.createNotification`

## `shouldGetSystemStatsWithNullRevenue`

**Scenario (Positive):** Get System Stats With Null Revenue

- **Expected Outcomes:**
  - State/Persistence Assertion: `0.0, stats.getTotalRevenue()`

## `shouldDeleteUser`

**Scenario (Positive):** Delete User

- **Expected Outcomes:**
  - Verifies dependency interaction: `userService.deleteUser`
