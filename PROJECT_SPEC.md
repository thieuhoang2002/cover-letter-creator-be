# PROJECT_SPEC.md — Cover Letter Creator Backend

## 1. Tổng quan Dự án

**Cover Letter Creator Backend** là hệ thống RESTful API được xây dựng trên **Spring Boot 3.4.3** & **Java 21**, cung cấp toàn bộ logic nghiệp vụ cho nền tảng tạo và quản lý hồ sơ tuyển dụng:
1. Quản lý hồ sơ cá nhân ứng viên (kỹ năng, kinh nghiệm, học vấn, chứng chỉ, sở thích).
2. Quản lý và cung cấp mẫu hồ sơ (Classic Cover Letter và Modern CV) có sẵn trong cơ sở dữ liệu.
3. Tích hợp AI thông minh (**Groq Cloud API** với model `openai/gpt-oss-120b` & `llama-3.3-70b-versatile`) kèm cơ chế xoay vòng nhiều API key (Multi-key comma rotation) và Semaphore concurrency limiter.
4. Trích xuất file PDF chất lượng cao (iText html2pdf 4.0.3), trả về dạng **Binary Stream** (`application/pdf`) cho trình duyệt tải ngay lập tức, đồng thời tự động lưu trữ lên **Cloudflare R2 Storage** (S3-compatible).
5. Tải lên file CV cá nhân dạng PDF từ máy người dùng lên Cloudflare R2 với hạn ngạch (Quota): 3 CV cho tài khoản Thường, **30 CV cho tài khoản VIP**, không giới hạn cho Admin.
6. Cơ chế tự động dọn dẹp file (Auto-cleanup) trên Cloudflare R2: Xóa avatar cũ khi cập nhật avatar mới, xóa file PDF trên R2 khi xóa CV ứng tuyển.
7. Chống trùng lặp tạo PDF (Deduplication Guard) ngăn ngừa double-click từ client.
8. Quản lý lịch sử xuất PDF, theo dõi tiến trình ứng tuyển (Followed CV) và quy trình xét duyệt tài khoản VIP.

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
                                       ├─ Multi-key Round-Robin            Model: openai/gpt-oss-120b
                                       ├─ Semaphore Concurrency (max 3)    Fallback: llama-3.3-70b-versatile
                                       └─ Timeout Queue (45s)
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

### 2.4. Luồng Upload CV Cá Nhân & Kiểm Soát Hạn Mức (FollowCV Quota)
```
1. GET /api/follow-cv/quota ──────► FollowedCVController
                                    ◄── JSON { used: 2, max: 30, remaining: 28, isVip: true }

2. POST /api/follow-cv/upload ────► FollowedCVController
   [MultipartFile, name, company]   ├─ Kiểm tra Quota (User: 3, VIP: 30, Admin: vô hạn)
                                    ├─ CloudflareR2Service.uploadFile → Lưu vào `customer-cvs/{userId}/`
                                    ├─ FollowedCVRepository.save (source: 'uploaded', file_size: ...)
                                    ◄── JSON FollowedCV record

3. DELETE /api/follow-cv/{id} ────► FollowedCVController
                                    ├─ FollowedCVRepository.deleteById
                                    └─ CloudflareR2Service.deleteFile → Xóa triệt để file PDF trên R2
```

### 2.5. Luồng Cập Nhật Avatar & Dọn Dẹp File R2
```
1. POST /api/users/profile/avatar ► UserController
   [MultipartFile]                  ├─ Kiểm tra avatarUrl hiện tại: nếu thuộc Cloudflare R2 (/avatars/)
                                    │   └─ CloudflareR2Service.deleteFile (Xóa avatar cũ)
                                    ├─ CloudflareR2Service.uploadAvatar → Lưu `avatars/{uuid}.jpg`
                                    └─ UserRepository.save(avatarUrl)
                                    ◄── JSON { avatarUrl: "https://...r2.dev/avatars/..." }
```

### 2.6. Luồng Yêu Cầu & Quản Trị Phê Duyệt VIP
```
1. User gửi yêu cầu:
   POST /api/follow-cv/vip-request  ► FollowedCVController
   { plan: "pro", note: "..." }     └─ Lưu bản ghi vào bảng `vip_upgrade_requests` (status: 'pending')

2. Admin phê duyệt:
   GET /api/admin/vip-requests      ► VipUpgradeRequestController → Danh sách yêu cầu chờ duyệt
   PUT /api/admin/vip-requests/{id}/approve ► Cập nhật status='approved' + cập nhật User.role='vip'
```

