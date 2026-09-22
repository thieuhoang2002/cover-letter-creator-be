# 📋 PROJECT ROADMAP & AUDIT CHECKLIST — BACKEND (TODO.md)

Theo dõi chi tiết lộ trình phát triển, các tính năng đã hoàn thiện và danh sách nhiệm vụ kỹ thuật nâng cấp toàn diện dự án **Cover Letter Creator Backend** trên nhánh `main` và `dev`.

---

## ✅ 1. Trạng Thái Đã Hoàn Thành (Production & Live)

### 1.1. Hạ Tầng & Triển Khai
- [x] **Triển khai Render Live Web Service**: `https://cover-letter-creator-be-eadm.onrender.com`.
- [x] **Docker Alpine Production**: Multi-stage build Maven 3.9 + JRE 21 Alpine, tích hợp font DejaVu & fontconfig hỗ trợ tiếng Việt đầy đủ.
- [x] **Tối ưu RAM Container**: Cấu hình `-XX:+UseContainerSupport -Xmx384m` ổn định trên Render Free tier 512MB RAM.
- [x] **Cơ sở dữ liệu TiDB Cloud Serverless**: MySQL 8.0 wire-compatible trên AWS ap-southeast-1 kèm bắt buộc SSL/TLS, script cập nhật an toàn `update_tidb_database.sql`.
- [x] **Bộ Dữ Liệu 11 Mẫu Sẵn Có**: 6 Cover Letter (3 Nhà Nước + 3 Hiện Đại) và 5 Modern CV (2 Nhà Nước + 3 Hiện Đại).

### 1.2. Xác Thực, Phân Quyền & Bảo Mật Nâng Cao
- [x] **JWT Token Chuẩn Hóa**: Claim role trả về định dạng chữ thường (`user` / `vip` / `admin`).
- [x] **Spring Security 6 RBAC**:
  - [x] Phân quyền chặt chẽ các endpoint Admin (`hasRole("ADMIN")`): `/api/admin/**`, `/api/users/profile`, tạo/sửa/xóa mẫu templates.
  - [x] Cho phép xem mẫu công khai (`permitAll()`): `GET /api/templates/**`, `GET /api/templates-modern/**`.
  - [x] Khớp chính xác endpoint profile người dùng (`/api/users/profile/me`).
- [x] **OAuth2 Social Login Tinh Chỉnh**:
  - [x] Google OAuth2 & GitHub OAuth2: Đọc trực tiếp role từ DB qua `UserRepository` trong `AuthenticationSuccessHandler`, không ghi đè role `vip` hay `admin` của người dùng thành `user`.
  - [x] Không ghi đè avatar tùy chỉnh R2 của người dùng bằng avatar GitHub/Google khi đăng nhập lại.
- [x] **Rate Limiting (Bucket4j / Filter)**:
  - [x] Giới hạn tần suất gọi API đăng nhập, đăng ký, quên mật khẩu và tạo CV với Groq AI.
  - [x] Trả về mã lỗi `HTTP 429 Too Many Requests` kèm header `Retry-After`.
- [x] **Quy Trình Mật Khẩu An Toàn**:
  - [x] Sửa lỗi token reset password hết hạn sớm do lệch múi giờ (UTC vs GMT+7).
  - [x] Bổ sung endpoint `GET /api/users/profile/has-password` kiểm tra tài khoản đã có mật khẩu chưa.
  - [x] Bổ sung endpoint `POST /api/users/profile/change-password-without-old` dành riêng cho người dùng đăng nhập bằng Google/GitHub chưa tạo mật khẩu.
- [x] **Validation Toàn Diện (Jakarta Bean Validation)**:
  - [x] Thêm `@Valid` và các annotation `@NotBlank`, `@Email`, `@Size` trên toàn bộ DTOs (`LoginRequest`, `RegisterRequest`, `ChangePasswordRequest`, `UserProfileUpdateRequest`, `TemplateDTO`, v.v.).
  - [x] Global Exception Handler (`@RestControllerAdvice`) xử lý đẹp lỗi validation 400 Bad Request.

### 1.3. Trí Tuệ Nhân Tạo (Groq AI) Đa Key & Hàng Đợi
- [x] **Groq Cloud API**: Model chính `openai/gpt-oss-120b` kết hợp tự động fallback sang `llama-3.3-70b-versatile`.
- [x] **Multi-key Rotation (Comma-separated)**: Hỗ trợ nạp nhiều API key cách nhau bởi dấu phẩy `api.key=key1,key2,key3`. Tự động xoay vòng Round-Robin và tự động nhảy key khi gặp lỗi Rate Limit (HTTP 429).
- [x] **Key Optional Fallback**: Cấu hình `@Value("${api.key:}")` cho phép backend khởi động bình thường ngay cả khi chưa nạp key Groq.
- [x] **Concurrency Queue**: Sử dụng Semaphore giới hạn 3 luồng AI đồng thời, hàng đợi chờ tối đa 45 giây.

