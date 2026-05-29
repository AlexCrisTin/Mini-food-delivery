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

## [AuthStoreTest](frontend/AuthStoreTest.md)

- `user ban đầu là null`
- `isAuthenticated = false khi chưa có token`
- `userRole = null khi chưa đăng nhập`
- `đăng nhập thành công → lưu user và token`
- `đăng nhập thành công → isAuthenticated = true`
- `đăng nhập thành công → token lưu vào localStorage`
- `chuẩn hóa role: ROLE_CUSTOMER → CUSTOMER`
- `chuẩn hóa role: ADMIN (không có prefix) → ADMIN`
- `đăng nhập thất bại → lưu error message`
- `đăng nhập thất bại → isAuthenticated = false`
- `đăng ký thành công → lưu user và token`
- `đăng ký thất bại → error được lưu`
- `logout → user = null`
- `logout → token bị xóa khỏi localStorage`

## [CartStoreTest](frontend/CartStoreTest.md)

- `giỏ hàng ban đầu rỗng`
- `itemCount ban đầu là 0`
- `subtotal ban đầu là 0`
- `thêm 1 món vào giỏ hàng rỗng`
- `thêm cùng món → tăng số lượng thay vì tạo dòng mới`
- `thêm món cùng nhà hàng nhưng khác size → tạo dòng mới`
- `thêm món cùng nhà hàng nhưng khác ghi chú → tạo dòng mới`
- `thêm món từ nhà hàng khác → hiển thị xác nhận và xóa giỏ cũ`
- `incrementQuantity → tăng quantity của đúng item`
- `decrementQuantity → giảm quantity`
- `decrementQuantity khi quantity = 1 → không giảm nữa`
- `removeItem → xóa hẳn item ra khỏi giỏ`

## [OrderStoreTest](frontend/OrderStoreTest.md)

- `orders ban đầu là mảng rỗng`
- `currentOrder ban đầu là null`
- `tải danh sách đơn thành công`
- `thất bại → lưu error`
- `lọc đơn đang xử lý (PENDING, SHIPPING...)`
- `tạo đơn → thêm đầu danh sách và set currentOrder`
- `hủy đơn → cập nhật status trong orders và currentOrder`

## [NotificationStoreTest](frontend/NotificationStoreTest.md)

- `ban đầu không có thông báo`
- `pushNotification → thêm vào đầu danh sách`
- `markAsRead → giảm unreadCount`
- `markAllAsRead → unreadCount = 0`
- `clearNotifications → xóa toàn bộ`
- `giá trị mặc định khi payload thiếu trường`

## [ViewsTest](frontend/ViewsTest.md)

- `CartView.spec.js - render được mà không bị lỗi`
- `CartView.spec.js - hiển thị tiêu đề "Giỏ hàng"`
- `CartView.spec.js - hiển thị trạng thái empty khi giỏ hàng rỗng`
- `CartView.spec.js - click "Khám phá ngay" gọi goBrowse()`
- `CartView.spec.js - hiển thị tên món trong giỏ hàng`
- `CartView.spec.js - hiển thị số lượng = 2`
- `CartView.spec.js - click nút + gọi increment(item)`
- `CartView.spec.js - click nút xóa gọi removeItem(lineId)`
- `NotFoundView.spec.js - hiển thị mã 404 và thông báo`
- `NotFoundView.spec.js - nút "Về trang chủ" điều hướng về /`
- `OrderHistory.spec.js - hiển thị tiêu đề trang`
- `OrderHistory.spec.js - danh sách rỗng → thông báo chưa có đơn`
- `OrderHistory.spec.js - hiển thị đơn hàng từ store`

## [UtilsTest](frontend/UtilsTest.md)

- `validators.spec.js - isRequired`
- `validators.spec.js - isEmail`
- `validators.spec.js - isPhoneVN`
- `validators.spec.js - minLength`
- `formatters.spec.js - formatCurrency`
- `formatters.spec.js - formatDateTime`
- `formatters.spec.js - formatOrderStatus`
- `pricingUtils.spec.js - getDeliveryFeeBySubtotal`
- `pricingUtils.spec.js - getDiscountBySubtotal`
- `pricingUtils.spec.js - Tính tổng đơn hàng (integration)`
- `browseViewUtils.spec.js - Yêu thích (Favorites)`
- `browseViewUtils.spec.js - Category Assets`
- `browseViewUtils.spec.js - Size Variations Pricing`

## [E2ETest](frontend/E2ETest.md)

- `auth.cy.js - để trống email và mật khẩu → không gọi API`
- `auth.cy.js - nhập email sai định dạng → hiện lỗi`
- `auth.cy.js - API trả về 200 → chuyển hướng sau đăng nhập`
- `auth.cy.js - mật khẩu xác nhận không khớp → hiện lỗi`
- `auth.cy.js - đăng ký thành công → mock 201 → chuyển trang`
- `browse.cy.js - tải trang /browse thành công`
- `browse.cy.js - hiển thị món ăn sau khi API trả về`
- `browse.cy.js - tìm kiếm "Phở" → chỉ hiển thị kết quả liên quan`
- `cart.cy.js - hiển thị thông báo giỏ hàng trống`
- `cart.cy.js - hiển thị subtotal đúng: 45.000 × 2 = 90.000 ₫`
- `cart.cy.js - hiển thị tạm tính khi subtotal >= 100.000 ₫`
- `home.cy.js - hiển thị logo / thương hiệu "Giao Đồ Ăn"`
- `home.cy.js - click "Đăng nhập" → modal hiện ra`
- `not-found.cy.js - truy cập URL không tồn tại → hiển thị 404`
- `not-found.cy.js - click "Về trang chủ" → quay về /`
- `orders.cy.js - hiển thị danh sách đơn từ API`
- `orders.cy.js - hiển thị bước trạng thái "Đang giao"`
