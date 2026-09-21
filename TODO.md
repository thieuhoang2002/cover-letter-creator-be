# TODO.md — Cover Letter Creator Backend

## ✅ APIs Đã Hoàn Thành

### Auth & User Management (`/api/users`)
- [x] `POST /api/users/login` — Đăng nhập email/password, trả JWT
- [x] `POST /api/users/profile/register` — Đăng ký tài khoản mới
- [x] `POST /api/users/google-login` — Đăng nhập bằng Google ID Token
- [x] `POST /api/users/github-login` — *(Không có controller riêng, đây là OAuth2 flow của Spring Security, redirect về `/login/oauth2/code/github`)*
- [x] `GET /api/users/profile/me` — Lấy thông tin user hiện tại từ JWT
- [x] `PUT /api/users/profile/me` — Cập nhật profile đầy đủ (kể cả skills, experiences, educations, certificates, hobbies)
- [x] `GET /api/users/profile/{id}` — Lấy profile theo user ID
- [x] `PUT /api/users/profile/{id}` — Cập nhật user theo ID
- [x] `DELETE /api/users/profile/{id}` — Xóa user theo ID
- [x] `POST /api/users/profile/me/love-template/{templateId}` — Toggle yêu thích template Classic
- [x] `POST /api/users/profile/me/love-modern-template/{templateId}` — Toggle yêu thích template Modern
- [x] `POST /api/users/profile/change-password` — Đổi mật khẩu (cần nhập mật khẩu cũ)
- [x] `POST /api/users/profile/change-password-without-old` — Đổi mật khẩu (không cần mật khẩu cũ — dùng sau khi reset)

### Password Reset (`/api/auth`)
- [x] `POST /api/auth/forgot-password` — Gửi email reset mật khẩu
- [x] `POST /api/auth/reset-password` — Reset mật khẩu bằng token

### Templates Classic (`/api/templates`)
- [x] `GET /api/templates` — Lấy TẤT CẢ template (không filter status)
- [x] `GET /api/templates/all` — Lấy template đang active (dành cho public)
- [x] `GET /api/templates/{id}` — Lấy template + tăng view count
- [x] `POST /api/templates` — Tạo template mới
- [x] `PUT /api/templates/{id}` — Cập nhật template (chỉ sửa field, không overwrite toàn bộ)
- [x] `DELETE /api/templates/{id}` — Xóa template
- [x] `GET /api/templates/top-viewed` — Lấy template nhiều view nhất

### Templates Modern (`/api/templates-modern`)
- [x] `GET /api/templates-modern/all` — Lấy tất cả template modern đang active
- [x] `GET /api/templates-modern/{id}` — Lấy modern template theo ID
- [x] `POST /api/templates-modern` — Tạo modern template mới
- [x] `PUT /api/templates-modern/{id}` — Cập nhật modern template
- [x] `DELETE /api/templates-modern/{id}` — Xóa modern template

### PDF Generation & Google Drive

#### Classic CV PDFs (`/api/pdf`)
- [x] `POST /api/pdf/generate` — Sinh PDF từ HTML, upload Google Drive, lưu DB
- [x] `GET /api/pdf/list/{userId}` — Lấy danh sách Cover Letter PDF của user
- [x] `DELETE /api/pdf/delete/{id}` — Xóa Cover Letter PDF (cả Drive lẫn DB)

#### Modern CV PDFs (`/api/modern-cv/pdf`)
- [x] `POST /api/modern-cv/pdf/generate` — Sinh PDF Modern, upload Drive, lưu DB
- [x] `GET /api/modern-cv/pdf/list/{userId}` — Lấy danh sách Modern CV PDF của user
- [x] `DELETE /api/modern-cv/pdf/delete/{id}` — Xóa Modern CV PDF

#### AI-Generated CV PDFs (`/api/ai-cv/pdf`)
- [x] `POST /api/ai-cv/pdf/generate` — Sinh PDF từ AI HTML, upload Drive, lưu DB
- [x] `GET /api/ai-cv/pdf/list/{userId}` — Lấy danh sách AI CV PDF của user
- [x] `DELETE /api/ai-cv/pdf/delete/{id}` — Xóa AI CV PDF

### AI CV Generation (`/api/ai`)
- [x] `POST /api/ai/generate-cv` — Gọi DeepSeek API sinh HTML CV từ thông tin user
- [x] `GET /api/ai/health` — Health check của AI service

### Followed CV (`/api/follow-cv`)
- [x] `POST /api/follow-cv` — Thêm CV theo dõi
- [x] `GET /api/follow-cv/me` — Lấy danh sách CV đang theo dõi của user
- [x] `PUT /api/follow-cv/{id}` — Cập nhật CV theo dõi
- [x] `DELETE /api/follow-cv/{id}` — Xóa CV theo dõi

