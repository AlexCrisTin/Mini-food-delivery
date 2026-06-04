# 🏛️ PHẦN 1 — KIẾN TRÚC & CẤU HÌNH HỆ THỐNG

---

## 1.1. Kiến trúc tổng quan — Layered Architecture

Hệ thống backend tuân theo mô hình **Phân tầng (Layered Architecture)** với 4 tầng chính, mỗi tầng có trách nhiệm rõ ràng và chỉ giao tiếp với tầng liền kề.

```mermaid
graph TB
    CLIENT["🖥️ Vue.js Client<br/>(HTTP/REST + WebSocket)"]

    subgraph BACKEND["Spring Boot 3.5.13 — com.example.server"]
        direction TB
        
        subgraph PRES["🔵 Presentation Layer"]
            CTRL["13 Controllers<br/>(REST + WebSocket)"]
            GEH["GlobalExceptionHandler<br/>(@ControllerAdvice)"]
            DTOS["DTOs<br/>(Request / Response)"]
        end

        subgraph BIZ["🟢 Business Logic Layer"]
            SVC["12 Service Interfaces<br/>+ 12 Implementations"]
            EVT["Event System<br/>(OrderReadyEvent → Listener)"]
            MAP["7 MapStruct Mappers<br/>(Entity ↔ DTO)"]
        end

        subgraph SEC_LAYER["🟡 Security Layer"]
            FILT["JwtAuthFilter<br/>(OncePerRequestFilter)"]
            UTILS["JwtUtils<br/>(HMAC-SHA / JJWT 0.13)"]
            UDS["CustomUserDetailsService"]
            SCFG["SecurityConfig<br/>(RBAC + CORS + Stateless)"]
        end

        subgraph DAL["🔴 Data Access Layer"]
            REPO["14 JPA Repositories"]
            ENT["14 JPA Entities"]
            FLY["Flyway Migrations<br/>(V1 → V8)"]
        end
    end

    DB[("🗄️ MySQL 8.0")]

    CLIENT -->|"REST API (JSON)"| PRES
    CLIENT -.->|"STOMP/SockJS"| CTRL
    PRES --> BIZ
    BIZ --> DAL
    SEC_LAYER -.->|"intercepts every request"| PRES
    DAL --> DB

    style BACKEND fill:#1a1a2e,stroke:#16213e,color:#eee
    style PRES fill:#0f3460,stroke:#533483,color:#fff
    style BIZ fill:#1a5c2b,stroke:#2d8a4e,color:#fff
    style SEC_LAYER fill:#6b5b00,stroke:#a89100,color:#fff
    style DAL fill:#8b1a1a,stroke:#c62828,color:#fff
```

> [!NOTE]
> Security Layer không phải một tầng tuần tự — nó hoạt động như một **cross-cutting concern** xuyên suốt filter chain, chặn mọi request trước khi đến Controller.

---

## 1.2. Entry Point — `ServerApplication.java`

