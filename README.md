# 📄 Cover Letter & CV Creator - Backend API

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Database](https://img.shields.io/badge/Database-TiDB%20Cloud%20Serverless-00A4A6.svg)](https://tidbcloud.com/)
[![Render](https://img.shields.io/badge/Deployment-Render%20Live-black.svg)](https://cover-letter-creator-be-eadm.onrender.com)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

Backend RESTful API phục vụ hệ sinh thái **Cover Letter & Modern CV Creator**. Ứng dụng cung cấp các dịch vụ thông minh bao gồm tạo hồ sơ, sinh CV dạng HTML chuẩn A4 thông qua AI (**Groq Cloud API** với model `openai/gpt-oss-120b` & `llama-3.3-70b-versatile`), trích xuất file PDF chất lượng cao (iText html2pdf) kết hợp tải trực tiếp (`application/pdf` binary stream) và lưu trữ đám mây tốc độ cao **Cloudflare R2 (S3-compatible)**.

---

## 🌐 Môi Trường Triển Khai Production (Live)
* **Backend Render URL:** `https://cover-letter-creator-be-eadm.onrender.com`
* **Health Check Endpoint:** `https://cover-letter-creator-be-eadm.onrender.com/api/ai/health`
* **Cơ Sở Dữ Liệu:** TiDB Cloud Serverless (MySQL 8.0 wire-compatible, AWS ap-southeast-1, TLS/SSL Mode)
* **Frontend Tương Ứng:** `https://cover-letter-creator-fe.vercel.app`

---

## 🌟 Tính Năng Nổi Bật

- 🔐 **Xác thực, Phân Quyền & Bảo Mật Toàn Diện:**
  - JWT Authentication (HMAC-SHA512) thế hệ mới với claim role chuẩn hóa (`user` / `vip` / `admin`).
  - Spring Security 6 RBAC: Bảo vệ nghiêm ngặt các endpoint quản trị `/api/admin/**`, `/api/users/profile`, duyệt yêu cầu VIP và quản lý mẫu templates.
  - **In-Memory Rate Limiting (Bucket4j)**: Giới hạn tần suất gọi API Đăng nhập, Đăng ký, Quên mật khẩu và AI Groq (trả về HTTP 429 Too Many Requests).
  - Quy trình khôi phục mật khẩu bảo mật qua email (Gmail SMTP), sửa lỗi timezone lệch giờ hết hạn token.
  - API kiểm tra mật khẩu (`has-password`) và đổi mật khẩu không cần mật khẩu cũ (`change-password-without-old`) cho tài khoản Google/GitHub.
  - Hỗ trợ Social Login đa nền tảng: Google OAuth2 & GitHub OAuth2 (bảo toàn role VIP/Admin trong DB và bảo vệ avatar tùy chỉnh của người dùng).
  - **Jakarta Bean Validation Toàn Diện**: `@Valid`, `@NotBlank`, `@Email`, `@Size` trên toàn bộ DTOs kết hợp Global Exception Handler.
  - CORS linh hoạt tương thích trơn tru với Vercel và môi trường phát triển cục bộ.
- 🤖 **Sinh Nội Dung CV Bằng AI (Groq Cloud) Đa Key & Điều Phối Luồng:**
  - Tích hợp **Groq Cloud API** sử dụng model tiên tiến **`openai/gpt-oss-120b`** (Primary) kết hợp cơ chế tự động fallback sang **`llama-3.3-70b-versatile`**.
  - **Multi API Keys Rotation**: Hỗ trợ cấu hình nhiều API Key cách nhau bởi dấu phẩy (`api.key=key1,key2,key3`), tự động xoay vòng Round-Robin và tự động nhảy key khi gặp lỗi Rate Limit (HTTP 429).
  - **Concurrency Limiting & Queue**: Giới hạn tối đa 3 tác vụ AI đồng thời thông qua Semaphore với thời gian chờ hàng đợi 45 giây.
  - Tự động định dạng HTML và CSS Inline chuẩn tỉ lệ A4, tối ưu chống tràn trang khi xuất PDF.
- 🖨️ **Xử Lý PDF & Lưu Trữ Đám Mây Cloudflare R2:**
  - Biên dịch HTML sang PDF chính xác bằng **iText html2pdf 4.0.3**.
  - Tích hợp sẵn font Unicode Times New Roman & DejaVu tiếng Việt trong container Alpine.
  - Tải file PDF trực tiếp về trình duyệt qua Binary Stream (`application/pdf`) với header `Content-Disposition`.
  - Đồng bộ lưu trữ vĩnh viễn trên **Cloudflare R2** (S3-compatible bucket `cover-letter-cv-storage`), phân phối qua CDN R2 Public URL.
  - **Upload CV PDF Cá Nhân Từ Máy Khách**: Hỗ trợ khách tự upload file PDF cá nhân lên R2 qua API `POST /api/follow-cv/upload` (tối đa 10MB/file).
  - **Hệ Thống Quota & Gói VIP**: Giới hạn 3 CV PDF cho tài khoản Thường, **30 CV PDF cho tài khoản VIP**, không giới hạn cho Admin.
  - **Tự Động Dọn Dẹp File Trên R2 (Auto-cleanup)**:
    - Khi người dùng tải avatar mới: Tự động phát hiện và xóa vĩnh viễn file avatar cũ trên R2.
    - Khi người dùng xóa CV tải lên trong Theo dõi ứng tuyển: Tự động xóa file PDF tương ứng trên bucket R2.
  - **Deduplication Guard:** Tự động chống trùng lặp bản ghi xuất PDF trong khung thời gian 10 giây (ngăn chặn double-click từ người dùng).
- 📋 **Quản Lý Template & Bộ Dữ Liệu 11 Mẫu Sẵn Có:**
  - **6 mẫu Đơn xin việc (Cover Letter)**: 3 mẫu Chuẩn Cơ Quan Nhà Nước + 3 mẫu Doanh Nghiệp Hiện Đại.
  - **5 mẫu CV Hiện Đại (Modern CV)**: 2 mẫu Cơ Quan Nhà Nước / Viên Chức + 3 mẫu Hiện Đại (Tech, Business, Design).
  - Quản lý profile chi tiết: Kỹ năng, Kinh nghiệm, Học vấn, Chứng chỉ, Sở thích.
  - Theo dõi trạng thái ứng tuyển thông qua tính năng CV Followed (phân biệt nguồn CV Uploaded 📎 vs Hệ thống 🔗).
- 💎 **Hệ Thống Nâng Cấp & Quản Trị VIP:**
  - API người dùng gửi yêu cầu nâng cấp gói VIP (`POST /api/follow-cv/vip-request`).
  - Trang quản trị Admin xem danh sách và trực tiếp Phê duyệt (Approve) / Từ chối (Reject) yêu cầu VIP.

---

## 🌿 Chiến Lược Nhánh (Branching Strategy)

- **`main`**: Nhánh Production ổn định, kết nối trực tiếp với Render Web Service và Vercel Frontend.
- **`dev`**: Nhánh phát triển tính năng mới. Mọi đóng góp code đều được thực hiện trên nhánh này trước khi merge vào `main`.

---

## 🚀 Hướng Dẫn Cài Đặt Cục Bộ (Local Development)

### 1. Yêu Cầu Môi Trường
- **JDK 21** trở lên
- **Maven 3.9+**
- **MySQL 8.0+** hoặc tài khoản **TiDB Cloud**

### 2. Cấu Hình Biến Môi Trường
Sao chép file mẫu:
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```
Điền các giá trị thực tế (Database, Groq API key, Cloudflare R2, OAuth2 credentials) vào file `application.properties` hoặc thiết lập biến môi trường hệ điều hành:
- `api.key`: Danh sách key Groq AI phân tách bằng dấu phẩy (ví dụ `gsk_key1,gsk_key2`).

### 3. Khởi Chạy Ứng Dụng
```bash
./mvnw clean spring-boot:run
```
API sẽ lắng nghe tại: `http://localhost:8080`.

---

## 🐳 Triển Khai Docker
```bash
# Build Docker Image
docker build -t cover-letter-creator-be .

# Chạy container với biến môi trường
docker run -d -p 8080:8080 --name clc-backend cover-letter-creator-be
```