### Google Drive (`/api/drive`)
- [x] `POST /api/drive/upload` — Upload file trực tiếp lên Google Drive *(GoogleDriveController)*

---

## ⚠️ Vấn đề Kỹ thuật Cần Xử lý

### 🔴 High Priority

- [ ] **Credentials bị hardcode trong application.properties** — JWT secret, DB password, Google/GitHub client secret, DeepSeek API key, Gmail App Password đang để plain text trong file. Cần chuyển sang environment variables hoặc secrets manager trước khi deploy production.
  - File: [`application.properties`](file:///c:/Users/thhoang/Desktop/coverlettercreator/cover-letter-creator-be/src/main/resources/application.properties)

- [ ] **Google Drive Service Account key bị hardcode path** — `/calendar-438415-5bdb470fb244.json` trong [`GoogleDriveConfig.java`](file:///c:/Users/thhoang/Desktop/coverlettercreator/cover-letter-creator-be/src/main/java/cover/letter/creator/config/GoogleDriveConfig.java#L27). Cần cấu hình từ environment variable.

- [ ] **GoogleLoginController không verify chữ ký của Google ID Token** — Chỉ `parse()` token mà không `verify()` signature. Đây là lỗ hổng bảo mật nghiêm trọng. Cần dùng `GoogleIdTokenVerifier` để xác thực.

### 🟡 Medium Priority

- [ ] **Warning: Raw type `Map` trong `DeepSeekAIService`** — `ResponseEntity<Map>` nên đổi thành `ResponseEntity<Map<String, Object>>`. File: [`DeepSeekAIService.java`](file:///c:/Users/thhoang/Desktop/coverlettercreator/cover-letter-creator-be/src/main/java/cover/letter/creator/service/DeepSeekAIService.java#L55)

- [ ] **Trùng lặp code `CustomMultipartFile`** — Ba controller (`PdfController`, `ModernCVPdfController`, `AICVPdfController`) đều có class `CustomMultipartFileXxx` giống hệt nhau. Nên gộp thành 1 class dùng chung trong package `util/`.

- [ ] **Trùng lặp DTO request** — `PdfRequest`, `ModernCVPdfRequest`, `AICVPdfRequest` có cùng cấu trúc. Nên tạo 1 `GeneratePdfRequest` dùng chung.

- [ ] **Class DTO nội tuyến trong file Controller** — `LoginRequest`, `GoogleLoginRequest`, `PdfRequest`, v.v. đang khai báo trong cùng file với Controller. Nên chuyển sang package `dto/`.

- [ ] **`spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect`** — Class này đã bị deprecated từ Hibernate 6. Với Spring Boot 3.x + Hibernate 6, nên xóa dòng này (Hibernate tự detect) hoặc dùng `org.hibernate.dialect.MySQLDialect`.

### 🟢 Low Priority / Nice-to-have

- [ ] **Endpoint `GET /api/templates` (không filter)** — Không có auth nhưng trả về cả template đã ẩn. Nên thêm `@PreAuthorize("hasRole('ADMIN')")` hoặc bảo vệ qua SecurityConfig.

- [ ] **Chưa có PATCH endpoint** — Các `PUT` endpoint đang thực hiện partial update (kiểm tra null), nên dùng `PATCH` theo REST convention.

- [ ] **Chưa có Pagination** — `GET /api/templates/all`, `GET /api/pdf/list/{userId}` trả về toàn bộ list, cần thêm pagination khi dữ liệu lớn.

- [ ] **Chưa có Cover Letter (text) endpoint** — Tên project là "Cover Letter Creator" nhưng hiện tại chỉ có CV (Curriculum Vitae). Nếu cần tính năng tạo Cover Letter từ mô tả job và CV, cần thêm endpoint riêng.

- [ ] **Thiếu unit tests** — Chỉ có dependency test nhưng chưa có test nào được viết.

---

## 📋 Endpoint Bị Comment / Không Dùng

| Endpoint | File | Ghi chú |
|---|---|---|
| `GET /api/users/profile/me` (version cũ trả `User`) | `UserController.java` | Đã replaced bằng version trả `UserProfileDTO` |
| `PUT /api/templates/{id}` (version cũ overwrite toàn bộ) | `TemplateController.java` | Đã replaced bằng version partial update |
| `PUT /api/users/profile/{id}` (version cũ) | `UserService.java` | Đã replaced |
| Social login controller riêng | `SocialLoginController.java` | Cần kiểm tra nội dung — có thể redundant với `GoogleLoginController` |
