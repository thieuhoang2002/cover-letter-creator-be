# PROJECT_SPEC.md — Cover Letter Creator Backend

## 1. Tổng quan Dự án

**Cover Letter Creator Backend** là hệ thống RESTful API được xây dựng trên **Spring Boot 3.4.3** & **Java 21**, cung cấp toàn bộ logic nghiệp vụ cho nền tảng tạo và quản lý hồ sơ tuyển dụng:
1. Quản lý hồ sơ cá nhân ứng viên (kỹ năng, kinh nghiệm, học vấn, chứng chỉ, sở thích).
2. Quản lý và cung cấp mẫu hồ sơ (Classic Cover Letter và Modern CV) có sẵn trong cơ sở dữ liệu.
3. Tích hợp AI thông minh (**Groq Cloud API** với model `openai/gpt-oss-120b` & `llama-3.3-70b-versatile`) tự động phân tích hồ sơ và sinh mã HTML CV hoàn chỉnh.
4. Trích xuất file PDF chất lượng cao (iText html2pdf 4.0.3), trả về dạng **Binary Stream** (`application/pdf`) cho trình duyệt tải ngay lập tức, đồng thời tự động lưu trữ lên **Cloudflare R2 Storage** (S3-compatible).
5. Chống trùng lặp tạo PDF (Deduplication Guard) ngăn ngừa double-click từ client.
6. Quản lý lịch sử xuất PDF và theo dõi trạng thái ứng tuyển tuyển dụng (Followed CV).

---

## 2. Luồng API Chính

### 2.1. Luồng Tạo CV Bằng AI (AI-Generated CV)

```
Frontend (React 18)                 Backend (Spring Boot 3.4.3)            External Services
─────────────────────────────────────────────────────────────────────────────────────────────
1. POST /api/users/login ─────────► LoginController ─────────────────────► JWT Token
   {email, password}

2. POST /api/ai/generate-cv ──────► HtmlGenerationController
   {position, theme, userData}      └─ GroqAIService ────────────────────► Groq Cloud API
                                                                           Model: openai/gpt-oss-120b
                                                                           Fallback: llama-3.3-70b-versatile
                                    ◄── HTML string (A4 formatted) ───────┘

3. (Frontend render TinyMCE, user chỉnh sửa & preview)

4. POST /api/ai-cv/pdf/generate ──► AICVPdfController
   {htmlContent, id, email,         ├─ AICVPdfService (Deduplication check trong 10s)
    templateName, date}             ├─ PdfService (iText html2pdf)
                                    │   └─ HTML → PDF binary bytes
                                    ├─ CloudflareR2Service
                                    │   └─ Upload lên Cloudflare R2 bucket `cover-letter-cv-storage`
                                    ├─ AICVPdfRepository
                                    │   └─ Lưu record vào MySQL (`ai_cv_pdf` table: url_google_drive / r2_file_url)
                                    ◄── Binary Stream [application/pdf] (Kèm header Content-Disposition)
```

### 2.2. Luồng Tạo Cover Letter từ Template Classic

```
1. GET /api/templates/all ────────► TemplateController → Lấy danh sách template Classic đang active

2. GET /api/templates/{id} ───────► TemplateController → Lấy chi tiết mẫu + tăng view count

3. POST /api/pdf/generate ────────► PdfController
   {htmlContent, id, email,         ├─ PdfService (iText html2pdf → PDF bytes)
    templateName, date}             ├─ CloudflareR2Service → Lưu trữ Cloudflare R2
                                    ├─ CoverLetterPdfService → Lưu bảng `cover_letters_pdf`
                                    ◄── Binary Stream [application/pdf] tải trực tiếp về máy
```

### 2.3. Luồng Tạo CV từ Template Modern

```
1. GET /api/templates-modern/all ─► TemplateModernCVController → Lấy tất cả Modern CV template active

2. GET /api/templates-modern/{id} ► TemplateModernCVController → Lấy chi tiết template theo ID

3. POST /api/modern-cv/pdf/generate ► ModernCVPdfController
   {htmlContent, id, email,           ├─ PdfService (iText html2pdf → PDF bytes)
    templateName, date}               ├─ CloudflareR2Service → Lưu trữ Cloudflare R2
                                      ├─ ModernCVPdfService → Lưu bảng `modern_cv_pdf`
                                      ◄── Binary Stream [application/pdf] tải trực tiếp về máy
```

---

## 3. Chi Tiết Request / Response Các API Trọng Tâm

### `POST /api/ai/generate-cv`
- **Request Body (JSON):**
```json
{
  "position": "Senior Fullstack Developer",
  "theme": "navy",
  "userData": {
    "name": "Nguyễn Văn A",
    "email": "nguyenvana@gmail.com",
    "phone": "0901234567",
    "address": "Hà Nội, Việt Nam",
    "specialization": "Java & React",
    "skills": [{"name": "Spring Boot"}, {"name": "ReactJS"}, {"name": "Docker"}],
    "experiences": [{"company": "FPT Software", "role": "Senior Dev", "time": "2021-2024", "description": "Xây dựng hệ thống microservices..."}],
    "educations": [{"school": "Đại học Bách Khoa Hà Nội", "degree": "Kỹ sư", "fieldOfStudy": "CNTT", "time": "2016-2021"}],
    "certificates": [{"name": "AWS Certified Solutions Architect", "organization": "Amazon Web Services", "time": "2023"}],
    "hobbies": [{"name": "Đọc sách công nghệ"}, {"name": "Chạy bộ marathon"}]
  }
}
```
- **Response Success (200 OK):**
```json
{
  "status": "success",
  "content": "<div style=\"font-family: 'Times New Roman'; width: 210mm; ...\">...Nội dung HTML chuẩn A4...</div>"
}
```

