# 📄 Cover Letter & CV Creator - Backend API

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

Backend RESTful API phục vụ hệ sinh thái **Cover Letter & Modern CV Creator**. Ứng dụng cung cấp các dịch vụ thông minh bao gồm tạo hồ sơ, sinh CV dạng HTML chuẩn A4 thông qua AI (Groq / Llama 3.3), trích xuất file PDF chất lượng cao (iText 8) và đồng bộ lưu trữ trên đám mây Google Drive.

---

## 🌟 Tính Năng Nổi Bật

- 🔐 **Xác thực & Bảo mật:**
  - JWT Authentication (HMAC-SHA512) bảo vệ toàn bộ tài nguyên.
  - Hỗ trợ Social Login đa nền tảng: Google OAuth2 & GitHub OAuth2.
  - Quy trình khôi phục mật khẩu bảo mật qua mã xác thực gửi bằng Gmail SMTP.
- 🤖 **Sinh nội dung CV bằng AI:**
  - Tích hợp **Groq Cloud API** (`llama-3.3-70b-versatile` / `mixtral-8x7b-32768`) với tốc độ phản hồi cực nhanh.
  - Tự động định dạng HTML và CSS Inline chuẩn tỉ lệ A4, tối ưu chống tràn trang khi xuất PDF.
- 🖨️ **Xử lý PDF & Lưu trữ:**
  - Biên dịch HTML sang PDF chính xác bằng **iText html2pdf**.
  - Tích hợp sẵn font Unicode Times New Roman tiếng Việt đầy đủ biến thể (Regular, Bold, Italic).
  - Tự động upload và phân quyền file trên **Google Drive API** qua Service Account.
- 📋 **Quản lý Template & Theo dõi CV:**
  - Hệ thống template đa dạng: Classic Templates & Modern CV Templates.
  - Bộ tính năng quản lý profile chi tiết: Kỹ năng, Kinh nghiệm, Học vấn, Chứng chỉ, Sở thích.
  - Theo dõi trạng thái ứng tuyển thông qua tính năng CV Followed.

---

## 🛠️ Công Nghệ Sử Dụng

- **Ngôn ngữ:** Java 21 (LTS)
- **Framework:** Spring Boot 3.4.3 (Spring Web, Spring Security 6, Spring Data JPA)
- **Database:** MySQL 8.x / MariaDB (hỗ trợ XAMPP hoặc Cloud DB)
- **Tạo PDF:** iText html2pdf 4.0.3
- **AI Engine:** Groq Cloud API (Llama 3.3 70B)
- **Cloud Storage:** Google Drive API v3
- **Thư viện tiện ích:** Lombok, JJWT 0.12.5, Jackson

👉 *Chi tiết toàn bộ thư viện và phiên bản xem tại: [TECHSTACK.md](TECHSTACK.md)*

---

## 🚀 Hướng Dẫn Cài Đặt & Chạy Localhost

### 1. Yêu cầu môi trường
- JDK 21 trở lên.
- MySQL Server (khuyên dùng **XAMPP 8.2.x** bật cổng 3306).
- Maven Wrapper (`mvnw`) đã được tích hợp sẵn trong repo, không cần cài thêm Maven rời.

### 2. Thiết lập Database
Mở phpMyAdmin (`http://localhost/phpmyadmin`) và tạo database:
```sql
CREATE DATABASE cover_letter_creator_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Cấu hình ứng dụng
1. Copy file mẫu cấu hình:
   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```
2. Điền các thông số tương ứng trong `src/main/resources/application.properties`:
   - Cấu hình database kết nối (mặc định đã sẵn sàng cho XAMPP).
   - Điền API Key của Groq Cloud (`api.key=gsk_...`).
   - Cấu hình Mail Gmail và Google/GitHub OAuth nếu cần sử dụng các tính năng này.
3. Thêm file private key Google Service Account `calendar-438415-5bdb470fb244.json` vào thư mục `src/main/resources/` (để dùng tính năng upload Google Drive).

### 4. Khởi chạy ứng dụng

**Sử dụng Terminal / Command Line:**
```powershell
# Windows PowerShell:
.\mvnw.cmd spring-boot:run

# Linux / macOS:
./mvnw spring-boot:run
```

**Sử dụng Eclipse IDE:**
- Chuột phải vào `CoverLetterCreatorApplication.java` ➔ **Run As** ➔ **Java Application**.

Sau khi chạy thành công, ứng dụng sẽ lắng nghe tại: **`http://localhost:8080`**.

---

## 📚 Hệ Thống Tài Liệu Chi Tiết

Dự án đi kèm bộ tài liệu kiến trúc đầy đủ:

| Tài liệu | Mô tả |
|---|---|
| [PROJECT_SPEC.md](PROJECT_SPEC.md) | Chi tiết luồng nghiệp vụ API, prompt AI, schema database và auth flow. |
| [TECHSTACK.md](TECHSTACK.md) | Danh mục công cụ, phiên bản JDK, Spring Boot và thư viện dependencies. |
| [HANDOVER.md](HANDOVER.md) | Bộ sưu tập mẫu cURL test API, tài liệu bàn giao kỹ thuật & troubleshooting. |
| [DOCKER.md](DOCKER.md) | Hướng dẫn đóng gói Dockerfile và triển khai trọn gói bằng Docker Compose. |
| [TODO.md](TODO.md) | Trạng thái các endpoint, kế hoạch phát triển và tối ưu bảo mật. |

---

## 🔒 Bản Quyền & Bảo Mật

- Dự án đã được quét sạch lịch sử commit nhạy cảm.
- Tất cả mật khẩu ứng dụng, API key và private credentials đều được loại trừ khỏi Git thông qua `.gitignore`. Vui lòng không commit file `application.properties` và file `.json` lên repository công khai.
