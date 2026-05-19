# Exhaustive Granular Test List (~281 Tests)

This index lists all test classes. Click on a test class to view the detailed input/output expectations for each test method.

## [AdminControllerTest](backend/AdminControllerTest.md)

- `shouldGetSystemStats`
- `shouldGetAllUsers`
- `shouldGetPendingRestaurants`
- `shouldApproveRestaurant`
- `shouldUpdateUserRoleSuccessfully`
- `shouldUpdateUserStatusSuccessfully`
- `shouldDeleteUserSuccessfully`
- `shouldReturnBadRequestWhenAdminDeletesSelf`
- `shouldReturnBadRequestWhenAdminDemotesSelf`
- `shouldReturnBadRequestWhenAdminDeactivatesSelf`
- `shouldGetAdminReport`
- `shouldGetRestaurantRevenue`
- `shouldExportRevenueCsv`

## [AuthControllerTest](backend/AuthControllerTest.md)

- `shouldLoginSuccessfully`
- `shouldRegisterSuccessfully`
- `shouldRefreshTokenSuccessfully`
- `shouldFailLoginWhenValidationFails`

## [DeliveryControllerTest](backend/DeliveryControllerTest.md)

- `shouldAssignShipperSuccessfully`
- `shouldMarkPickedUpSuccessfully`
- `shouldMarkDeliveredSuccessfully`
- `shouldUpdateLocationSuccessfully`
- `shouldGetShipperLocationSuccessfully`
- `shouldGetAvailableDeliveries`
- `shouldGetMyDeliveries`
- `shouldGetByOrderId`

## [LocationWebSocketControllerTest](backend/LocationWebSocketControllerTest.md)

- `shouldHandleShipperLocationSuccessfully`
- `shouldUpdateExistingShipperLocationSuccessfully`
- `shouldIgnoreWhenPrincipalIsNull`
- `shouldIgnoreWhenShipperIdMismatch`

## [MenuControllerTest](backend/MenuControllerTest.md)

- `shouldGetMenuCategoriesSuccessfully`
- `shouldAddMenuCategorySuccessfully`
- `shouldUpdateMenuCategorySuccessfully`
- `shouldDeleteMenuCategorySuccessfully`
- `shouldAddMenuItemSuccessfully`
- `shouldUpdateMenuItemSuccessfully`
- `shouldDeleteMenuItemSuccessfully`
- `shouldGetMenuItemSuccessfully`

## [OrderControllerTest](backend/OrderControllerTest.md)

- `shouldCreateOrderSuccessfully`
- `shouldGetOrderSummarySuccessfully`
- `shouldUpdateOrderStatusSuccessfully`

## [OwnerRequestControllerTest](backend/OwnerRequestControllerTest.md)

- `shouldSubmitRequestSuccessfully`
- `shouldReturnBadRequestWhenSubmittingInvalidRequest`
- `shouldGetMyRequestsSuccessfully`
- `shouldGetPendingRequestsAsAdminSuccessfully`
- `shouldReturnForbiddenWhenGettingPendingRequestsAsUser`
- `shouldProcessRequestAsAdminSuccessfully`
- `shouldReturnForbiddenWhenProcessingRequestAsUser`

## [RestaurantControllerTest](backend/RestaurantControllerTest.md)

- `shouldSearchRestaurantsSuccessfully`
- `shouldGetRestaurantDetailSuccessfully`
- `shouldGetMyRestaurantsAsOwner`
- `shouldFailToGetMyRestaurantsAsCustomer`
- `shouldCreateRestaurantAsOwner`
- `shouldFailToCreateRestaurantAsCustomer`
- `shouldUpdateRestaurantAsOwner`
- `shouldDeleteRestaurantAsOwner`

## [ShipperRequestControllerTest](backend/ShipperRequestControllerTest.md)

- `shouldSubmitRequestSuccessfully`
- `shouldGetMyRequestsSuccessfully`
- `shouldGetPendingRequestsAsAdminSuccessfully`
- `shouldReturnForbiddenWhenGettingPendingRequestsAsUser`
- `shouldProcessRequestAsAdminSuccessfully`

## [UserControllerTest](backend/UserControllerTest.md)

- `shouldGetMyProfileSuccessfully`
- `shouldUpdateMyProfileSuccessfully`
- `shouldDeleteMyProfileSuccessfully`
- `shouldGetMyAddressesSuccessfully`
- `shouldAddAddressSuccessfully`
- `shouldUpdateAddressSuccessfully`
- `shouldDeleteAddressSuccessfully`
- `shouldSetDefaultAddressSuccessfully`
- `shouldGetMyNotificationsSuccessfully`
- `shouldMarkNotificationReadSuccessfully`
- `shouldMarkAllNotificationsReadSuccessfully`

