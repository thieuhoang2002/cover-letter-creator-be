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

- 🔐 **Xác thực & Bảo mật:**
  - JWT Authentication (HMAC-SHA512) thế hệ mới với bộ giải mã siêu bền bỉ (hỗ trợ Base64, Base64URL và băm tự động SHA-512).
  - Hỗ trợ Social Login đa nền tảng: Google OAuth2 & GitHub OAuth2.
  - CORS linh hoạt (`setAllowedOriginPatterns("*")`) tương thích trơn tru với Vercel và localhost.
  - Quy trình khôi phục mật khẩu bảo mật qua mã xác thực gửi bằng Gmail SMTP.
- 🤖 **Sinh nội dung CV bằng AI (Groq Cloud):**
  - Tích hợp **Groq Cloud API** sử dụng model tiên tiến **`openai/gpt-oss-120b`** (Primary) kết hợp cơ chế tự động fallback sang **`llama-3.3-70b-versatile`** khi gặp lỗi quota hoặc service quá tải.
  - Tự động định dạng HTML và CSS Inline chuẩn tỉ lệ A4, tối ưu chống tràn trang khi xuất PDF.
- 🖨️ **Xử lý PDF & Lưu trữ Cloudflare R2:**
  - Biên dịch HTML sang PDF chính xác bằng **iText html2pdf 4.0.3**.
  - Tích hợp sẵn font Unicode Times New Roman & DejaVu tiếng Việt trong container Alpine.
  - Tải file PDF trực tiếp về trình duyệt qua Binary Stream (`application/pdf`) với header `Content-Disposition`.
  - Đồng bộ lưu trữ vĩnh viễn trên **Cloudflare R2** (S3-compatible bucket `cover-letter-cv-storage`), phân phối qua CDN R2 Public URL.
  - **Deduplication Guard:** Tự động chống trùng lặp bản ghi xuất PDF trong khung thời gian 10 giây (ngăn chặn double-click từ người dùng).
- 📋 **Quản lý Template & Bộ Dữ Liệu 11 Mẫu Sẵn Có:**
  - **6 mẫu Đơn xin việc (Cover Letter)**: 3 mẫu Chuẩn Cơ Quan Nhà Nước + 3 mẫu Doanh Nghiệp Hiện Đại.
  - **5 mẫu CV Hiện Đại (Modern CV)**: 2 mẫu Cơ Quan Nhà Nước / Viên Chức + 3 mẫu Hiện Đại (Tech, Business, Design).
  - Quản lý profile chi tiết: Kỹ năng, Kinh nghiệm, Học vấn, Chứng chỉ, Sở thích.
  - Theo dõi trạng thái ứng tuyển thông qua tính năng CV Followed.

---

## 🌿 Chiến Lược Nhánh (Branching Strategy)

- **`main`**: Nhánh Production ổn định, kết nối trực tiếp với Render Web Service và Vercel Frontend.
- **`dev`**: Nhánh phát triển tính năng mới (UI/UX nâng cao, AI Streaming, ATS Scoring, Public Sharing). Mọi đóng góp code đều được thực hiện trên nhánh này trước khi merge vào `main`.

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
Điền các giá trị thực tế (Database, Groq API key, Cloudflare R2, OAuth2 credentials) vào file `application.properties` hoặc thiết lập biến môi trường hệ điều hành.

### 3. Khởi Chạy Ứng Dụng
```bash
mvn clean spring-boot:run
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
