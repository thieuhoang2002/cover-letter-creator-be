# TECHSTACK.md — Cover Letter Creator Backend

## 1. Nền Tảng & Ngôn Ngữ (Platform & Language)

| Thành phần | Phiên bản | Ghi chú |
|---|---|---|
| **Java** | **21 (LTS)** | Eclipse Temurin / OpenJDK 21 |
| **Build Tool** | **Maven Wrapper (`mvnw`)** | Tích hợp sẵn, không yêu cầu cài Maven toàn cục |
| **Framework** | **Spring Boot 3.4.3** | Bản phát hành ổn định mới nhất |
| **Spring Framework** | **6.2.x** | Jakarta EE 10 |
| **Database** | **TiDB Cloud Serverless** | MySQL 8.0 wire-compatible, AWS ap-southeast-1, SSL/TLS Mode |

---

## 2. Danh Mục Thư Viện Cốt Lõi (`pom.xml`)

### 2.1. Web, REST & Validation
| Thư viện | ArtifactId | Phiên bản | Ghi chú |
|---|---|---|---|
| Spring Boot Web | `spring-boot-starter-web` | 3.4.3 | Xây dựng REST API, Embedded Tomcat 10.1 |
| Jakarta Validation | `jakarta.validation-api` | 3.1.1 | Kiểm tra dữ liệu đầu vào (JSR-380) |
| Hibernate Validator | `hibernate-validator` | 8.x | Triển khai xác thực dữ liệu backend |

### 2.2. Bảo Mật, Xác Thực & Rate Limiting
| Thư viện | ArtifactId | Phiên bản | Ghi chú |
|---|---|---|---|
| Spring Security | `spring-boot-starter-security` | 3.4.3 | Phân quyền truy cập RBAC (`user`, `vip`, `admin`), Stateless Session |
| OAuth2 Client | `spring-boot-starter-oauth2-client` | 3.4.3 | Đăng nhập bằng tài khoản GitHub & Google (bảo toàn role DB & avatar) |
| JJWT API / Impl / Jackson | `jjwt-*` | 0.12.5 | JWT Authentication HMAC-SHA512 |
| Bucket4j / In-Memory Filter | Custom Filter | v1 | Chống brute-force và lạm dụng API AI (HTTP 429) |

### 2.3. Cơ Sở Dữ Liệu & ORM
| Thư viện | ArtifactId | Phiên bản | Ghi chú |
|---|---|---|---|
| Spring Data JPA | `spring-boot-starter-data-jpa` | 3.4.3 | Quản lý 14 thực thể dữ liệu qua Hibernate 6.6 |
| Spring JDBC | `spring-boot-starter-jdbc` | 3.4.3 | Hỗ trợ thực thi native SQL |
| MySQL Connector/J | `mysql-connector-j` | 8.x | Trình điều khiển kết nối MySQL / TiDB Cloud |

### 2.4. Trí Tuệ Nhân Tạo & Cloud Storage
| Dịch vụ / Thư viện | Tên thư viện / SDK | Phiên bản | Chức năng |
|---|---|---|---|
| **Groq Cloud API** | REST API Client (RestTemplate) | v1 | Model `openai/gpt-oss-120b`, fallback `llama-3.3-70b-versatile`, Multi-key comma rotation, Semaphore Concurrency Limiter |
| **Cloudflare R2** | AWS Java SDK S3 (`aws-java-sdk-s3`) | 1.12.x | Lưu trữ file PDF và Avatar qua giao thức S3-compatible, cơ chế Auto-cleanup tự động xóa file cũ |
| **PDF Generation** | `com.itextpdf:html2pdf` | **4.0.3** | Biên dịch HTML + CSS inline sang file PDF chuẩn A4 |
| **Fonts Unicode** | Times New Roman TTF | 4 variants | Nạp trực tiếp từ `resources/fonts/` chống lỗi vỡ font tiếng Việt |

### 2.5. Tiện Ích & Mail
| Thư viện | ArtifactId | Phiên bản | Ghi chú |
|---|---|---|---|
| Project Lombok | `lombok` | Latest | Boilderplate reduction kết hợp explicit standard Java getters/setters |
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
├── controller/              # REST Controllers xử lý API endpoints
│   ├── AICVPdfController.java
│   ├── AuthController.java
│   ├── CoverLetterPdfController.java
│   ├── FollowedCVController.java       # Quản lý theo dõi CV, upload PDF, quota VIP
│   ├── HtmlGenerationController.java   # Sinh CV AI với Groq Cloud
│   ├── LoginController.java
│   ├── ModernCVPdfController.java
│   ├── SocialLoginController.java      # Google/GitHub callback & avatar preservation
│   ├── TemplateController.java
│   ├── TemplateModernCVController.java
│   ├── UserController.java             # Profile, đổi avatar R2, đổi mật khẩu
│   └── VipUpgradeRequestController.java # Quản lý yêu cầu VIP cho User & Admin
├── dto/                     # Request / Response DTOs chuẩn hóa Jakarta Validation
├── model/                   # 14 JPA Entity models (User, Template, FollowedCV, VipUpgradeRequest, ...)
├── repository/              # Spring Data JPA interfaces
└── service/                 # Tầng nghiệp vụ xử lý logic (GroqAIService, CloudflareR2Service, PdfService, ...)
```

---

## 4. Trạng Thái Biên Dịch & Triển Khai
- **Compiler:** `javac 21` (Oracle / Eclipse Temurin).
- **Compile Status:** ✅ **BUILD SUCCESS** (`./mvnw clean compile` - 70 source files biên dịch thành công, 0 error).
- **Production Server:** Render Web Service kết nối TiDB Cloud Serverless.