## [GlobalExceptionHandlerTest](backend/GlobalExceptionHandlerTest.md)

- `shouldHandleResourceNotFound`
- `shouldHandleAppException`
- `shouldHandleAccessDenied`
- `shouldHandleOptimisticLockingFailure`
- `shouldHandleAppExceptionWithNullStatus`
- `shouldHandleDataIntegrityViolation`
- `shouldHandleGlobalException`
- `shouldHandleValidationExceptions`

## [OrderEventListenerTest](backend/OrderEventListenerTest.md)

- `shouldCreateUnassignedAssignmentSuccessfully`
- `shouldNotifyAdminWhenAssignmentFails`
- `shouldLogWhenNotificationAlsoFails`

## [EntityMappingIntegrationTest](backend/EntityMappingIntegrationTest.md)

- `shouldCascadeSaveAddresses`
- `shouldThrowExceptionWhenDuplicateEmail`

## [OrderRepositoryIntegrationTest](backend/OrderRepositoryIntegrationTest.md)

- `shouldFindOrdersNearLocation`

## [UserRepositoryIntegrationTest](backend/UserRepositoryIntegrationTest.md)

- `shouldReturnUserWhenSearchingByEmail`
- `shouldReturnTrueWhenEmailExists`
- `shouldReturnActiveUsersWhenSearchingByRole`

## [CustomUserDetailsServiceTest](backend/CustomUserDetailsServiceTest.md)

- `shouldLoadUserByUsernameSuccessfully`
- `shouldThrowExceptionWhenUserNotFoundByUsername`

## [JwtUtilsTest](backend/JwtUtilsTest.md)

- `shouldGenerateAndValidateToken`
- `shouldFailForInvalidToken`

## [AdminServiceImplTest](backend/AdminServiceImplTest.md)

- `shouldApproveRestaurantSuccessfully`
- `shouldUpdateUserRole`
- `shouldUpdateUserStatus`
- `shouldGetAllUsers`
- `shouldGetPendingRestaurants`
- `shouldGetSystemStats`
- `shouldRejectRestaurantSuccessfully`
- `shouldApproveRestaurantWithoutNote`
- `shouldGetSystemStatsWithNullRevenue`
- `shouldDeleteUser`

## [AuthServiceImplTest](backend/AuthServiceImplTest.md)

- `shouldRegisterUserSuccessfully`
- `shouldThrowExceptionWhenRegistrationWithExistingEmail`
- `shouldLoginUserSuccessfully`
- `shouldThrowExceptionWhenLoginWithInvalidCredentials`
- `shouldLockAccountAfterMaxFailedAttempts`
- `shouldThrowExceptionWhenLoginDuringLockoutPeriod`
- `shouldLoginSuccessfullyAfterLockoutExpires`
- `shouldRefreshTokenSuccessfully`
- `shouldThrowExceptionWhenRefreshTokenExpired`
- `shouldThrowExceptionWhenLoginUserNotFound`
- `shouldThrowExceptionWhenRefreshTokenNotFound`
- `shouldThrowExceptionWhenCreateRefreshTokenUserNotFound`

## [DeliveryServiceImplTest](backend/DeliveryServiceImplTest.md)

- `shouldCreateUnassignedAssignment`
- `shouldNotCreateUnassignedAssignmentIfAlreadyExists`
- `shouldAssignShipperSuccessfully`
- `shouldThrowExceptionWhenAssigningNonShipperRole`
- `shouldMarkOrderAsPickedUpSuccessfully`
- `shouldThrowExceptionWhenMarkingPickedUpUnauthorized`
- `shouldMarkOrderAsDeliveredSuccessfully`
- `shouldThrowExceptionWhenMarkingDeliveredWithoutCod`
- `shouldCreateNewLocationWhenUpdatingLocationForFirstTime`
- `shouldUpdateExistingLocationWhenUpdatingLocation`
- `shouldGetShipperLocationSuccessfullyForCustomer`
- `shouldThrowExceptionWhenGettingShipperLocationUnauthorized`
- `shouldGetAvailableDeliveries`
- `shouldGetMyDeliveries`
- `shouldGetByOrderId`
- `shouldAssignShipperAsAdmin`
- `shouldAssignShipperSelfWhenRequesterIsShipper`
- `shouldThrowExceptionWhenMarkingPickedUpInvalidState`
- `shouldThrowExceptionWhenMarkingDeliveredInvalidState`
- `shouldAllowAdminToViewShipperLocation`
- `shouldAllowShipperToViewOwnLocation`
- `shouldAllowAdminToViewAssignment`
- `shouldAllowCustomerToViewAssignment`
- `shouldThrowExceptionWhenViewingAssignmentUnauthorized`
- `shouldThrowExceptionWhenOrderNotFoundInAssign`
- `shouldThrowExceptionWhenShipperNotFoundInUpdateLocation`

