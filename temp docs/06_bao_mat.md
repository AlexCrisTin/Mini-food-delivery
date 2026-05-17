# PHẦN 6 — BẢO MẬT VÀ XÁC THỰC

## 6.1. Kiến trúc Bảo mật Stateless
Hệ thống loại bỏ hoàn toàn HTTP Session truyền thống, thay vào đó sử dụng **Stateless JWT** (JSON Web Token). Điều này cho phép Backend mở rộng ngang (horizontal scaling) dễ dàng và giảm tải cho bộ nhớ server.

## 6.2. Quy trình Xác thực & Ủy quyền (JWT Flow)

1. **Đăng nhập**: `AuthService` xác thực thông tin và sinh JWT. 
   - *Claims*: JWT chứa `id`, `role`, `fullName` và `sub` (email).
2. **Filter Interception**: `JwtAuthFilter` chặn mọi request (trừ public endpoints).
   - *No-DB Validation*: Filter trích xuất thông tin user trực tiếp từ Claims của token. 
   - *Context Mapping*: Chuyển đổi Claims thành `CustomUserDetails` và nạp vào `SecurityContext`.
3. **RBAC Enforcement**: `SecurityConfig` và `@PreAuthorize` kiểm tra quyền truy cập dựa trên Role trong Context.

## 6.3. Bảo mật Dữ liệu & IDOR Protection
Hệ thống triển khai các biện pháp chống lại các lỗ hổng phổ biến:
- **IDOR (Insecure Direct Object Reference)**: Tại tầng Service, mọi thao tác truy cập thực thể (Order, Restaurant, Address) đều được kiểm tra quyền sở hữu (Ownership) dựa trên ID người dùng đang đăng nhập.
- **Password Hashing**: Sử dụng `BCryptPasswordEncoder` với salt tự động.
- **Restrictive CORS**: Chỉ cho phép các origin chính thức của Frontend (mặc định localhost:5173).
- **SQL Injection**: Ngăn chặn tuyệt đối thông qua việc sử dụng Spring Data JPA Repositories và Prepared Statements.

## 6.4. Xử lý Lỗi Bảo mật Tập trung
Mọi lỗi liên quan đến bảo mật được ánh xạ về chuẩn JSON:
- **401 Unauthorized**: Token thiếu, hết hạn hoặc không hợp lệ.
- **403 Forbidden**: Người dùng không có Role phù hợp hoặc vi phạm Ownership validation.
- **409 Conflict**: Vi phạm Optimistic Locking (dữ liệu đã bị thay đổi bởi người khác).

## 6.5. Bảo mật WebSocket
- **STOMP Interceptor**: JWT được kiểm tra trong frame CONNECT. Nếu không hợp lệ, kết nối WebSocket bị từ chối ngay lập tức.
- **Spoofing Prevention**: Khi Shipper gửi vị trí, hệ thống đối chiếu `shipperId` trong tin nhắn với `id` của người dùng đã xác thực trong session WebSocket.

## 6.6. Error Codes nghiệp vụ (Trích dẫn)
- `AUTH_FAILED`: Sai email/mật khẩu.
- `FORBIDDEN`: Không có quyền truy cập tài nguyên.
- `CONCURRENCY_FAILURE`: Xung đột phiên bản dữ liệu (@Version).
- `UNAUTHORIZED_ACCESS`: Vi phạm Ownership (IDOR).
