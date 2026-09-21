# TODO.md — Cover Letter Creator Backend

## ✅ Các Hạng Mục Đã Hoàn Thành (Completed)

### 1. AI Migration & Upgrade (Groq Cloud)
- [x] Chuyển đổi từ mô hình cũ sang **Groq Cloud API** (`https://api.groq.com/openai/v1/chat/completions`).
- [x] Xử lý sự cố Groq thông báo `mixtral-8x7b-32768` decommissioned: nâng cấp model chính sang **`openai/gpt-oss-120b`** và model dự phòng sang **`llama-3.3-70b-versatile`**.
- [x] Bổ sung cơ chế tự động Fallback trong `GroqAIService.java` khi model chính gặp lỗi quota hoặc 400 Bad Request.
- [x] Tinh chỉnh Prompt kỹ thuật định dạng HTML & CSS inline chuẩn A4, loại bỏ các thuộc tính Flexbox không tương thích với iText 4.0.3.

### 2. PDF Engine & Cloudflare R2 Integration
- [x] Loại bỏ hoàn toàn sự phụ thuộc vào Google Drive API và Service Account JSON file.
- [x] Chuyển sang xuất PDF dạng **Binary Stream** (`application/pdf`) gửi kèm header `Content-Disposition` để trình duyệt tải trực tiếp.
- [x] Tích hợp lưu trữ đám mây vĩnh viễn trên **Cloudflare R2** (S3-compatible bucket `cover-letter-cv-storage`), phân phối qua public CDN domain.
- [x] **Deduplication Guard**: Bổ sung cơ chế chống trùng lặp tạo PDF trong vòng 10 giây tại `AICVPdfService.java`, ngăn ngừa tình trạng spam click tạo nhiều bản ghi cùng một file.

### 3. Quản Lý Template & Cơ Sở Dữ Liệu
- [x] Seed sẵn bộ 3 mẫu Cover Letter Classic (Kỹ sư phần mềm, Chuyên viên Marketing, Kế toán trưởng) vào bảng `templates`.
- [x] Seed sẵn bộ 3 mẫu Modern CV (Công nghệ thông tin / Tech Minimalist, Chuyên gia Kinh doanh / Corporate Navy, Thiết kế sáng tạo / Creative Emerald) vào bảng `template_modern_cv`.
- [x] Dọn dẹp sạch các bản ghi PDF trùng lặp trong cơ sở dữ liệu `ai_cv_pdf`.

### 4. Xác Thực & Người Dùng
- [x] `POST /api/users/login` — Đăng nhập email/password, trả JWT (HMAC-SHA512).
- [x] `POST /api/users/profile/register` — Đăng ký tài khoản mới.
- [x] `POST /api/users/google-login` — Đăng nhập bằng Google ID Token.
- [x] OAuth2 Social Login với GitHub thông qua Spring Security.
- [x] `GET /api/users/profile/me` & `PUT /api/users/profile/me` — Quản lý hồ sơ cá nhân đầy đủ (kỹ năng, kinh nghiệm, học vấn, chứng chỉ, sở thích).
- [x] `POST /api/auth/forgot-password` & `POST /api/auth/reset-password` — Khôi phục mật khẩu qua Gmail SMTP.

---

## 📋 Danh Mục Cần Cải Tiến & Tối Ưu Hóa (Backlog)

### 🔴 Mức Độ Quan Trọng (High Priority)
- [ ] **Bảo vệ Credentials:** Chuyển các secret key (`jwt.secret`, `api.key`, `cloudflare.r2.*`, `spring.mail.password`) từ file `application.properties` sang biến môi trường hệ thống (Environment Variables) hoặc Docker Secrets khi deploy Production.
- [ ] **Xác thực Token Google Chặt Chẽ:** Tích hợp `GoogleIdTokenVerifier` chính thức để verify chữ ký số của Google thay vì chỉ giải mã base64.

### 🟡 Mức Độ Trung Bình (Medium Priority)
- [ ] **Chuẩn hóa DTO Dùng Chung:** Gộp các DTO `PdfRequest`, `ModernCVPdfRequest`, `AICVPdfRequest` thành một DTO duy nhất trong package `dto/`.
- [ ] **Dọn Dẹp Class Nội Tuyến:** Di chuyển các DTO đang khai báo inline trong Controller ra file riêng.
- [ ] **Rate Limiting:** Thêm bộ đếm tần suất gọi API (Bucket4j) cho endpoint `/api/ai/generate-cv` để tránh lạm dụng hạn ngạch Groq API.

### 🟢 Mức Độ Tiện Ích (Low Priority)
- [ ] **Xuất Định Dạng Khác:** Nghiên cứu hỗ trợ xuất thêm file định dạng Word (.docx) hoặc chia sẻ link CV online có mật khẩu.
- [ ] **Admin Analytics:** Mở rộng các API thống kê số lượt xem, số lần tải PDF theo biểu đồ ngày/tuần/tháng cho bảng điều khiển Admin.