## [MapServiceImplTest](backend/MapServiceImplTest.md)

- `shouldSearchAddressSuccessfully`
- `shouldReturnEmptyListWhenGeocodingFails`
- `shouldGetRouteSuccessfully`
- `shouldReturnNullWhenRoutingFails`

## [MenuServiceImplTest](backend/MenuServiceImplTest.md)

- `shouldGetMenuCategoriesSuccessfully`
- `shouldAddMenuCategorySuccessfully`
- `shouldThrowExceptionWhenAddingCategoryToOtherOwnerRestaurant`
- `shouldAddMenuItemSuccessfully`
- `shouldDeleteMenuItemSuccessfully`
- `shouldGetMenuItemSuccessfully`
- `shouldThrowExceptionWhenMenuItemIsDeleted`
- `shouldFilterDeletedCategories`
- `shouldUpdateMenuCategorySuccessfully`
- `shouldDeleteMenuCategorySuccessfully`
- `shouldThrowExceptionWhenAddMenuItemWithWrongCategory`
- `shouldUpdateMenuItemSuccessfully`
- `shouldUpdateMenuItemCategorySuccessfully`
- `shouldThrowExceptionWhenUpdateMenuItemWithWrongCategory`
- `shouldReturnEmptyListWhenRestaurantNotFound`
- `shouldThrowExceptionWhenAddingCategoryToNonExistentRestaurant`
- `shouldThrowExceptionWhenCategoryNotFound`
- `shouldThrowExceptionWhenMenuItemNotFound`

## [NotificationServiceImplTest](backend/NotificationServiceImplTest.md)

- `shouldReturnMappedNotificationsForUser`
- `shouldMarkNotificationAsReadSuccessfully`
- `shouldThrowExceptionWhenMarkingNonExistentNotificationAsRead`
- `shouldThrowExceptionWhenMarkingAsReadUnauthorized`
- `shouldMarkAllNotificationsAsReadWhenNoTypeProvided`
- `shouldMarkAllNotificationsOfTypeAsReadWhenTypeProvided`
- `shouldCreateNotificationSuccessfully`
- `shouldThrowExceptionWhenCreatingNotificationForNonExistentUser`
- `shouldHandleBroadcastFailureInCreateNotification`
- `shouldMarkAllNotificationsAsReadWhenTypeIsBlank`

## [OrderServiceImplTest](backend/OrderServiceImplTest.md)

- `shouldCreateOrderWithDistanceBasedFee`
- `shouldFallbackToDefaultFeeWhenMapServiceFails`
- `shouldUseDefaultFeeWhenRouteIsNull`
- `shouldUseDefaultFeeWhenRoutesListIsEmpty`
- `shouldGetOrderSummaryForAdmin`
- `shouldAllowShipperToViewAssignedOrderSummary`
- `shouldThrowExceptionWhenShipperNotAssignedViewsOrder`
- `shouldThrowExceptionWhenUnassignedShipperViewsOrder`
- `shouldThrowExceptionWhenUnauthorizedUserViewsOrder`
- `shouldGetOrderHistory`
- `shouldUpdateOrderStatusSuccessfully`
- `shouldAllowAdminToUpdateStatus`
- `shouldAllowCustomerToCancelOrder`
- `shouldThrowExceptionWhenWrongUserCancelsOrder`
- `shouldAllowRestaurantOwnerToRejectOrder`
- `shouldAllowShipperToUpdateToShipping`
- `shouldAllowShipperToUpdateToDelivered`
- `shouldThrowExceptionWhenUnassignedShipperUpdatesToShipping`
- `shouldThrowExceptionWhenShipperUpdatesOtherShipperOrder`
- `shouldThrowExceptionWhenInvalidTransition`
- `shouldThrowExceptionWhenCancellingShippedOrder`
- `shouldThrowExceptionWhenRejectingShippedOrder`
- `shouldThrowExceptionWhenCancellingDeliveredOrder`
- `shouldThrowExceptionWhenTransitioningFromTerminalState`
- `shouldThrowExceptionWhenStatusIsNull`
- `shouldThrowExceptionWhenStatusIsInvalid`
- `shouldPublishEventWhenOrderIsReady`
- `shouldGetOrderTracking`
- `shouldGetRestaurantOrders`
- `shouldAllowAdminToGetRestaurantOrdersWithoutOwnershipCheck`
- `shouldThrowExceptionWhenNonOwnerGetsRestaurantOrders`

