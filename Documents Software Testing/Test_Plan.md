# Master Test Plan - Mini Food Delivery

## 1. Introduction

This document outlines the comprehensive testing strategy for the Mini Food Delivery system, covering both backend (Spring Boot) and frontend (Vue.js) components.

## 2. Testing Objectives

- Ensure all business requirements are met.
- Verify system stability and reliability under various conditions.
- Prevent regressions during future development.
- Maintain high code coverage (Target: >80% for critical business logic).
- Validate security measures (Authentication, RBAC, IDOR protection).

## 3. Testing Levels

### 3.1. Unit Testing

- **Scope**: Individual classes (Services, Utils, Mappers).
- **Tooling**: JUnit 5, Mockito.
- **Focus**: Business logic, edge cases, error handling.
- **Goal**: 90% coverage for Service implementations.

### 3.2. Integration Testing

- **Scope**: Interaction between layers (Controllers, Repositories, Services).
- **Tooling**: `@SpringBootTest`, MockMvc, Testcontainers (MySQL).
- **Focus**: Database queries, JPA mappings, REST API contracts, Security filters.

### 3.3. E2E (End-to-End) Testing

- **Scope**: Full user workflows.
- **Tooling**: Cypress (Frontend).
- **Focus**: User interface, navigation, real-time updates (WebSockets), complex flows (Order to Delivery).

## 4. Testing Strategy by Domain

### 4.1. Authentication & Security

- Validate JWT generation and parsing.
- Test RBAC (Role-Based Access Control) for all restricted endpoints.
- Verify password hashing (BCrypt).
- Test Token expiration and malformed token handling.
- Verify Token Refresh mechanism (rotation and expiration).
- Validate Account Lockout logic (lock after 5 failed attempts, auto-unlock after 15 minutes).

### 4.2. Order Management

- Test the 2-phase order creation (Distance calculation fallback).
- Validate order status transitions (State Machine).
- Verify ownership checks (Prevent users from accessing others' orders).
- Test concurrent status updates (Optimistic Locking).

### 4.3. Delivery & Tracking

- Test auto-assignment generation on `OrderReadyEvent`.
- Verify shipper location updates via WebSockets.
- Test COD collection enforcement.
- Validate distance-based fee calculation.

### 4.4. Restaurant & Menu

- Test soft-delete logic for restaurants and menu items.
- Verify search functionality with various filters.
- Test promotion workflow (User -> Owner/Shipper) including Admin approval.

## 5. Environment & Infrastructure

- **CI/CD**: JaCoCo for coverage reporting, Maven for build/test execution.
- **Database**: Clean state for each integration test using `@Transactional` or manual cleanup.
- **External APIs**: Mock `MapService` (OSRM/Nominatim) in unit tests; use real or wiremocked calls in specific integration tests.

## 6. Test Data Management

- Use `BaseIntegrationTest` for common setup.
- Utilize `@BeforeEach` to populate necessary reference data (Categories, Roles).
- Use distinct test profiles (`application-test.yaml`).

## 8. Testing Roadmap

### 8.1. Phase 2: Coverage Depth (ACHIEVED 2026-05-17)
*   **Target:** 100% coverage for Core Services and Controllers.
*   **Result:** All gaps closed, including WebSockets, Owner/Admin workflows, and Entity lifecycle hooks. Total backend tests: 279.

### 8.2. Phase 3: Production Readiness (Upcoming)
*   **Priority 1: Performance & Stress Testing**
    *   Target: `OrderRepository` (Haversine queries), `MapService` (Rate limiting fallback).
    *   Method: JMeter or Gatling to simulate high concurrent order volume.
*   **Priority 2: Security Hardening**
    *   Focus: Penetration testing for JWT hijacking, CSRF bypass in WebSockets, and advanced SQL injection (beyond JPA protection).
*   **Priority 3: Reliability Engineering**
    *   Target: Multi-service failure scenarios (Circuit Breaking simulation).
    *   Method: Chaos Engineering (e.g., stopping the Docker DB during an active transaction).

