# AdminControllerTest Detailed Tests

## `shouldGetSystemStats`

**Scenario (Positive):** Get System Stats

- **Expected Outcomes:**
  - Returns HTTP Status: `isOk`

## `shouldGetAllUsers`

**Scenario (Positive):** Get All Users

- **Expected Outcomes:**
  - Returns HTTP Status: `isOk`

## `shouldGetPendingRestaurants`

**Scenario (Positive):** Get Pending Restaurants

- **Expected Outcomes:**
  - Returns HTTP Status: `isOk`

## `shouldApproveRestaurant`

**Scenario (Positive):** Approve Restaurant

- **Expected Outcomes:**
  - Returns HTTP Status: `isOk`
  - Verifies dependency interaction: `adminService.approveRestaurant`

## `shouldUpdateUserRoleSuccessfully`

**Scenario (Positive):** Update User Role Successfully

- **Expected Outcomes:**
  - Returns HTTP Status: `isOk`
  - Verifies dependency interaction: `adminService.updateUserRole`

## `shouldUpdateUserStatusSuccessfully`

**Scenario (Positive):** Update User Status Successfully

- **Expected Outcomes:**
  - Returns HTTP Status: `isOk`
  - Verifies dependency interaction: `adminService.updateUserStatus`

## `shouldDeleteUserSuccessfully`

**Scenario (Positive):** Delete User Successfully

- **Expected Outcomes:**
  - Returns HTTP Status: `isNoContent`
  - Verifies dependency interaction: `adminService.deleteUser`

## `shouldReturnBadRequestWhenAdminDeletesSelf`

**Scenario (Negative):** Return Bad Request When Admin Deletes Self
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - Returns HTTP Status: `isBadRequest`

## `shouldReturnBadRequestWhenAdminDemotesSelf`

**Scenario (Negative):** Return Bad Request When Admin Demotes Self
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - Returns HTTP Status: `isBadRequest`

## `shouldReturnBadRequestWhenAdminDeactivatesSelf`

**Scenario (Negative):** Return Bad Request When Admin Deactivates Self
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - Returns HTTP Status: `isBadRequest`

## `shouldGetAdminReport`

**Scenario (Positive):** Get Admin Report

- **Expected Outcomes:**
  - Returns HTTP Status: `isOk`

## `shouldGetRestaurantRevenue`

**Scenario (Positive):** Get Restaurant Revenue

- **Expected Outcomes:**
  - Returns HTTP Status: `isOk`

## `shouldExportRevenueCsv`

**Scenario (Positive):** Export Revenue Csv

- **Expected Outcomes:**
  - Returns HTTP Status: `isOk`
