# Current Test Status - Mini Food Delivery Backend

## Overview

- **Test Framework**: JUnit 5, Mockito, Spring Boot Test
- **Infrastructure**: Testcontainers (MySQL 8.0)
- **Coverage Tool**: JaCoCo
- **Total Tests**: 281
- **Pass Rate**: 100% (281/281)

## Coverage Analysis (Summary)

Based on the latest JaCoCo report (2026-05-18):

### High Coverage (> 95% Instructions)

- `Security`: `SecurityConfig`, `JwtAuthFilter`, `CustomUserDetailsService`, `JwtUtils`
- `Services`: `AuthServiceImpl` (100%), `NotificationServiceImpl` (100%), `OwnerRequestServiceImpl` (100%), `ShipperRequestServiceImpl` (100%), `RestaurantServiceImpl` (100%), `UserServiceImpl` (100%), `ReportServiceImpl` (100%), `MapServiceImpl` (100%), `OrderServiceImpl` (100%), `DeliveryServiceImpl` (100%), `AdminServiceImpl` (100%), `MenuServiceImpl` (100%)
- `Controllers`: `UserController` (100%), `RestaurantController` (100%), `ShipperRequestController` (100%), `AuthController` (100%), `DeliveryController` (100%), `MenuController` (100%), `AdminController` (100%), `OwnerRequestController` (100%), `LocationWebSocketController` (100%)
- `Entities`: All entities achieve 100% coverage via lifecycle and builder tests.
- `Listeners`: `OrderEventListener` (100%)
- `Mappers`: Trivial tests verifying MapStruct generated getters/setters have been intentionally pruned. Coverage now reflects true business logic verification.
- `Config`: `WebConfig`, `MapClientConfig`, `WebSocketConfig`, `OpenApiConfig`

## Backend Existing Test Suites

### Unit Tests (`src/test/java/com/example/server/service/impl/`)

- `AdminServiceImplTest` (UPDATED)
- `AuthServiceImplTest` (UPDATED)
- `DeliveryServiceImplTest` (UPDATED)
- `MapServiceImplTest`
- `MenuServiceImplTest` (UPDATED)
- `NotificationServiceImplTest` (UPDATED)
- `OrderServiceImplTest` (RESTRUCTURED - 100% Branch Coverage)
- `OwnerRequestServiceImplTest` (UPDATED)
- `ReportServiceImplTest`
- `RestaurantServiceImplTest` (RESTRUCTURED)
- `ShipperRequestServiceImplTest` (UPDATED)
- `UserServiceImplTest` (UPDATED)

### Entity & Lifecycle Tests (`src/test/java/com/example/server/entity/`)

- `EntityLifecycleTest` (NEW): Verifies `@PrePersist`, `@PreUpdate`, and `@Builder` defaults for all 15 entities.

### Listener Tests (`src/test/java/com/example/server/listener/`)

- `OrderEventListenerTest` (NEW): Verifies async delivery assignment creation.

### Controller Tests (`src/test/java/com/example/server/controller/`)

- `AdminControllerTest` (UPDATED)
- `AuthControllerTest`
- `DeliveryControllerTest`
- `MenuControllerTest`
- `OrderControllerTest`
- `OwnerRequestControllerTest` (NEW)
- `RestaurantControllerTest`
- `ShipperRequestControllerTest`
- `UserControllerTest`
- `LocationWebSocketControllerTest` (NEW)

### Integration Tests (`src/test/java/com/example/server/repository/`)

- `EntityMappingIntegrationTest`: Verifies JPA mappings and basic CRUD.
- `OrderRepositoryIntegrationTest`: Verifies complex queries like Haversine distance.
- `RestaurantRepositoryIntegrationTest`: Verifies search and approval queries.
- `UserRepositoryIntegrationTest`: Verifies role and email lookups.

## Frontend Existing Test Suites (`SRC/frontend/src/__tests__/`)

Detailed frontend test case parameters are fully documented under:
*   [Test_Cases_Frontend.md](Test_Cases_Frontend.md) (Master Frontend Test Cases Index)

### Unit/Component Tests (Vitest) — [Detailed Unit Specs](frontend/ViewsTest.md)
*   [AuthStoreTest.md](frontend/AuthStoreTest.md): Authentication logic and state management (`auth.spec.js`).
*   [CartStoreTest.md](frontend/CartStoreTest.md): Cart operations and calculations (`cart.spec.js`).
*   [OrderStoreTest.md](frontend/OrderStoreTest.md): Order status changes (`order.spec.js`).
*   [NotificationStoreTest.md](frontend/NotificationStoreTest.md): Global toast manager (`notification.spec.js`).
*   [ViewsTest.md](frontend/ViewsTest.md): Component rendering specs (`CartView.spec.js`, `NotFoundView.spec.js`, `OrderHistory.spec.js`).
*   [UtilsTest.md](frontend/UtilsTest.md): Comprehensive functional helpers (`validators.spec.js`, `formatters.spec.js`, `pricingUtils.spec.js`, `browseViewUtils.spec.js`, etc.).

### E2E/Integration Tests (Cypress) — [Detailed E2E Specs](frontend/E2ETest.md)
*   [E2ETest.md](frontend/E2ETest.md): Full user journeys covering signup/login (`auth.cy.js`), search & filters (`browse.cy.js`), checkout calculations (`cart.cy.js`), landing views (`home.cy.js`), 404 boundaries (`not-found.cy.js`), and order history (`orders.cy.js`).

## Gaps Identified (Resolved)

1. **Real-time Logic**: `LocationWebSocketController` is now 100% tested.
2. **Edge Case Scenarios**: All service branches (null returns, error conditions) are covered.
3. **Entity Defaults**: Lombok defaults and lifecycle hooks are verified.

## Coverage Targets (Phase 2) - ACHIEVED

- **Overall Instructions**: > 75% (Current: ~98%)
- **Core Services (Business Logic)**: > 85% (Current: 100%)
- **Controllers (API Contracts)**: > 60% (Current: 100%)
- **Security Layer**: > 90% (Current: 100%)
