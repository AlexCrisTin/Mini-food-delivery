# Current Test Status - Mini Food Delivery Backend

## Overview

- **Test Framework**: JUnit 5, Mockito, Spring Boot Test
- **Infrastructure**: Testcontainers (MySQL 8.0)
- **Coverage Tool**: JaCoCo
- **Total Tests**: 168
- **Pass Rate**: 100% (168/168)

## Coverage Analysis (Summary)

Based on the JaCoCo report (2026-05-14):

### High Coverage (> 70% Instructions)

- `Security`: `SecurityConfig`, `JwtAuthFilter`, `CustomUserDetailsService`
- `Services`: `AuthServiceImpl` (94%), `NotificationServiceImpl` (95%), `OwnerRequestServiceImpl` (84%), `ShipperRequestServiceImpl` (82%), `RestaurantServiceImpl` (71%), `UserServiceImpl` (81%), `ReportServiceImpl` (100%), `MapServiceImpl` (100%)
- `Controllers`: `UserController` (100%), `RestaurantController` (100%), `ShipperRequestController` (100%), `AuthController` (100%), `DeliveryController` (100%), `MenuController` (100%)
- `Config`: `WebConfig`, `MapClientConfig`, `WebSocketConfig`, `OpenApiConfig`
- `Mappers`: MapStruct generated implementations are now directly tested within the service layer tests, achieving 73% coverage.

### Medium Coverage (40% - 70% Instructions)

- `Services`: `OrderServiceImpl` (60%), `DeliveryServiceImpl` (50%), `MenuServiceImpl` (51%), `AdminServiceImpl` (40%)
- `Controllers`: `OrderController` (53%)
- `Security`: `JwtUtils`

### Low/Zero Coverage (< 40% Instructions)

- `Controllers`: `AdminController` (34%), `LocationWebSocketController` (3%), `OwnerRequestController` (0%)

## Backend Existing Test Suites

### Unit Tests (`src/test/java/com/example/server/service/impl/`)

- `AdminServiceImplTest`
- `AuthServiceImplTest`
- `DeliveryServiceImplTest`
- `NotificationServiceImplTest`
- `OrderServiceImplTest`
- `ReportServiceImplTest`
- `UserServiceImplTest`
- `RestaurantServiceImplTest` (NEW)
- `MenuServiceImplTest` (NEW)
- `OwnerRequestServiceImplTest` (NEW)
- `ShipperRequestServiceImplTest` (NEW)

### Integration Tests (`src/test/java/com/example/server/repository/`)

- `EntityMappingIntegrationTest`: Verifies JPA mappings and basic CRUD.
- `OrderRepositoryIntegrationTest`: Verifies complex queries like Haversine distance.
- `RestaurantRepositoryIntegrationTest`: Verifies search and approval queries.
- `UserRepositoryIntegrationTest`: Verifies role and email lookups.

### Security Tests (`src/test/java/com/example/server/security/`)

- `JwtUtilsTest`
- `JwtAuthFilterTest`
- `CustomUserDetailsServiceTest`

### Controller Tests (`src/test/java/com/example/server/controller/`)

- `AdminControllerTest`
- `OrderControllerTest`
- `DeliveryControllerTest`
- `ShipperRequestControllerTest`

## Frontend Existing Test Suites (`SRC/frontend/src/__tests__/`)

### Unit/Component Tests (Vitest)

- `auth.spec.js`: Authentication logic and state management.
- `cart.spec.js`: Cart operations and calculations.
- `CartView.spec.js`: Component rendering for the cart.
- `formatters.spec.js`: Date, currency, and address formatting.
- `pricingUtils.spec.js`: Delivery fee and total calculation logic.
- `validators.spec.js`: Form validation rules.

### E2E/Integration Tests (Cypress)

- `auth.cy.js`: Login/Registration flows.
- `browse.cy.js`: Restaurant browsing and filtering.
- `cart.cy.js`: Adding items and cart persistence.
- `home.cy.js`: Landing page interactions.

## Gaps Identified (High Priority)

1.  **Real-time Logic**: `LocationWebSocketController` and STOMP message handling.
2.  **Edge Case Scenarios**: Additional negative testing across all layers for invalid edge cases.

## Coverage Targets (Phase 2)

*   **Overall Instructions**: > 75%
*   **Core Services (Business Logic)**: > 85%
*   **Controllers (API Contracts)**: > 60%
*   **Security Layer**: > 90%
