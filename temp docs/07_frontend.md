# PHẦN 7 — GIAO DIỆN NGƯỜI DÙNG (FRONTEND)

## 7.1. Kiến trúc Frontend
Frontend được xây dựng trên nền tảng **Vue 3 (Composition API)** kết hợp với **Vite** để đạt tốc độ phản hồi và phát triển tối ưu.

## 7.2. Quản lý Trạng thái & Dữ liệu
- **Pinia Stores**: Chia nhỏ state theo domain:
    - `auth.js`: Quản lý token, thông tin người dùng và trạng thái đăng nhập.
    - `cart.js`: Quản lý giỏ hàng cục bộ, tính toán tổng tiền tạm tính.
    - `order.js`: Theo dõi trạng thái các đơn hàng đang diễn ra.
    - `restaurant.js`: Cache thông tin nhà hàng và thực đơn.
- **Composables (ViewModels)**: Tách biệt logic UI khỏi template, giúp mã nguồn dễ bảo trì và kiểm thử.
    - Ví dụ: `useShipperTracking.js` quản lý kết nối WebSocket và cập nhật vị trí shipper trên bản đồ.

## 7.3. Các phân hệ giao diện chính

### 7.3.1. Phân hệ Khách hàng (Customer)
- **Browse View**: Giao diện tìm kiếm và lọc nhà hàng theo danh mục. 
- **Restaurant Detail**: Hiển thị thực đơn sinh động, hỗ trợ thêm món nhanh vào giỏ hàng.
- **Checkout & Tracking**: Quy trình đặt hàng COD và bản đồ theo dõi Shipper thời gian thực qua WebSocket.

### 7.3.2. Phân hệ Chủ nhà hàng (Owner)
- **Restaurant Dashboard**: Quản lý đơn hàng đang chờ, đơn đang chuẩn bị.
- **Menu Manager**: Giao diện kéo thả hoặc cập nhật danh mục, món ăn, giá cả và hình ảnh.

### 7.3.3. Phân hệ Shipper
- **Delivery Dashboard**: Danh sách đơn hàng khả dụng gần vị trí hiện tại.
- **Delivery Detail**: Thông tin đường đi, số điện thoại khách hàng và nút xác nhận thu tiền COD.

### 7.3.4. Phân hệ Quản trị (Admin)
- **System Overview**: Biểu đồ thống kê doanh thu và tăng trưởng người dùng.
- **Approval Center**: Duyệt các yêu cầu trở thành Owner/Shipper hoặc mở nhà hàng mới.

## 7.4. UI/UX & Styling
- **Design System**: Sử dụng bảng màu hiện đại (Primary: #f8143f), hệ thống Grid linh hoạt và bộ 40+ SVG icons tùy chỉnh.
- **Responsive Design**: Tối ưu hóa cho cả Desktop và Mobile Web.
- **Interactive Elements**: Hiệu ứng transition mượt mà (0.2s), feedback tức thì qua hệ thống Notification (Toast).

## 7.5. Tích hợp API
- **Axios Interceptors**: 
    - *Request*: Tự động đính kèm JWT vào header Authorization.
    - *Response*: Xử lý tập trung các lỗi 401 (chuyển hướng đăng nhập) và 409 (thông báo xung đột dữ liệu).
- **WebSocket (SockJS/STOMP)**: Kết nối ổn định để nhận tọa độ Shipper mà không cần tải lại trang.
