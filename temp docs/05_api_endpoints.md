# PHẦN 5 — RESTFUL API & WEBSOCKET

## 5.1. Quy ước API
- **Endpoint Root**: `/api`
- **Xác thực**: Header `Authorization: Bearer <JWT>`
- **Định dạng phản hồi**: 
  ```json
  {
    "success": boolean,
    "message": string,
    "data": object|array,
    "errorCode": string|null,
    "timestamp": string
  }
  ```

## 5.2. Danh mục Endpoints chính

### 5.2.1. Authentication (`/api/auth`)
- `POST /login`: Đăng nhập và nhận JWT + Refresh Token (Claims: id, role, fullName).
- `POST /register`: Đăng ký tài khoản CUSTOMER mới.
- `POST /refresh`: Làm mới Access Token bằng Refresh Token.

### 5.2.2. Nhà hàng & Menu (`/api/restaurants`)
- `POST /search`: Tìm kiếm nhà hàng với filter category và từ khóa (Phân trang).
- `GET /{id}`: Chi tiết nhà hàng kèm thực đơn đầy đủ.
- `POST /`: (OWNER) Tạo nhà hàng mới.
- `GET /my-restaurants`: (OWNER) Danh sách nhà hàng sở hữu.

### 5.2.3. Đơn hàng (`/api/orders`)
- `POST /`: (CUSTOMER) Đặt hàng mới.
- `GET /history`: (Authenticated) Lịch sử đơn hàng của cá nhân.
- `PATCH /{id}/status`: (Owner/Shipper/Admin) Cập nhật trạng thái (State machine validation).
- `GET /{id}/tracking`: (Authenticated) Theo dõi tiến độ đơn hàng và timeline.

### 5.2.4. Giao hàng (`/api/deliveries`)
- `POST /assign`: (Admin/Shipper) Nhận hoặc gán đơn cho Shipper.
- `PATCH /{orderId}/pickup`: (Shipper) Xác nhận đã lấy hàng từ nhà hàng.
- `PATCH /{orderId}/deliver`: (Shipper) Xác nhận giao thành công và thu tiền COD.
- `PUT /location`: (Shipper) Cập nhật tọa độ thời gian thực.
- `GET /{shipperId}/location`: (Authenticated) Lấy tọa độ Shipper (Bảo mật ownership).

### 5.2.5. Quản trị (`/api/admin`)
- `GET /stats`: Thống kê tổng quan hệ thống.
- `GET /users`: Quản lý danh sách người dùng.
- `GET /reports/summary`: Báo cáo doanh thu và đơn hàng theo thời gian.
- `GET /reports/export/csv`: Xuất dữ liệu báo cáo ra file CSV.

## 5.3. WebSocket (Real-time Tracking)
Hệ thống sử dụng **STOMP over WebSocket** để truyền tải vị trí Shipper.

- **Endpoint**: `/ws` (Hỗ trợ SockJS fallback).
- **Inbound Channel Interceptor**: Xác thực JWT token ngay tại frame CONNECT.
- **Luồng dữ liệu**:
    - Shipper gửi tới: `/app/shipper/location`
    - Customer subscribe: `/topic/order/{orderId}`
- **Security**: Server kiểm tra tính hợp lệ của `shipperId` trong payload so với `Principal` của session để ngăn chặn giả mạo vị trí.

## 5.4. API Documentation
Tài liệu tương tác đầy đủ được cung cấp qua Swagger UI tại:
`http://localhost:8080/swagger-ui.html`
- Bao gồm định nghĩa DTO chi tiết.
- Hỗ trợ thử nghiệm trực tiếp với Authorization Header.