---

## 3. Kiến Trúc AI Engine (GroqAIService)

- **Nhà cung cấp:** Groq Cloud Platform (`https://api.groq.com/openai/v1/chat/completions`)
- **Cơ chế nạp Key linh hoạt:** `@Value("${api.key:}")` hỗ trợ chuỗi key phân cách bằng dấu phẩy. Khởi động an toàn ngay cả khi chưa có key.
- **Xoay vòng Round-Robin & Tự động Retry:** Phân bổ đều tải trên tất cả các key; khi 1 key chạm Rate Limit (HTTP 429), tự động chuyển sang key tiếp theo.
- **Model chính (Primary):** `openai/gpt-oss-120b` (Mô hình mã nguồn mở thế hệ mới với khả năng suy luận mạnh mẽ, định dạng HTML hoàn hảo).
- **Model dự phòng (Fallback):** `llama-3.3-70b-versatile` (Tự động kích hoạt khi model chính gặp sự cố).
- **Điều phối hàng đợi:** Semaphore giới hạn tối đa 3 tác vụ AI đồng thời, timeout hàng đợi 45 giây.

---

## 4. Xuất File PDF & Lưu Trữ Đám Mây

- **Thư viện PDF:** `com.itextpdf:html2pdf:4.0.3`.
- **Phông chữ tích hợp:** Times New Roman Unicode nạp từ thư mục `src/main/resources/fonts/`.
- **Lưu trữ Cloudflare R2:**
  - Bucket: `cover-letter-cv-storage`
  - Giao thức: AWS S3 Client SDK (`AmazonS3ClientBuilder` với Custom Endpoint `https://<account-id>.r2.cloudflarestorage.com`).
  - Phân vùng thư mục ảo:
    + `avatars/` - Ảnh đại diện người dùng.
    + `customer-cvs/{userId}/` - File PDF do người dùng tự tải lên.
    + Root bucket - File PDF xuất tự động từ hệ thống.
  - **Auto-cleanup:** Hàm `CloudflareR2Service.deleteFile` giữ nguyên đường dẫn key tương đối chuẩn xác trong bucket, đảm bảo xóa sạch 100%.

---

## 5. Sơ Đồ Cơ Sở Dữ Liệu MySQL / TiDB Cloud (`cover_letter_creator_db`)

| Bảng | Chức năng | Cột lưu trữ URL / Dữ liệu nổi bật |
|---|---|---|
| `users` | Tài khoản, phân quyền (`user` / `vip` / `admin`) | `avatar_url`, `role`, `specialization` |
| `skills` / `experiences` / `educations` / `certificates` / `hobbies` | Dữ liệu chi tiết hồ sơ ứng viên | Liên kết khóa ngoại `user_id` |
| `templates` | Danh mục mẫu Cover Letter Classic (6 mẫu) | `status` ('active' / 'hidden') |
| `modern_cv_templates` | Danh mục mẫu Modern CV (5 mẫu) | `image`, `status` |
| `cover_letters_pdf` | Lịch sử xuất Cover Letter | `url_google_drive` (Lưu Cloudflare R2 URL) |
| `modern_cv_pdf` | Lịch sử xuất Modern CV | `url_google_drive` (Lưu Cloudflare R2 URL) |
| `ai_cv_pdf` | Lịch sử xuất AI-Generated CV | `url_google_drive` (Lưu Cloudflare R2 URL) |
| `user_loved_templates` | Mối quan hệ n-n: Mẫu Classic yêu thích | `user_id`, `template_id` |
| `user_loved_modern_templates` | Mối quan hệ n-n: Mẫu Modern CV yêu thích | `user_id`, `modern_template_id` |
| `followed_cvs` | Quản lý tiến độ tuyển dụng & upload CV PDF | `source` ('system' / 'uploaded'), `file_size`, `url_google_drive` |
| `vip_upgrade_requests` | Danh sách yêu cầu nâng cấp gói VIP | `user_id`, `user_email`, `plan`, `status`, `note`, `admin_note` |
| `password_reset_token` | Mã xác thực reset mật khẩu qua email | `token`, `expiry_date`, `used`, `user_id` |
