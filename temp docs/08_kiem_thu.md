# PHẦN 8 — KIỂM THỬ VÀ TRIỂN KHAI

## 8.1. Chiến lược Kiểm thử Đa tầng
Dự án áp dụng mô hình kiểm thử kim tự tháp để đảm bảo độ tin cậy của mã nguồn từ cấp thấp nhất đến trải nghiệm người dùng cuối.

### 8.2. Kiểm thử Đơn vị (Unit Testing)
- **Backend**: Sử dụng JUnit 5 và Mockito. Tập trung kiểm thử logic State Machine của đơn hàng và các ràng buộc nghiệp vụ trong Service layer. 
    - *Độ phủ*: ~65 tests đạt trạng thái PASS 100%.
- **Frontend**: Sử dụng Vitest để kiểm thử các hàm tiện ích (utils) và logic trong Pinia stores.

### 8.3. Kiểm thử Tích hợp (Integration Testing)
- **Testcontainers**: Khởi động một container MySQL 8.0 thực thụ trong quá trình chạy test để kiểm thử tầng Repository và các câu lệnh Native SQL phức tạp (Haversine formula).
- **Flyway Integration**: Đảm bảo tất cả migration script (V1-V9) chạy thành công và tạo ra schema chính xác.

### 8.4. Kiểm thử Chấp nhận (E2E Testing)
- **Cypress**: Mô phỏng hành trình khách hàng từ lúc đăng nhập, chọn món cho đến khi đặt hàng thành công. Kiểm tra tính đúng đắn của giao diện trên các kích thước màn hình khác nhau.

## 8.5. Quản lý Chất lượng Mã nguồn
- **JaCoCo**: Báo cáo độ phủ mã nguồn (Code Coverage). Mục tiêu duy trì >80% logic nghiệp vụ quan trọng.
- **Logging**: Sử dụng SLF4J/Logback với các mức độ log rõ ràng (INFO cho luồng nghiệp vụ, WARN cho bảo mật, ERROR cho lỗi hệ thống).

## 8.6. Quy trình Triển khai (Deployment)

### 8.6.1. Yêu cầu Hệ thống
- **Runtime**: Java 17+, Node.js 18+.
- **Database**: MySQL 8.0+.
- **Environment**: File `.env` chứa các bí mật (Secrets) như DB password và JWT secret.

### 8.6.2. Các bước triển khai Backend
1. Build artifact: `./mvnw clean package -DskipTests`
2. Cấu hình biến môi trường (`SPRING_PROFILES_ACTIVE=prod`).
3. Chạy ứng dụng: `java -jar target/server-1.0.jar`.
4. Flyway sẽ tự động nâng cấp schema lên phiên bản mới nhất.

### 8.6.3. Các bước triển khai Frontend
1. Cài đặt: `npm install`
2. Build production: `npm run build`
3. Triển khai thư mục `dist/` lên web server (Nginx/Apache).

## 8.7. Kế hoạch Duy trì & Mở rộng
- **Monitoring**: Sử dụng Spring Boot Actuator để theo dõi sức khỏe hệ thống (Health, Metrics).
- **Scalability**: Kiến trúc Stateless cho phép dễ dàng container hóa (Docker) và triển khai trên các cụm Kubernetes.
- **Roadmap**: Tích hợp thanh toán qua cổng điện tử (VNPAY/Momo), tối ưu hóa bộ nhớ đệm (Redis) và xây dựng ứng dụng mobile native.
