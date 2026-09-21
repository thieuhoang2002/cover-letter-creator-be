# TECHSTACK.md — Cover Letter Creator Backend

## 1. Nền Tảng & Ngôn Ngữ (Platform & Language)

| Thành phần | Phiên bản | Ghi chú |
|---|---|---|
| **Java** | **21 (LTS)** | Eclipse Temurin / OpenJDK 21 |
| **Build Tool** | **Maven Wrapper (`mvnw`)** | Tích hợp sẵn, không yêu cầu cài Maven toàn cục |
| **Framework** | **Spring Boot 3.4.3** | Bản phát hành ổn định mới nhất |
| **Spring Framework** | **6.2.x** | Jakarta EE 10 |

---

## 2. Danh Mục Thư Viện Cốt Lõi (`pom.xml`)

### 2.1. Web, REST & Validation
| Thư viện | ArtifactId | Phiên bản | Ghi chú |
|---|---|---|---|
| Spring Boot Web | `spring-boot-starter-web` | 3.4.3 | Xây dựng REST API, Embedded Tomcat 10.1 |
| Jakarta Validation | `jakarta.validation-api` | 3.1.1 | Kiểm tra dữ liệu đầu vào (JSR-380) |

### 2.2. Bảo Mật & Xác Thực (Security & Auth)
| Thư viện | ArtifactId | Phiên bản | Ghi chú |
|---|---|---|---|
| Spring Security | `spring-boot-starter-security` | 3.4.3 | Phân quyền truy cập, Stateless Session |
| OAuth2 Client | `spring-boot-starter-oauth2-client` | 3.4.3 | Đăng nhập bằng tài khoản GitHub & Google |
| JJWT API | `jjwt-api` | 0.12.5 | Định nghĩa interface cho JWT |
| JJWT Impl | `jjwt-impl` | 0.12.5 | Thuật toán mã hóa HMAC-SHA512 |
| JJWT Jackson | `jjwt-jackson` | 0.12.5 | Chuyển đổi payload JSON sang token |

### 2.3. Cơ Sở Dữ Liệu & ORM
| Thư viện | ArtifactId | Phiên bản | Ghi chú |
|---|---|---|---|
| Spring Data JPA | `spring-boot-starter-data-jpa` | 3.4.3 | Quản lý dữ liệu qua Hibernate 6.6 |
| Spring JDBC | `spring-boot-starter-jdbc` | 3.4.3 | Hỗ trợ thực thi native SQL |
| MySQL Connector/J | `mysql-connector-j` | 8.x | Trình điều khiển kết nối MySQL / MariaDB |

### 2.4. Trí Tuệ Nhân Tạo & Cloud Storage
| Dịch vụ / Thư viện | Tên thư viện / SDK | Phiên bản | Chức năng |
|---|---|---|---|
| **Groq Cloud API** | REST API Client (RestTemplate / HttpClient) | v1 | Sinh CV: `openai/gpt-oss-120b`, fallback `llama-3.3-70b-versatile` |
| **Cloudflare R2** | AWS Java SDK S3 (`aws-java-sdk-s3`) | 1.12.x | Lưu trữ đám mây file PDF qua giao thức S3-compatible |
| **PDF Generation** | `com.itextpdf:html2pdf` | **4.0.3** | Biên dịch HTML + CSS inline sang file PDF chuẩn A4 |
| **Fonts Unicode** | Times New Roman TTF | 4 variants | Nạp trực tiếp từ `resources/fonts/` chống lỗi vỡ font tiếng Việt |

### 2.5. Tiện Ích & Mail
| Thư viện | ArtifactId | Phiên bản | Ghi chú |
|---|---|---|---|
| Project Lombok | `lombok` | Latest | Giảm boilerplate (`@Data`, `@Getter`, `@Setter`, `@Builder`) |
| Spring Mail | `spring-boot-starter-mail` | 3.4.3 | Gửi mã xác nhận khôi phục mật khẩu qua Gmail SMTP |
| Jackson Databind | `jackson-databind` | 2.18.x | Xử lý JSON serialization / deserialization |

---

## 3. Cấu Trúc Gói Ứng Dụng (Package Structure)

```
cover.letter.creator
├── config/                  # Cấu hình bảo mật, CORS, JWT, Cloudflare R2, WebMvc
│   ├── CloudflareR2Config.java
│   ├── JwtAuthenticationFilter.java
│   ├── JwtUtil.java
│   ├── SecurityConfig.java
│   └── WebConfig.java
├── controller/              # 13 REST Controllers xử lý API endpoints
│   ├── AICVPdfController.java
│   ├── AuthController.java
│   ├── CoverLetterPdfController.java
│   ├── FollowCVController.java
│   ├── HtmlGenerationController.java
│   ├── LoginController.java
│   ├── ModernCVPdfController.java
│   ├── TemplateController.java
│   ├── TemplateModernCVController.java
│   └── UserController.java
├── dto/                     # Request / Response DTOs
├── model/                   # JPA Entity models (User, Template, ModernCV, FollowCV, ...)
├── repository/              # Spring Data JPA interfaces
└── service/                 # Tầng nghiệp vụ xử lý logic (GroqAIService, CloudflareR2Service, PdfService, ...)
```

---

## 4. Trạng Thái Biên Dịch & Kiểm Tra
- **Compiler:** `javac 21.0.6` (Oracle / Eclipse Temurin).
- **Compile Status:** ✅ **BUILD SUCCESS** (`.\mvnw.cmd compile` - 65 source files biên dịch thành công, 0 error).
- **Runtime Test:** Port 8080 hoạt động ổn định, kết nối trực tiếp với MySQL XAMPP và Groq Cloud API.