## [OwnerRequestServiceImplTest](backend/OwnerRequestServiceImplTest.md)

- `shouldSubmitRequestSuccessfully`
- `shouldThrowExceptionWhenPendingRequestExists`
- `shouldProcessApprovalSuccessfully`
- `shouldProcessRejectionSuccessfully`
- `shouldThrowExceptionWhenProcessingAlreadyProcessedRequest`
- `shouldThrowExceptionWhenUserNotFound`
- `shouldThrowExceptionWhenRequestNotFound`
- `shouldGetAllPendingRequests`
- `shouldGetUserRequests`

## [ReportServiceImplTest](backend/ReportServiceImplTest.md)

- `shouldAggregateAdminReportDataCorrectly`
- `shouldReturnZeroRevenueWhenNoOrdersFound`
- `shouldGetRestaurantRevenueSuccessfully`
- `shouldGenerateRevenueCsvSuccessfully`

## [RestaurantServiceImplTest](backend/RestaurantServiceImplTest.md)

- `shouldSearchRestaurantsSuccessfully`
- `shouldGetRestaurantDetailSuccessfully`
- `shouldThrowExceptionWhenRestaurantNotFound`
- `shouldThrowExceptionWhenRestaurantIsDeleted`
- `shouldCreateRestaurantSuccessfully`
- `shouldUpdateRestaurantSuccessfully`
- `shouldThrowExceptionWhenUpdatingOtherOwnerRestaurant`
- `shouldDeleteRestaurantSuccessfully`
- `shouldThrowExceptionWhenDeletingOtherOwnerRestaurant`
- `shouldGetAllCategories`
- `shouldApproveRestaurantSuccessfully`
- `shouldGetMyRestaurantsFilteringDeleted`
- `shouldCreateRestaurantWithoutCategory`
- `shouldUpdateRestaurantWithNewCategory`
- `shouldThrowExceptionWhenCreateRestaurantUserNotFound`
- `shouldThrowExceptionWhenCreateRestaurantCategoryNotFound`
- `shouldThrowExceptionWhenUpdateRestaurantNotFound`
- `shouldSearchRestaurantsWithDefaultSorting`

## [ShipperRequestServiceImplTest](backend/ShipperRequestServiceImplTest.md)

- `shouldSubmitRequestSuccessfully`
- `shouldThrowExceptionWhenPendingRequestExists`
- `shouldProcessApprovalSuccessfully`
- `shouldProcessRejectionSuccessfully`
- `shouldThrowExceptionWhenProcessingAlreadyProcessedRequest`
- `shouldThrowExceptionWhenUserNotFound`
- `shouldThrowExceptionWhenRequestNotFound`
- `shouldNotCreateLocationIfAlreadyExistsOnApproval`
- `shouldGetAllPendingRequests`
- `shouldGetUserRequests`

## [UserServiceImplTest](backend/UserServiceImplTest.md)

- `shouldGetUserProfileSuccessfully`
- `shouldUpdateUserProfileSuccessfully`
- `shouldUpdateUserRoleSuccessfully`
- `shouldUpdateUserStatusSuccessfully`
- `shouldGetUserAddressesSuccessfully`
- `shouldAddAddressSuccessfully`
- `shouldUpdateAddressSuccessfully`
- `shouldThrowExceptionWhenUpdatingOtherUserAddress`
- `shouldDeleteAddressSuccessfully`
- `shouldSetDefaultAddressSuccessfully`
- `shouldDeleteUserSuccessfully`
- `shouldThrowExceptionWhenUserNotFound`
- `shouldThrowExceptionWhenAddressNotFound`
- `shouldThrowExceptionWhenDeletingOtherUserAddress`
- `shouldThrowExceptionWhenSettingDefaultOtherUserAddress`
- `shouldAddNonDefaultAddressSuccessfully`
- `shouldUpdateNonDefaultAddressSuccessfully`
