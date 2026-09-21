# 📄 Cover Letter & CV Creator - Backend API

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

Backend RESTful API phục vụ hệ sinh thái **Cover Letter & Modern CV Creator**. Ứng dụng cung cấp các dịch vụ thông minh bao gồm tạo hồ sơ, sinh CV dạng HTML chuẩn A4 thông qua AI (**Groq Cloud API** với model `openai/gpt-oss-120b` & `llama-3.3-70b-versatile`), trích xuất file PDF chất lượng cao (iText html2pdf) kết hợp tải trực tiếp (`application/pdf` binary stream) và lưu trữ đám mây tốc độ cao **Cloudflare R2 (S3-compatible)**.

---

## 🌟 Tính Năng Nổi Bật

- 🔐 **Xác thực & Bảo mật:**
  - JWT Authentication (HMAC-SHA512) bảo vệ toàn bộ tài nguyên.
  - Hỗ trợ Social Login đa nền tảng: Google OAuth2 & GitHub OAuth2.
  - Quy trình khôi phục mật khẩu bảo mật qua mã xác thực gửi bằng Gmail SMTP.
- 🤖 **Sinh nội dung CV bằng AI (Groq Cloud):**
  - Tích hợp **Groq Cloud API** sử dụng model tiên tiến **`openai/gpt-oss-120b`** (Primary) kết hợp cơ chế tự động fallback sang **`llama-3.3-70b-versatile`** khi gặp lỗi quota hoặc service quá tải.
  - Tự động định dạng HTML và CSS Inline chuẩn tỉ lệ A4, tối ưu chống tràn trang khi xuất PDF.
- 🖨️ **Xử lý PDF & Lưu trữ Cloudflare R2:**
  - Biên dịch HTML sang PDF chính xác bằng **iText html2pdf 4.0.3**.
  - Tích hợp sẵn font Unicode Times New Roman tiếng Việt đầy đủ biến thể (Regular, Bold, Italic, Bold Italic).
  - Tải file PDF trực tiếp về trình duyệt qua Binary Stream (`application/pdf`) với header `Content-Disposition`.
  - Đồng bộ lưu trữ vĩnh viễn trên **Cloudflare R2** (S3-compatible bucket `cover-letter-cv-storage`), phân phối qua CDN R2 Public URL.
  - **Deduplication Guard:** Tự động chống trùng lặp bản ghi xuất PDF trong khung thời gian 10 giây (ngăn chặn double-click từ người dùng).
- 📋 **Quản lý Template & Theo dõi CV:**
  - Hệ thống template mẫu phong phú đã được nạp sẵn vào cơ sở dữ liệu: Classic Cover Letters & Modern CV Templates.
  - Quản lý profile chi tiết: Kỹ năng, Kinh nghiệm, Học vấn, Chứng chỉ, Sở thích.
  - Theo dõi trạng thái ứng tuyển thông qua tính năng CV Followed.

---

## 🛠️ Công Nghệ Sử Dụng

- **Ngôn ngữ:** Java 21 (LTS)
- **Framework:** Spring Boot 3.4.3 (Spring Web, Spring Security 6, Spring Data JPA)
- **Database:** MySQL 8.x / MariaDB (hỗ trợ XAMPP hoặc Cloud DB)
- **Tạo PDF:** iText html2pdf 4.0.3
- **AI Engine:** Groq Cloud API (`openai/gpt-oss-120b`, fallback `llama-3.3-70b-versatile`)
- **Cloud Storage:** Cloudflare R2 (AWS S3 Client SDK)
- **Thư viện tiện ích:** Lombok, JJWT 0.12.5, Jackson

👉 *Chi tiết toàn bộ thư viện và phiên bản xem tại: [TECHSTACK.md](TECHSTACK.md)*

---

## 🚀 Hướng Dẫn Cài Đặt & Chạy Localhost

### 1. Yêu cầu môi trường
- JDK 21 trở lên.
- MySQL Server (khuyên dùng **XAMPP 8.2.x** bật cổng 3306).
- Maven Wrapper (`mvnw`) đã được tích hợp sẵn trong repo, không cần cài thêm Maven rời.

### 2. Thiết lập Database & Seed Dữ Liệu Mẫu
1. Mở phpMyAdmin (`http://localhost/phpmyadmin`) và tạo database:
   ```sql
   CREATE DATABASE cover_letter_creator_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. Khởi động ứng dụng Spring Boot một lần để JPA tự sinh cấu trúc bảng (`ddl-auto=update`).
3. Chạy file seed mẫu Cover Letter và Modern CV vào database:
   ```powershell
   C:\xampp\mysql\bin\mysql.exe -u root cover_letter_creator_db < ..\cover-letter-creator-fe\seed_templates.sql
   ```

### 3. Cấu hình ứng dụng (`application.properties`)
File cấu hình tại `src/main/resources/application.properties`:
```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/cover_letter_creator_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=

# Groq Cloud AI
api.key=gsk_...
groq.model=openai/gpt-oss-120b
groq.fallback-model=llama-3.3-70b-versatile

# Cloudflare R2 Storage (S3-Compatible)
cloudflare.r2.account-id=your_cloudflare_account_id
cloudflare.r2.access-key=your_cloudflare_access_key
cloudflare.r2.secret-key=your_cloudflare_secret_key
cloudflare.r2.bucket-name=cover-letter-cv-storage
cloudflare.r2.public-url=https://pub-your-bucket-id.r2.dev
```

### 4. Khởi chạy ứng dụng

**Sử dụng Terminal / Command Line:**
```powershell
# Windows PowerShell:
.\mvnw.cmd spring-boot:run

# Linux / macOS:
./mvnw spring-boot:run
```

Sau khi chạy thành công, API backend lắng nghe tại: **`http://localhost:8080`**.

---

## 📚 Hệ Thống Tài Liệu Chi Tiết

| Tài liệu | Mô tả |
|---|---|
| [PROJECT_SPEC.md](PROJECT_SPEC.md) | Chi tiết luồng nghiệp vụ API, prompt AI, schema database và auth flow. |
| [TECHSTACK.md](TECHSTACK.md) | Danh mục công cụ, phiên bản JDK, Spring Boot và thư viện dependencies. |
| [HANDOVER.md](HANDOVER.md) | Bộ sưu tập mẫu cURL test API, tài liệu bàn giao kỹ thuật & troubleshooting. |
| [DOCKER.md](DOCKER.md) | Hướng dẫn đóng gói Dockerfile và triển khai trọn gói bằng Docker Compose. |
| [TODO.md](TODO.md) | Trạng thái các endpoint, lịch sử nâng cấp và tối ưu hóa hệ thống. |

---

## 👥 Nhóm Tác Giả & Bản Quyền
- Dự án phát triển phục vụ người tìm việc và sinh viên mới tốt nghiệp.
- Mọi đóng góp xin gửi pull request hoặc liên hệ qua email quản trị viên.