### `POST /api/pdf/generate` / `POST /api/modern-cv/pdf/generate` / `POST /api/ai-cv/pdf/generate`
- **Request Body (JSON):**
```json
{
  "htmlContent": "<div style=\"...\">...HTML cần xuất PDF...</div>",
  "id": 1,
  "email": "user@example.com",
  "templateName": "AI-Generated CV for Senior Fullstack Developer",
  "date": "21/09/2026"
}
```
- **Response Success (200 OK):**
  - **Content-Type:** `application/pdf`
  - **Content-Disposition:** `attachment; filename="AI-Generated_CV_for_Senior_Fullstack_Developer_21-09-2026.pdf"`
  - **Body:** Binary Stream (mảng byte trực tiếp của file PDF)
- **Response Error (4xx/5xx):**
```json
{
  "status": "error",
  "message": "Chi tiết lý do lỗi..."
}
```

---

## 4. Kiến Trúc AI Engine (GroqAIService)

- **Nhà cung cấp:** Groq Cloud Platform (`https://api.groq.com/openai/v1/chat/completions`)
- **Model chính (Primary):** `openai/gpt-oss-120b` (Mô hình mã nguồn mở thế hệ mới với khả năng suy luận mạnh mẽ, định dạng HTML hoàn hảo).
- **Model dự phòng (Fallback):** `llama-3.3-70b-versatile` (Tự động kích hoạt khi model chính trả mã lỗi 400/429/500/deprecate).
- **Yêu cầu sinh định dạng:**
  - Định dạng HTML bọc trong `<div>...</div>`.
  - Toàn bộ CSS sử dụng Inline Styling tương thích iText 4.0.3 (hạn chế các thuộc tính Flexbox nâng cao mà XMLWorker/iText không hỗ trợ như `gap`, `space-between`).
  - Cân đối độ dài nội dung để hiển thị vừa vặn 1 trang chuẩn A4.

---

## 5. Xuất File PDF & Lưu Trữ Đám Mây

- **Thư viện PDF:** `com.itextpdf:html2pdf:4.0.3`.
- **Phông chữ tích hợp:** Times New Roman Unicode (`Times_New_Roman.ttf`, `Times_New_Roman_Bold.ttf`, `Times_New_Roman_Italic.ttf`, `Times_New_Roman_Bold_Italic.ttf`) nạp từ thư mục `src/main/resources/fonts/`.
- **Lưu trữ Cloudflare R2:**
  - Bucket: `cover-letter-cv-storage`
  - Giao thức: AWS S3 Client SDK (`AmazonS3ClientBuilder` với Custom Endpoint `https://<account-id>.r2.cloudflarestorage.com`).
  - URL Public: `https://pub-f5c93cea64c64c428eb01ca4a52f506d.r2.dev/<file-name>`
- **Deduplication Guard:** `AICVPdfService` kiểm tra xem trong vòng 10 giây trước đó user có vừa xuất CV cùng tên hay không. Nếu có, backend tái sử dụng bản ghi cũ thay vì insert mới.

---

## 6. Sơ Đồ Cơ Sở Dữ Liệu MySQL (`cover_letter_creator_db`)

| Bảng | Chức năng | Cột lưu trữ URL file |
|---|---|---|
| `users` | Tài khoản, phân quyền (user / admin) | `avatar` |
| `skills` / `experiences` / `educations` / `certificates` / `hobbies` | Dữ liệu chi tiết hồ sơ ứng viên | - |
| `templates` | Danh mục mẫu Cover Letter Classic (đã seed 3 mẫu) | - |
| `template_modern_cv` | Danh mục mẫu Modern CV (đã seed 3 mẫu) | `image` |
| `cover_letters_pdf` | Lịch sử xuất Cover Letter | `url_google_drive` (Lưu Cloudflare R2 URL) |
| `modern_cv_pdf` | Lịch sử xuất Modern CV | `url_google_drive` (Lưu Cloudflare R2 URL) |
| `ai_cv_pdf` | Lịch sử xuất AI-Generated CV | `url_google_drive` (Lưu Cloudflare R2 URL) |
| `user_loved_templates` | Mối quan hệ nhiều-nhiều: Mẫu Classic được yêu thích | - |
| `user_loved_modern_templates` | Mối quan hệ nhiều-nhiều: Mẫu Modern CV được yêu thích | - |
| `followed_cv` | Quản lý tiến độ tuyển dụng (Công ty, vị trí, trạng thái nộp) | - |
| `password_reset_tokens` | Mã xác thực reset mật khẩu qua email | - |

---

## 7. Cơ Chế Xác Thực (Authentication Flow)

- **Email & Mật khẩu:** `POST /api/users/login` → Trả JWT Token.
- **Google Social Login:** `POST /api/users/google-login` tiếp nhận Google ID Token từ client.
- **GitHub Social Login:** OAuth2 Client chuẩn của Spring Security, callback tại `/login/oauth2/code/github`, xử lý qua `CustomOAuth2SuccessHandler` và redirect về frontend kèm token.
- **JWT Header:** `Authorization: Bearer <token>` (HMAC-SHA512, thời hạn 10 giờ).
