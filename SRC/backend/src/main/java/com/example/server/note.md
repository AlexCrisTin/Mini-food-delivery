# Backend Development Note - Mini Food Delivery

## Project Context

A Spring Boot 3.5.14 backend for a multi-actor food delivery system (Customer, Owner, Shipper, Admin).

## Backend Architecture (Detailed)

```txt
src/main/java/com/example/server/
├── ServerApplication.java              # Main entry point (loads .env variables)
├── config/                             # Application configuration
│   ├── SecurityConfig.java             # [HARDENED] Auth, RBAC, Restrictive CORS & Password Encoding
│   ├── WebSocketConfig.java            # [SECURED] STOMP with JWT Interceptor
│   ├── OpenApiConfig.java              # Swagger UI configuration
│   └── MapClientConfig.java            # RestClient configuration for External APIs
├── controller/                         # [DONE] REST Endpoints (Admin, Auth, Delivery, Menu, Order, Restaurant, User, RestaurantCategory, OwnerRequest, ShipperRequest)
├── dto/                                # [DONE] Data Transfer Objects (Request/Response)
├── entity/                             # [ENHANCED] JPA Entities (13 core entities with @Builder & @Version for concurrency)
├── enums/                              # [DONE] State Definitions (Role, OrderStatus, etc.)
├── event/                              # [DONE] Application Events (OrderReadyEvent)
├── exception/                          # [ROBUST] GlobalExceptionHandler (handles Lock Failures, Data Integrity, and API Errors)
├── listener/                           # [DONE] Event Listeners (OrderEventListener - Transactional Integrity)
├── mapper/                             # [DONE] MapStruct Object Mappers (1.6.3)
├── repository/                         # [OPTIMIZED] Data Access Layer (Spring Data JPA with @EntityGraph and @NonNull overrides)
├── security/                           # [STATELESS] JWT with Claims, CustomUserDetails & Auth Logic
└── service/                            # [DONE] Business Logic Interfaces
    └── impl/                           # [DONE] Service Implementations (Admin, Auth, Delivery, Menu, Notification, Order, Report, Restaurant, User)
```

## Current Implementation Status

- **Dependencies**: Web, Actuator, Data JPA, Security 6.4, Validation, Flyway (MySQL), Lombok, MapStruct 1.6.3, JJWT 0.13.0, Testcontainers, Dotenv-java 3.1.0, Springdoc-OpenAPI 2.8.5.
- **Security (Hardened)**:
  - **Stateless JWT**: Tokens contain `id`, `role`, and `fullName` claims. `JwtAuthFilter` reconstructs context without DB hits.
  - **IDOR Protection**: Explicit ownership validation in `OrderService`, `RestaurantService`, etc.
  - **Restrictive CORS**: Configured in `SecurityConfig` and `WebConfig`.
  - **WebSocket Security**: JWT validation enforced in STOMP `CONNECT` frame.
- **Performance (Optimized)**:
  - **N+1 Fixed**: Associations (`Order->Restaurant`, `MenuItem->Category`) eagerly fetched via `@EntityGraph`.
  - **Transactional Hygiene**: External Map API calls decoupled from DB transactions.
  - **Cache Management**: `@Modifying(clearAutomatically = true)` used for direct DML queries.
- **Reliability**:
  - **Concurrency Control**: Optimistic Locking (`@Version`) implemented for `Order` and `DeliveryAssignment`.
  - **Schema Management**: Flyway baseline (V1-V8) extended with **V9** (version columns). `repair-on-migrate: true` enabled.
  - **Error Handling**: `OrderServiceImpl` uses `parseStatus` for safe enum conversion, preventing 500 errors.
- **Environment**: Dynamic config via `.env`. Test isolation via `application-test.yaml`.
- **Testing (Modernized)**:
  - **Coverage**: 65 tests (100% pass). Layered architecture verified.
  - **Theme**: Behavioral naming convention (`should...`) matching `^[a-z][a-zA-Z0-9]*$`.
  - **Infrastructure**: Unit tests (Mockito) + Integration tests (Testcontainers/MySQL).
- **API Highlights**:
  - `MapService` (Nominatim/OSRM) integrated for dynamic fee calculation.
  - Full promotion workflows (User -> Owner/Shipper) with Admin approval.

## Backend Roadmap - Phase 2 (Updated)

1. **Promotion Workflows [DONE]**:
    - [DONE] User request to become Owner/Shipper.
    - [DONE] Admin review/approval system.
2. **Security & Performance Remediation [DONE]**:
    - [DONE] WebSocket security & location spoofing prevention.
    - [DONE] IDOR protection and N+1 query optimization.
    - [DONE] True stateless JWT (claims-based).
    - [DONE] Optimistic locking migration (V9) and implementation.
3. **Reporting & Monitoring [DONE]**:
    - [DONE] Admin stats, revenue reports, and CSV exports.
    - [DONE] JaCoCo test coverage reporting integrated.
4. **Resilience [DONE]**:
    - [DONE] Enhanced Exception Handling (Centralized Data Integrity and App Error mapping).
    - [DONE] Safe Enum parsing to prevent Internal Server Errors.
5. **Real-time Updates [DONE]**:
    - [DONE] Secured WebSocket infrastructure.
    - [DONE] Integrate SSE or WebSocket for user-facing notifications.
6. **Advanced Security & DevOps [IN PROGRESS]**:
    - [DONE] Implement Token Refresh mechanism.
    - [DONE] Account lockout logic.
    - [TODO] Multi-stage Dockerfile and CI/CD pipelines. -->ignore, not really needed for now

## Design Constraints

- **READY**: Triggers `DeliveryAssignment` creation via `TransactionalEventListener`.
- **UNASSIGNED**: Default status for assignments; concurrency-protected.
- **COD**: Only Cash on Delivery supported.
- **Terminal Status**: `DELIVERED` (Order); `COMPLETED` (Delivery).
- **Soft Delete**: `Restaurant`, `MenuCategory`, `MenuItem`.
- **Hard Delete**: `User`, `Address`.
- **Entity Creation**: Always use `@Builder` for consistency and readability.
