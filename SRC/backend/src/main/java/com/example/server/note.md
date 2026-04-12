# Self note

```txt
src/main/java/com/example/server/
├── ServerApplication.java          # File main để khởi chạy Spring Boot
├── config/                         # Các file cấu hình ứng dụng
│   ├── SecurityConfig.java         # Cấu hình phân quyền, chặn API
│   └── WebConfig.java              # Cấu hình CORS cho Frontend (Vue.js) gọi API
├── controller/                     # Các REST API Endpoints (Tiếp nhận Request)
│   ├── AuthController.java         # API Login, Register (dùng JWT)
│   ├── OrderController.java        # API đặt đồ ăn
│   ├── RestaurantController.java   # API xem danh sách nhà hàng
│   └── UserController.java         # API quản lý User/Shipper/Admin
├── dto/                            # Data Transfer Objects (Dữ liệu giao tiếp với Client)
│   ├── request/                    # Dữ liệu Client gửi lên (VD: OrderRequest, LoginRequest)
│   └── response/                   # Dữ liệu Server trả về (VD: OrderResponse, JwtResponse)
├── entity/                         # Các class map với bảng trong MySQL
│   ├── User.java                   # Đại diện chung cho Customer, Shipper, Admin
│   ├── Role.java                   # Quyền của User
│   ├── Restaurant.java
│   ├── MenuItem.java
│   └── Order.java
├── exception/                      # Quản lý lỗi chung cho toàn hệ thống
│   ├── ResourceNotFoundException.java 
│   └── GlobalExceptionHandler.java # Bắt mọi lỗi và format response lỗi chuẩn (VD: status 400, 404)
├── repository/                     # Thao tác với Database (Kế thừa JpaRepository)
│   ├── UserRepository.java
│   ├── OrderRepository.java
│   └── RestaurantRepository.java
├── security/                       # Chứa các logic liên quan đến xác thực JWT
│   ├── JwtTokenProvider.java       # Class tạo và xác thực Token
│   └── JwtAuthenticationFilter.java# Filter chặn các request để kiểm tra Token
└── service/                        # Nơi chứa Business Logic (Nghiệp vụ cốt lõi)
    ├── UserService.java            # Interface định nghĩa các hàm nghiệp vụ
    ├── OrderService.java
    └── impl/                       # Implement các hàm được định nghĩa ở interface
        ├── UserServiceImpl.java
        └── OrderServiceImpl.java
```
