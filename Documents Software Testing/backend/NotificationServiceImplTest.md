# NotificationServiceImplTest Detailed Tests

## `shouldReturnMappedNotificationsForUser`

**Scenario (Positive):** Return Mapped Notifications For User

- **Expected Outcomes:**
  - State/Persistence Assertion: `result`
  - State/Persistence Assertion: `1, result.size()`
  - State/Persistence Assertion: `notificationId, result.get(0).getId()`
  - Verifies dependency interaction: `notificationRepository.findByUserIdOrderByCreatedAtDesc`

## `shouldMarkNotificationAsReadSuccessfully`

**Scenario (Positive):** Mark Notification As Read Successfully

- **Expected Outcomes:**
  - State/Persistence Assertion: `notification.getIsRead()`
  - Verifies dependency interaction: `notificationRepository.save`

## `shouldThrowExceptionWhenMarkingNonExistentNotificationAsRead`

**Scenario (Negative):** Throw Exception When Marking Non Existent Notification As Read
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `ResourceNotFoundException.class, () -> notificationService.markAsRead(userId, request)`

## `shouldThrowExceptionWhenMarkingAsReadUnauthorized`

**Scenario (Security/Negative):** Throw Exception When Marking As Read Unauthorized
**Security Validation:** Verifies IDOR protection, Role-Based Access Control, or unauthorized state mutation prevention.

- **Expected Outcomes:**
  - State/Persistence Assertion: `AppException.class, () -> notificationService.markAsRead(userId, request)`
  - State/Persistence Assertion: `HttpStatus.FORBIDDEN, exception.getStatus()`
  - State/Persistence Assertion: `"UNAUTHORIZED_NOTIFICATION_ACCESS", exception.getErrorCode()`

## `shouldMarkAllNotificationsAsReadWhenNoTypeProvided`

**Scenario (Positive):** Mark All Notifications As Read When No Type Provided

- **Expected Outcomes:**
  - Verifies dependency interaction: `notificationRepository.markAllAsRead`

## `shouldMarkAllNotificationsOfTypeAsReadWhenTypeProvided`

**Scenario (Positive):** Mark All Notifications Of Type As Read When Type Provided

- **Expected Outcomes:**
  - Verifies dependency interaction: `notificationRepository.markAllByTypeAsRead`

## `shouldCreateNotificationSuccessfully`

**Scenario (Positive):** Create Notification Successfully

- **Expected Outcomes:**
  - Verifies dependency interaction: `notificationRepository.save`

## `shouldThrowExceptionWhenCreatingNotificationForNonExistentUser`

**Scenario (Negative):** Throw Exception When Creating Notification For Non Existent User
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `ResourceNotFoundException.class, () -> notificationService.createNotification(userId, "Title", "Message", "INFO")`

## `shouldHandleBroadcastFailureInCreateNotification`

**Scenario (Negative):** Handle Broadcast Failure In Create Notification
**Validation:** Ensures the system correctly identifies and rejects invalid inputs, illegal state transitions, or missing resources.

- **Expected Outcomes:**
  - State/Persistence Assertion: `() -> notificationService.createNotification(userId, "Title", "Message", "INFO")`
  - Verifies dependency interaction: `notificationRepository.save`

## `shouldMarkAllNotificationsAsReadWhenTypeIsBlank`

**Scenario (Positive):** Mark All Notifications As Read When Type Is Blank

- **Expected Outcomes:**
  - Verifies dependency interaction: `notificationRepository.markAllAsRead`
