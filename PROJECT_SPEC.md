# PROJECT_SPEC.md — Cover Letter Creator Backend

## Tổng quan

**Cover Letter Creator** là một nền tảng web giúp người dùng:
1. Quản lý hồ sơ cá nhân (kỹ năng, kinh nghiệm, học vấn, chứng chỉ, sở thích)
2. Lựa chọn template CV (Classic hoặc Modern)
3. Sử dụng AI (DeepSeek) để tự động sinh nội dung CV dạng HTML
4. Xuất CV thành file PDF, lưu lên Google Drive
5. Quản lý lịch sử CV đã tạo

---

## Luồng API chính

### 1. Luồng tạo CV bằng AI (AI-Generated CV)

```
Frontend                         Backend                         External
─────────────────────────────────────────────────────────────────────────
1. POST /api/users/login ──────► LoginController ──────────────► JWT Token
   {email, password}

2. POST /api/ai/generate-cv ───► HtmlGenerationController
   {position, theme, userData}   └─ DeepSeekAIService ─────────► DeepSeek API
                                                                   (deepseek-chat)
                                  ◄── HTML string ─────────────────────────

3. (Frontend render HTML, user preview)

4. POST /api/ai-cv/pdf/generate ► AICVPdfController
   {htmlContent, id, email,       ├─ PdfService (iText html2pdf)
    templateName, date}           │   └─ HTML → PDF bytes
                                  ├─ GoogleDriveService ────────► Google Drive API
                                  │   └─ Upload PDF file
                                  └─ AICVPdfService
                                      └─ Lưu record vào DB (ai_cv_pdf table)
                                  ◄── fileId (Google Drive ID)
```

### 2. Luồng tạo CV từ Template Classic

```
1. GET /api/templates/all ─────► TemplateController → Lấy danh sách template đang active

2. GET /api/templates/{id} ────► TemplateController → Lấy template + tăng view count

3. POST /api/pdf/generate ─────► PdfController
   {htmlContent, id, email,       ├─ PdfService (iText html2pdf)
    templateName, date}           ├─ GoogleDriveService ────────► Google Drive API
                                  └─ CoverLetterPdfService
                                      └─ Lưu record vào DB (cover_letters_pdf table)
```

### 3. Luồng tạo CV từ Template Modern

```
1. GET /api/templates-modern/all ► TemplateModernCVController

2. POST /api/modern-cv/pdf/generate ► ModernCVPdfController
   (tương tự Classic, lưu vào bảng modern_cv_pdf)
```

---

## Chi tiết Input/Output

### `POST /api/ai/generate-cv`
**Input (request body):**
```json
{
  "position": "Backend Developer",
  "theme": "blue",
  "userData": {
    "name": "Nguyễn Văn A",
    "email": "a@example.com",
    "phone": "0123456789",
    "address": "Hà Nội",
    "specialization": "Java Backend",
    "skills": [{"name": "Spring Boot"}, {"name": "MySQL"}],
    "experiences": [{"company": "ABC Corp", "role": "Dev", "time": "2022-2024", "description": "..."}],
    "educations": [{"school": "ĐH Bách Khoa", "degree": "Cử nhân", "fieldOfStudy": "CNTT", "time": "2018-2022"}],
    "certificates": [],
    "hobbies": []
  }
}
```
**Output:** `{ "status": "success", "content": "<div>...HTML CV...</div>" }`

### `POST /api/pdf/generate` / `POST /api/modern-cv/pdf/generate` / `POST /api/ai-cv/pdf/generate`
**Input:**
```json
{
  "htmlContent": "<div>...HTML đã render...</div>",
  "id": "1",
  "email": "user@example.com",
  "templateName": "Template Classic 01",
  "date": "21/09/2026"
}
```
**Output:** `"File upload thành công lên Google Drive. File ID: <driveFileId>"`

---

## Prompt Logic (DeepSeekAIService)

- **Model chính:** `deepseek-chat`
- **Fallback model:** `deepseek-reasoner` (khi model chính hết quota hoặc trả về lỗi 402)
- **Retry:** 3 lần nếu gặp lỗi 401 Unauthorized
- **Prompt yêu cầu AI:**
  - Tạo CV HTML bọc trong `<div>...</div>`
  - CSS inline (không dùng `space-between`, `flex-wrap`, `column-gap`, `row-gap`)
  - Nội dung vừa 1 trang A4 PDF
  - Không giải thích thêm, chỉ trả về HTML thuần
- **Input:** position (vị trí ứng tuyển), theme (chủ đề màu), userData (JSON thông tin người dùng)

---

## Xuất file PDF

- **Thư viện:** `com.itextpdf:html2pdf:4.0.3`
- **Font:** Times New Roman (4 biến thể: Regular, Bold, Italic, Bold Italic) được load từ `/resources/fonts/`
- **Flow:** HTML string → `HtmlConverter.convertToPdf()` → `byte[]` → `MultipartFile` → Google Drive

> **Lưu ý:** Hiện tại **không** có tính năng xuất Docx. Chỉ hỗ trợ PDF.

---

## Database Schema (chính)

| Bảng | Mô tả |
|---|---|
| `users` | Thông tin người dùng |
| `skills` / `experiences` / `educations` / `certificates` / `hobbies` | Thông tin chi tiết hồ sơ |
| `templates` | Template CV Classic |
| `template_modern_cv` | Template CV Modern |
| `cover_letters_pdf` | Lịch sử PDF từ template Classic |
| `modern_cv_pdf` | Lịch sử PDF từ template Modern |
| `ai_cv_pdf` | Lịch sử PDF từ AI-generated CV |
| `user_loved_templates` | Bảng nhiều-nhiều: User yêu thích Template |
| `user_loved_modern_templates` | Bảng nhiều-nhiều: User yêu thích Modern Template |
| `followed_cv` | CV theo dõi của user |
| `password_reset_tokens` | Token reset mật khẩu |

---

## Auth Flow

- **Email/Password:** `POST /api/users/login` → JWT
- **Google OAuth2 (Frontend token):** `POST /api/users/google-login` → parse `GoogleIdToken` → JWT
- **GitHub OAuth2 (Spring Security flow):** Redirect sang `/login/oauth2/code/github` → `SecurityConfig.successHandler` → Redirect về FE với JWT
- **JWT:** HS512, secret từ `jwt.secret`, hết hạn sau 10 giờ
