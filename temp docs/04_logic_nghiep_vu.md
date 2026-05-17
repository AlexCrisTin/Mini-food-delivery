# PHẦN 4 — TẦNG LOGIC NGHIỆP VỤ (SERVICE LAYER)

## 4.1. Kiến trúc Tầng Dịch vụ
Hệ thống sử dụng mô hình **Interface + Implementation** cho tất cả 12 dịch vụ lõi, đảm bảo tính đóng gói và dễ dàng kiểm thử (Unit Testing với Mockito).

## 4.2. Chi tiết các dịch vụ chính

### 4.2.1. Quản lý Giao dịch & Đơn hàng
- **OrderService**: Quản lý vòng đời đơn hàng qua máy trạng thái (State Machine). 
    - *Tính toán phí động*: Tích hợp `MapService` để tính phí dựa trên khoảng cách thực tế (Phase 1 - Ngoài transaction) trước khi lưu đơn hàng (Phase 2 - Trong transaction).
    - *Xác thực quyền*: Kiểm tra quyền sở hữu (Ownership) trước khi cho phép xem hoặc cập nhật.
- **DeliveryService**: Điều phối shipper và theo dõi vị trí.
    - *Cơ chế gán đơn*: Hỗ trợ gán tự động (qua Admin) hoặc Shipper tự nhận đơn khả dụng.
    - *Bảo mật vị trí*: Chỉ Admin và Customer có đơn hàng đang giao mới có quyền truy cập vị trí thời gian thực của Shipper.

### 4.2.2. Quản lý Nhà hàng & Thực đơn
- **RestaurantService**: Xử lý tìm kiếm nâng cao với phân trang và sắp xếp động. Tích hợp quy trình phê duyệt nhà hàng mới.
- **MenuService**: Quản lý cấu trúc thực đơn phức tạp. Đảm bảo tính toàn vẹn dữ liệu khi di chuyển MenuItem giữa các Category.

### 4.2.3. Hạ tầng & Tiện ích
- **AuthService**: Xử lý đăng ký/đăng nhập và cấp phát JWT. Toàn bộ thông tin định danh được đóng gói vào Claims để phục vụ kiến trúc stateless.
- **MapService**: Cổng giao tiếp với external APIs (Nominatim, OSRM) để geocoding và tính toán đường đi.
- **NotificationService**: Hệ thống thông báo nội bộ hỗ trợ WebSockets (SSE/STOMP) để cập nhật tức thời cho người dùng.

### 4.2.4. Quản trị & Báo cáo
- **AdminService**: Quản lý toàn bộ actor, phê duyệt các yêu cầu nâng cấp (Owner/Shipper) và quản lý cấu hình hệ thống.
- **ReportService**: Tổng hợp dữ liệu tài chính, doanh thu theo nhà hàng và xuất báo cáo CSV phục vụ kế toán.

## 4.3. Quy trình Nghiệp vụ Đặc trưng

### 4.3.1. Luồng Đặt hàng & Giao hàng tự động
1. `OrderService` lưu đơn hàng ở trạng thái `READY`.
2. Hệ thống phát đi `OrderReadyEvent`.
3. `OrderEventListener` (chờ sau khi Transaction commit) gọi `DeliveryService` để tạo `DeliveryAssignment` ở trạng thái `UNASSIGNED`.
4. Shipper nhận đơn -> Trạng thái chuyển thành `SHIPPING`.

### 4.3.2. Luồng Nâng cấp Vai trò (Promotion)
- User gửi `OwnerRequest` hoặc `ShipperRequest`.
- Admin xem xét thông tin và phê duyệt.
- Hệ thống tự động:
    - Cập nhật Role người dùng.
    - Tạo thực thể tương ứng (Restaurant cho Owner, ShipperLocation cho Shipper).
    - Gửi thông báo SYSTEM cho người dùng.

## 4.4. Đảm bảo Tính nhất quán
- **Transactional Integrity**: Sử dụng `@Transactional` trên tất cả phương thức thay đổi dữ liệu.
- **Optimistic Locking Handling**: `GlobalExceptionHandler` bắt `ObjectOptimisticLockingFailureException` và trả về mã lỗi `CONCURRENCY_FAILURE` (409) để Frontend yêu cầu người dùng refresh dữ liệu.
