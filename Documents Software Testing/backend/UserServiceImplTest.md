# UserServiceImplTest Detailed Tests

## `shouldGetUserProfileSuccessfully`

**Scenario (Positive):** Get User Profile Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `response`
  - Verifies dependency interaction: `userRepository.findById`

## `shouldUpdateUserProfileSuccessfully`

**Scenario (Positive):** Update User Profile Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `response`
  - State/Persistence Assertion: `"New Name", user.getFullName()`
  - State/Persistence Assertion: `"987654321", user.getPhone()`

## `shouldUpdateUserRoleSuccessfully`

**Scenario (Positive):** Update User Role Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `Role.ROLE_ADMIN, user.getRole()`
  - Verifies dependency interaction: `userRepository.save`

## `shouldUpdateUserStatusSuccessfully`

**Scenario (Positive):** Update User Status Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `user.getActive()`
  - Verifies dependency interaction: `userRepository.save`

## `shouldGetUserAddressesSuccessfully`

**Scenario (Positive):** Get User Addresses Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `1, responses.size()`

## `shouldAddAddressSuccessfully`

**Scenario (Positive):** Add Address Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `response`
  - State/Persistence Assertion: `existingDefault.getIsDefault()`
  - Verifies dependency interaction: `addressRepository.save`

## `shouldUpdateAddressSuccessfully`

**Scenario (Positive):** Update Address Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `response`
  - Verifies dependency interaction: `addressRepository.save`

## `shouldThrowExceptionWhenUpdatingOtherUserAddress`

**Scenario (Security/Negative):** Throw Exception When Updating Other User Address
**Security Validation:** Verifies IDOR protection, Role-Based Access Control, or unauthorized state mutation prevention.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> userService.updateAddress(999L, addressId, request)`
  - State/Persistence Assertion: `HttpStatus.FORBIDDEN, ex.getStatus()`

## `shouldDeleteAddressSuccessfully`

**Scenario (Positive):** Delete Address Successfully

- **Expected Outcomes:**
  - Verifies dependency interaction: `addressRepository.delete`

## `shouldSetDefaultAddressSuccessfully`

**Scenario (Positive):** Set Default Address Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `address.getIsDefault()`
  - State/Persistence Assertion: `otherDefault.getIsDefault()`
  - Verifies dependency interaction: `addressRepository.save`
  - Verifies dependency interaction: `addressRepository.save`

## `shouldDeleteUserSuccessfully`

**Scenario (Positive):** Delete User Successfully

- **Expected Outcomes:**
  - Verifies dependency interaction: `userRepository.delete`

## `shouldThrowExceptionWhenUserNotFound`

**Scenario (Negative):** Throw Exception When User Not Found
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `ResourceNotFoundException.class, () -> userService.getUserProfile(userId)`
  - State/Persistence Assertion: `ResourceNotFoundException.class, () -> userService.updateUserProfile(userId, new UserProfileUpdateRequest())`
  - State/Persistence Assertion: `ResourceNotFoundException.class, () -> userService.updateUserRole(userId, new UserRoleUpdateRequest())`
  - State/Persistence Assertion: `ResourceNotFoundException.class, () -> userService.updateUserStatus(userId, new UserStatusUpdateRequest())`
  - State/Persistence Assertion: `ResourceNotFoundException.class, () -> userService.addAddress(userId, new AddressRequest())`
  - State/Persistence Assertion: `ResourceNotFoundException.class, () -> userService.deleteUser(userId)`

## `shouldThrowExceptionWhenAddressNotFound`

**Scenario (Negative):** Throw Exception When Address Not Found
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `ResourceNotFoundException.class, () -> userService.updateAddress(userId, addressId, new AddressRequest())`
  - State/Persistence Assertion: `ResourceNotFoundException.class, () -> userService.deleteAddress(userId, addressId)`
  - State/Persistence Assertion: `ResourceNotFoundException.class, () -> userService.setDefaultAddress(userId, addressId)`

## `shouldThrowExceptionWhenDeletingOtherUserAddress`

**Scenario (Security/Negative):** Throw Exception When Deleting Other User Address
**Security Validation:** Verifies IDOR protection, Role-Based Access Control, or unauthorized state mutation prevention.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> userService.deleteAddress(999L, addressId)`

## `shouldThrowExceptionWhenSettingDefaultOtherUserAddress`

**Scenario (Security/Negative):** Throw Exception When Setting Default Other User Address
**Security Validation:** Verifies IDOR protection, Role-Based Access Control, or unauthorized state mutation prevention.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> userService.setDefaultAddress(999L, addressId)`

## `shouldAddNonDefaultAddressSuccessfully`

**Scenario (Positive):** Add Non Default Address Successfully

- **Expected Outcomes:**
  - Implicit success (Completes execution without throwing exceptions).

## `shouldUpdateNonDefaultAddressSuccessfully`

**Scenario (Positive):** Update Non Default Address Successfully

- **Expected Outcomes:**
  - Implicit success (Completes execution without throwing exceptions).
