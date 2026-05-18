# MenuServiceImplTest Detailed Tests

## `shouldGetMenuCategoriesSuccessfully`

**Scenario (Positive):** Get Menu Categories Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `1, response.size()`

## `shouldAddMenuCategorySuccessfully`

**Scenario (Positive):** Add Menu Category Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `response`
  - Verifies dependency interaction: `menuCategoryRepository.save`

## `shouldThrowExceptionWhenAddingCategoryToOtherOwnerRestaurant`

**Scenario (Security/Negative):** Throw Exception When Adding Category To Other Owner Restaurant
**Security Validation:** Verifies IDOR protection, Role-Based Access Control, or unauthorized state mutation prevention.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> menuService.addMenuCategory(999L, restaurantId, request)`

## `shouldAddMenuItemSuccessfully`

**Scenario (Positive):** Add Menu Item Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `response`
  - Verifies dependency interaction: `menuItemRepository.save`

## `shouldDeleteMenuItemSuccessfully`

**Scenario (Positive):** Delete Menu Item Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `menuItem.getIsDeleted()`
  - Verifies dependency interaction: `menuItemRepository.save`

## `shouldGetMenuItemSuccessfully`

**Scenario (Positive):** Get Menu Item Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `response`

## `shouldThrowExceptionWhenMenuItemIsDeleted`

**Scenario (Negative):** Throw Exception When Menu Item Is Deleted
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `ResourceNotFoundException.class, () -> menuService.getMenuItem(itemId)`

## `shouldFilterDeletedCategories`

**Scenario (Positive):** Filter Deleted Categories

- **Expected Outcomes:**
  - State/Persistence Assertion: `1, response.size()`

## `shouldUpdateMenuCategorySuccessfully`

**Scenario (Positive):** Update Menu Category Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `"Updated Name", category.getName()`
  - Verifies dependency interaction: `menuCategoryRepository.save`

## `shouldDeleteMenuCategorySuccessfully`

**Scenario (Positive):** Delete Menu Category Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `category.getIsDeleted()`
  - Verifies dependency interaction: `menuCategoryRepository.save`

## `shouldThrowExceptionWhenAddMenuItemWithWrongCategory`

**Scenario (Negative):** Throw Exception When Add Menu Item With Wrong Category
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> menuService.addMenuItem(ownerId, restaurantId, categoryId, request)`
  - State/Persistence Assertion: `"INVALID_CATEGORY", ex.getErrorCode()`

## `shouldUpdateMenuItemSuccessfully`

**Scenario (Positive):** Update Menu Item Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `"Updated Item", menuItem.getName()`

## `shouldUpdateMenuItemCategorySuccessfully`

**Scenario (Positive):** Update Menu Item Category Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `category, menuItem.getCategory()`

## `shouldThrowExceptionWhenUpdateMenuItemWithWrongCategory`

**Scenario (Negative):** Throw Exception When Update Menu Item With Wrong Category
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> menuService.updateMenuItem(ownerId, itemId, request)`
  - State/Persistence Assertion: `"INVALID_CATEGORY", ex.getErrorCode()`

## `shouldReturnEmptyListWhenRestaurantNotFound`

**Scenario (Positive):** Return Empty List When Restaurant Not Found

- **Expected Outcomes:**
  - State/Persistence Assertion: `response.isEmpty()`

## `shouldThrowExceptionWhenAddingCategoryToNonExistentRestaurant`

**Scenario (Negative):** Throw Exception When Adding Category To Non Existent Restaurant
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `ResourceNotFoundException.class, () -> menuService.addMenuCategory(ownerId, restaurantId, new MenuCategoryRequest())`

## `shouldThrowExceptionWhenCategoryNotFound`

**Scenario (Negative):** Throw Exception When Category Not Found
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `ResourceNotFoundException.class, () -> menuService.updateMenuCategory(ownerId, categoryId, new MenuCategoryRequest())`

## `shouldThrowExceptionWhenMenuItemNotFound`

**Scenario (Negative):** Throw Exception When Menu Item Not Found
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `ResourceNotFoundException.class, () -> menuService.getMenuItem(itemId)`