### 1.4. Xử Lý PDF, Cloudflare R2 & Quản Lý File Tự Động
- [x] **Lưu Trữ Đám Mây Cloudflare R2**: Bucket S3-compatible `cover-letter-cv-storage` kết nối qua AWS SDK S3.
- [x] **Xuất PDF Binary Stream**: Trả file trực tiếp qua luồng byte `application/pdf` kèm header `Content-Disposition`.
- [x] **Deduplication Guard**: Chống spam click tạo trùng lặp bản ghi PDF trong khung 10 giây.
- [x] **Upload Avatar Lên R2 (`POST /api/users/profile/avatar`)**: Hỗ trợ định dạng ảnh JPG/PNG/WebP <= 2MB, lưu vào thư mục `avatars/`.
- [x] **Tự Động Xóa Avatar Cũ Trên R2**: Khi tải avatar mới, hệ thống tự phát hiện và xóa avatar cũ khỏi R2 để tránh lãng phí dung lượng.
- [x] **Upload CV PDF Từ Máy Khách (`POST /api/follow-cv/upload`)**: Cho phép tải lên file PDF cá nhân (tối đa 10MB) lưu vào `customer-cvs/{userId}/`.
- [x] **Tự Động Xóa File CV Trên R2 Khi Xóa Bản Ghi**: Sửa triệt để lỗi cắt chuỗi key đường dẫn S3 trong `CloudflareR2Service.deleteFile`, đảm bảo file PDF trên bucket R2 bị xóa sạch 100%.

### 1.5. Hệ Thống Quota & Quản Trị Gói VIP
- [x] **Phân Cấp Hạn Mức Lưu Trữ (Upload Quota)**:
  - Tài khoản Thường (`user`): Tối đa **3 CV PDF**.
  - Tài khoản VIP (`vip`): Tối đa **30 CV PDF** (gấp 10 lần, chống lạm dụng tài nguyên R2).
  - Tài khoản Admin (`admin`): Không giới hạn (`Integer.MAX_VALUE`).
- [x] **Endpoint Tra Cứu Quota (`GET /api/follow-cv/quota`)**: Trả về `used`, `max`, `remaining`, `isVip`.
- [x] **Yêu Cầu Nâng Cấp VIP (`POST /api/follow-cv/vip-request`)**: Cho phép khách gửi yêu cầu đăng ký gói Pro VIP / Enterprise.
- [x] **Quản Trị VIP Cho Admin**:
  - `GET /api/admin/vip-requests`: Xem danh sách yêu cầu.
  - `PUT /api/admin/vip-requests/{id}/approve`: Duyệt nâng quyền người dùng thành `vip`.
  - `PUT /api/admin/vip-requests/{id}/reject`: Từ chối yêu cầu kèm lý do.

---

## 🚀 2. Kế Hoạch Nâng Cấp Tiếp Theo (Phase 3 Roadmap)

### ⚡ 2.1. AI Streaming & Tương Tác Trực Tiếp (SSE)
- [ ] Server-Sent Events (SSE) để stream nội dung CV sinh bởi Groq AI trực tiếp về màn hình theo thời gian thực (hiệu ứng gõ phím).
- [ ] Cho phép người dùng ra lệnh điều chỉnh từng đoạn văn bản cụ thể qua hội thoại AI (Chat with CV).

### 🎯 2.2. Đánh Giá Điểm Hồ Sơ (ATS Scoring)
- [ ] Phân tích độ tương thích giữa nội dung CV của ứng viên và Mô tả công việc (Job Description).
- [ ] Đưa ra điểm số ATS kèm các từ khóa (keywords) còn thiếu cần bổ sung.

### 🔗 2.3. Chia Sẻ Hồ Sơ Trực Tuyến (Public Sharing & Slug)
- [ ] Cung cấp đường dẫn web công khai cho mỗi CV/Cover Letter (ví dụ: `https://coverletter.app/p/hoang-thieu-senior-dev`).
- [ ] Cho phép nhà tuyển dụng quét mã QR để xem trực tiếp hồ sơ online mà không cần tải file PDF.