[ServerApplication.java](file:///c:/Users/bachp/Downloads/Mini-Food-Delivery/SRC/backend/src/main/java/com/example/server/ServerApplication.java)

### 1.2.1. Dotenv Integration

Ứng dụng sử dụng thư viện `cdimascio.dotenv-java` để tải biến môi trường từ file `.env` trước khi Spring Boot khởi tạo context:

```java
// Thử tải từ working directory trước
Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

// Nếu không tìm thấy, thử SRC/backend/
if (dotenv.entries().isEmpty()) {
    dotenv = Dotenv.configure().directory("SRC/backend").ignoreIfMissing().load();
}

// Chỉ set system property nếu chưa tồn tại (ưu tiên biến hệ thống)
dotenv.entries().forEach(entry -> {
    if (System.getProperty(entry.getKey()) == null) {
        System.setProperty(entry.getKey(), entry.getValue());
    }
});
```

> [!TIP]
> Cơ chế fallback 2 cấp cho phép chạy ứng dụng từ cả thư mục gốc dự án lẫn thư mục `SRC/backend/`. System properties có độ ưu tiên cao hơn `.env`.

### 1.2.2. Smoke Mode

Chế độ đặc biệt cho phép khởi động nhanh **không cần MySQL**:

```java
private static boolean isSmokeModeEnabled() {
    String env = System.getenv("APP_SMOKE_MODE");       // Biến môi trường
    String prop = System.getProperty("app.smokeMode");   // System property
    return "true".equalsIgnoreCase(env) || "true".equalsIgnoreCase(prop);
}
```

Khi bật, tự động exclude 3 auto-configurations:
- `DataSourceAutoConfiguration`
- `HibernateJpaAutoConfiguration`  
- `JpaRepositoriesAutoConfiguration`

### 1.2.3. Health Check (CommandLineRunner)

Sau khi context khởi tạo xong, ứng dụng tự động:

| Bước | Hành động | Chi tiết |
|:----:|:----------|:---------|
| 1 | Xác định port | Ưu tiên `local.server.port` → fallback `server.port` → `8080` |
| 2 | In API Base URL | `http://localhost:{port}/api` |
| 3 | In Swagger URL | `http://localhost:{port}/swagger-ui.html` |
| 4 | Test DB connection | `dataSource.getConnection()` → in tên DBMS |
| 5 | Đếm users | `userRepository.count()` → in số lượng |

Sử dụng `ObjectProvider<T>` thay vì inject trực tiếp để tránh lỗi khi ở Smoke Mode (khi beans không tồn tại).

---

## 1.3. Spring Profiles

```mermaid
graph LR
    subgraph "application.yaml (gốc)"
        BASE["spring.profiles.active = $SPRING_PROFILES_ACTIVE:dev<br/>spring.application.name = mini-food-delivery-backend"]
    end

    BASE -->|dev| DEV["application-dev.yaml"]
    BASE -->|prod| PROD["application-prod.yaml"]
    BASE -->|test| TEST["application-test.yaml"]

    DEV --- DEV_DETAIL["• DB: localhost:3306/mini_food_db<br/>• ddl-auto: update<br/>• show-sql: true<br/>• flyway.clean-disabled: false<br/>• JWT secret: hardcoded fallback<br/>• JWT expiration: 24h"]

    PROD --- PROD_DETAIL["• DB: $DB_URL (bắt buộc)<br/>• ddl-auto: validate<br/>• show-sql: false<br/>• JWT secret: $JWT_SECRET (bắt buộc)<br/>• JWT expiration: $JWT_EXPIRATION:24h"]

    TEST --- TEST_DETAIL["• Testcontainers MySQL 8.0<br/>• @ServiceConnection<br/>• ddl-auto: validate<br/>• Flyway migration tự động"]

    style DEV fill:#1a5c2b,color:#fff
    style PROD fill:#8b1a1a,color:#fff
    style TEST fill:#0f3460,color:#fff
```

### So sánh chi tiết các Profile

| Thuộc tính | `dev` | `prod` | `test` |
|:-----------|:------|:-------|:-------|
| **DB URL** | `localhost:3306/mini_food_db` + auto-create | `${DB_URL}` (env var) | Testcontainers dynamic |
| **ddl-auto** | `update` | `validate` | `validate` |
| **show-sql** | `true` | `false` | — |
| **Flyway clean** | `false` (disabled) | — | — |
| **JWT Secret** | Hardcoded fallback | `${JWT_SECRET}` | Test-specific |
| **DB Engine** | MySQL local | MySQL (any) | MySQL 8.0 (Docker) |

> [!WARNING]
> Profile `dev` chứa fallback JWT secret hardcoded. **Không bao giờ** sử dụng giá trị này trong production.

---

## 1.4. Configuration Classes

### 1.4.1. SecurityConfig

[SecurityConfig.java](file:///c:/Users/bachp/Downloads/Mini-Food-Delivery/SRC/backend/src/main/java/com/example/server/config/SecurityConfig.java)

Cấu hình trung tâm của Spring Security:

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity    // Kích hoạt @PreAuthorize
@RequiredArgsConstructor
public class SecurityConfig {
    
    @Bean SecurityFilterChain filterChain(HttpSecurity http) {
        http.csrf(AbstractHttpConfigurer::disable)              // Stateless = no CSRF
            .cors(cors -> cors.configurationSource(...))        // Custom CORS
            .sessionManagement(STATELESS)                       // No server-side session
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()     // Public auth
                .requestMatchers("/api/public/**").permitAll()   // Public endpoints
                .requestMatchers("/api/dev/**").permitAll()      // Dev tools
                .requestMatchers("/ws/**").permitAll()           // WebSocket
                .requestMatchers("/swagger-ui/**").permitAll()   // API docs
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/shipper/**").hasAnyRole("SHIPPER", "ADMIN")
                .requestMatchers("/api/owner/**").hasAnyRole("OWNER", "ADMIN")
                .anyRequest().authenticated()
            );
        
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
```

**CORS Configuration:**

| Thuộc tính | Giá trị |
|:-----------|:--------|
| Allowed Origins | `http://localhost:5173`, `http://127.0.0.1:5173` |
| Allowed Methods | GET, POST, PUT, PATCH, DELETE, OPTIONS |
| Allowed Headers | Authorization, Content-Type, X-Auth-Token, X-Requested-With, Accept, Origin |
| Exposed Headers | X-Auth-Token, Authorization |
| Allow Credentials | `true` |
| Max Age | 3600s (1 giờ) |

### 1.4.2. WebSocketConfig

[WebSocketConfig.java](file:///c:/Users/bachp/Downloads/Mini-Food-Delivery/SRC/backend/src/main/java/com/example/server/config/WebSocketConfig.java)

Hệ thống sử dụng **STOMP over WebSocket** với SockJS fallback:

```mermaid
sequenceDiagram
    participant S as Shipper Client
    participant WS as WebSocket (/ws)
    participant INT as ChannelInterceptor
    participant CTRL as LocationWebSocketController
    participant DB as ShipperLocationRepository
    participant C as Customer Client

    S->>WS: CONNECT (Authorization: Bearer <token>)
    WS->>INT: preSend() — JWT validation
    INT->>INT: Extract token → validate → set Principal
    INT-->>WS: Authenticated connection

    S->>CTRL: SEND /app/shipper/location (lat, lng, orderId)
    CTRL->>CTRL: Security check (authenticatedId == shipperId)
    CTRL->>DB: save(ShipperLocation)
    CTRL->>C: BROADCAST /topic/order/{orderId}
    
    C->>WS: SUBSCRIBE /topic/order/42
    Note over C: Receives real-time location updates
```

**Cấu hình chi tiết:**

- **Endpoint:** `/ws` với SockJS fallback (`withSockJS()`)
- **Broker prefix:** `/topic` — Clients subscribe tới các topic
- **App prefix:** `/app` — Clients gửi messages tới đây
- **CORS:** Cho phép tất cả origins (`setAllowedOriginPatterns("*")`)
- **Auth:** JWT validation trong `configureClientInboundChannel` interceptor

> [!IMPORTANT]
> WebSocket auth xảy ra tại thời điểm **STOMP CONNECT**, không phải mỗi message. Token được validate một lần và Principal được gán cho toàn bộ session.

### 1.4.3. OpenApiConfig

Cấu hình Swagger/OpenAPI documentation:
- **Title:** Mini Food Delivery API
- **Security Scheme:** JWT Bearer Token (`bearerAuth`)
- **Mọi endpoint** được apply security requirement mặc định

### 1.4.4. MapClientConfig

Cấu hình `RestClient` bean cho external API calls:
- **Purpose:** Gọi API bản đồ bên ngoài (geocoding, routing)
- **User-Agent:** Custom header để tránh bị block

### 1.4.5. WebConfig

Global CORS configuration cho MVC layer (bổ sung cho SecurityConfig CORS):
- Áp dụng cho tất cả path patterns

---

## 1.5. Flyway Database Migrations

Hệ thống sử dụng **10 migration files** để quản lý schema:

```mermaid
timeline
    title Lịch sử Migration
    V1 : Initial Schema
       : 12 bảng gốc
       : users, restaurants, orders, etc.
    V2 : Add is_deleted
       : Thêm soft delete cho categories
    V3 : Audit + Seed
       : Thêm timestamp columns
       : Seed 7 restaurant categories
    V4 : Owner Requests
       : Tạo bảng owner_requests
       : Workflow đăng ký chủ nhà hàng
    V5 : Cascade Deletes
       : Thêm ON DELETE CASCADE
       : Cho các FK relationships
    V6 : Nullable Shipper
       : delivery_assignments.shipper_id
       : Cho phép NULL (UNASSIGNED state)
    V7 : Shipper Requests
       : Tạo bảng shipper_requests
       : Workflow đăng ký shipper
    V8 : Image LONGTEXT
       : restaurant.image_url → LONGTEXT
       : Hỗ trợ base64 encoded images
    V9 : Optimistic Locking
       : Thêm version column vào orders
       : và delivery_assignments
    V10 : Security Hardening
        : Thêm failed_login_attempts và
        : account_locked_until vào users;
        : Tạo bảng refresh_tokens
```

> [!NOTE]
> Migration V6 là bước quan trọng cho Event-Driven Architecture: cho phép tạo `DeliveryAssignment` với `shipper_id = NULL` khi đơn hàng ở trạng thái `READY` nhưng chưa có shipper nhận. Mới nhất, migration V10 bổ sung thêm các trường bảo mật phục vụ đăng nhập chống brute force (lockout) và duy trì phiên đăng nhập (refresh token).
