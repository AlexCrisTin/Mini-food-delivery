# PHẦN 1 — TỔNG QUAN DỰ ÁN

## 1.1. Giới thiệu

**Mini Food Delivery** là một hệ thống đặt và giao thức ăn trực tuyến hiện đại, được xây dựng theo mô hình **Client-Server** với kiến trúc phân tầng (Layered Architecture). Hệ thống được tối ưu hóa cho hiệu năng cao, bảo mật chặt chẽ và khả năng mở rộng tốt, phục vụ 4 nhóm tác nhân (actor) chính với các quy trình nghiệp vụ khép kín.

## 1.2. Các tác nhân (Actors) của hệ thống

- **CUSTOMER** — Người dùng cuối: tìm kiếm nhà hàng, đặt món, quản lý địa chỉ và theo dõi đơn hàng thời gian thực.
- **OWNER** — Chủ nhà hàng: quản lý thông tin nhà hàng, thực đơn (phân tầng category/item), và điều phối trạng thái đơn hàng.
- **SHIPPER** — Người giao hàng: tiếp nhận đơn hàng, cập nhật vị trí thời gian thực qua WebSocket và xác nhận giao hàng/thu tiền COD.
- **ADMIN** — Quản trị viên: phê duyệt yêu cầu trở thành Owner/Shipper, quản lý người dùng, cấu hình hệ thống và theo dõi báo cáo doanh thu tổng hợp.

## 1.3. Công nghệ sử dụng (Tech Stack)

### Backend (Hardened & Optimized)
- **Framework:** Spring Boot 3.5.14
- **Ngôn ngữ:** Java 17
- **Bảo mật:** Spring Security 6.4 (Stateless RBAC)
- **Xác thực:** JWT (JJWT 0.13.0) với Claims-based authentication
- **Cơ sở dữ liệu:** MySQL 8.0 + Flyway Migration (V1 -> V10)
- **ORM:** Spring Data JPA (Hibernate) với Optimistic Locking (@Version)
- **Ánh xạ đối tượng:** MapStruct 1.6.3 + Lombok
- **Tài liệu API:** SpringDoc OpenAPI 2.8.5
- **Tích hợp bản đồ:** MapService (Nominatim/OSRM) cho tính phí giao hàng động
- **Real-time:** STOMP over WebSocket cho theo dõi Shipper

### Frontend (Modern Vue)
- **Framework:** Vue 3.5 + Vite 6
- **Quản lý trạng thái:** Pinia 3
- **Định tuyến:** Vue Router 5
- **Giao diện:** CSS Modules + SVG Icons (40+ assets)
- **Kiểm thử:** Vitest & Cypress

### Infrastructure & Testing
- **Kiểm thử tích hợp:** Testcontainers (MySQL 8.0)
- **Coverage:** JaCoCo 0.8.11
- **Môi trường:** Dynamic config qua Dotenv (3.1.0)

## 1.4. Cấu trúc thư mục dự án

### Backend (`SRC/backend/`)
- **`config/`** — Cấu hình Security, WebSocket, OpenAPI, MapClient.
- **`controller/`** — 13 REST Controllers + 1 WebSocket Controller.
- **`dto/`** — 40+ DTOs tổ chức theo domain (auth, order, delivery, etc.).
- **`entity/`** — 15 JPA Entities (bao gồm OwnerRequest, ShipperRequest và RefreshToken).
- **`service/`** — 12 Service Interfaces + 12 Implementations (Logic nghiệp vụ tập trung).
- **`security/`** — Logic JWT stateless, CustomUserDetails, Auth filters.

### Frontend (`SRC/frontend/`)
- **`src/composables/`** — Logic UI tái sử dụng (useAuth, useCart, useShipperTracking).
- **`src/services/`** — API Client tích hợp trực tiếp với Backend.
- **`src/stores/`** — State management cho Auth, Cart, Order và Restaurant.

## 1.5. Đặc điểm nổi bật
- **Stateless JWT**: Không lưu session tại server, payload chứa đầy đủ thông tin định danh và phân quyền.
- **Optimistic Locking**: Bảo vệ dữ liệu Order và DeliveryAssignment trước các sửa đổi đồng thời.
- **N+1 Optimization**: Sử dụng @EntityGraph để tối ưu hóa truy vấn quan hệ phức tạp.
- **Event-Driven**: Tự động hóa quy trình giao hàng thông qua OrderReadyEvent.
