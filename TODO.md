# 📋 PROJECT ROADMAP & AUDIT CHECKLIST — BACKEND (TODO.md)

Theo dõi chi tiết lộ trình phát triển, các tính năng đã hoàn thiện và danh sách nhiệm vụ kỹ thuật nâng cấp toàn diện dự án **Cover Letter Creator Backend** trên nhánh `dev`.

---

## ✅ 1. Trạng Thái Hiện Tại (Production / Done)

### 1.1. Hạ Tầng & Triển Khai
- [x] **Triển khai Render Live Web Service**: `https://cover-letter-creator-be-eadm.onrender.com`.
- [x] **Docker Alpine Production**: Multi-stage build Maven 3.9 + JRE 21 Alpine, tích hợp font DejaVu & fontconfig hỗ trợ đầy đủ tiếng Việt.
- [x] **Tối ưu RAM Container**: Cấu hình `-XX:+UseContainerSupport -Xmx384m` hoạt động ổn định trên gói Render Free 512MB RAM.
- [x] **Maven Mirror Tốc Độ Cao**: Bổ sung `settings.xml` trỏ Google Cloud Maven Central Mirror, triệt tiêu lỗi `429 Too Many Requests`.
- [x] **Cơ sở dữ liệu TiDB Cloud Serverless**: Chuyển đổi thành công sang MySQL 8.0 wire-compatible trên AWS ap-southeast-1 kèm bắt buộc SSL/TLS.
- [x] **Bộ Dữ Liệu Mẫu Sẵn Có**: 11 Templates (6 Cover Letter + 5 Modern CV) gồm cả phân hệ Chuẩn Cơ Quan Nhà Nước và Doanh Nghiệp Hiện Đại.

### 1.2. Bảo Mật & Xác Thực
- [x] **JWT Token Siêu Bền Vững (Resilient Signing Key)**: Tự động hỗ trợ Base64, Base64URL và fallback SHA-512 cryptographic digest, triệt tiêu hoàn toàn lỗi `Illegal base64 character: '_'`.
- [x] **CORS Mọi Nguồn Gốc**: Cấu hình `setAllowedOriginPatterns("*")` hỗ trợ linh hoạt toàn bộ domain Vercel và các port phát triển cục bộ.
- [x] **Social Login**: Tích hợp Google OAuth2 và GitHub OAuth2 callback flow.
- [x] **Public Health Check**: Endpoint `GET /api/ai/health` phục vụ giám sát keep-alive định kỳ.

### 1.3. AI & Lưu Trữ PDF
- [x] **Groq Cloud API**: Model chính `openai/gpt-oss-120b` kết hợp tự động fallback sang `llama-3.3-70b-versatile`.
- [x] **Lưu Trữ Đám Mây Cloudflare R2**: Bucket S3-compatible `cover-letter-cv-storage` kết nối qua AWS SDK S3.
- [x] **Xuất PDF Binary Stream**: Trả file trực tiếp qua luồng byte `application/pdf` kèm header `Content-Disposition`.
- [x] **Deduplication Guard**: Chống spam click tạo trùng lặp bản ghi PDF trong vòng 10 giây.

---

## 🚀 2. Lộ Trình Nâng Cấp Hệ Thống Chuẩn Senior Backend (Nhánh `dev`)

### ⚡ Phase 1: Tối Ưu Bộ Nhớ Đệm & Chống Spam API (Performance & Rate Limiting)
- [ ] **Caffeine In-Memory Cache Cho Danh Mục Template**:
  - [ ] Tích hợp Spring Cache với Caffeine Cache cho API `GET /api/templates/all` và `GET /api/templates-modern/all` (`@Cacheable("templates")`).
  - [ ] Giảm tải 95% số lượng truy vấn lặp lại đến TiDB Cloud, thời gian phản hồi danh mục mẫu đạt dưới 10ms.
  - [ ] Cơ chế tự động xóa cache (`@CacheEvict`) khi Admin tạo mới hoặc chỉnh sửa mẫu.
- [ ] **In-Memory Rate Limiting (Bucket4j)**:
  - [ ] Giới hạn tần suất gọi AI tạo CV theo User ID / IP (Ví dụ: tối đa 5 yêu cầu / phút) để bảo vệ hạn ngạch Groq API.
  - [ ] Trả về mã lỗi HTTP 429 kèm thông báo thân thiện: "Bạn thao tác quá nhanh, vui lòng thử lại sau ít giây".

---

### 🤖 Phase 2: Siêu Năng Lực AI & Streaming Thời Gian Thực (Groq AI Superpowers)
- [ ] **AI Streaming Response (Server-Sent Events / SSE)**:
  - [ ] Xây dựng endpoint `GET /api/ai/stream-cv`: Sử dụng `SseEmitter` kết nối với Groq Streaming API (`stream=true`) để đẩy trực tiếp từng token văn bản về Frontend.
- [ ] **AI ATS Resume Reviewer (Chấm Điểm CV & Phân Tích Độ Tương Thích)**:
  - [ ] Endpoint `POST /api/ai/review-cv`: Nhận nội dung CV + Chuỗi mô tả công việc (Job Description / JD).
  - [ ] Prompt kỹ thuật phân tích và trả về JSON chuẩn gồm: Điểm tương thích (`ats_score` từ 0 - 100), Điểm mạnh (`strengths`), Điểm cần cải thiện (`weaknesses`), và Từ khóa quan trọng còn thiếu (`missing_keywords`).
- [ ] **AI Text Rewriter & Tone Changer**:
  - [ ] Endpoint `POST /api/ai/rewrite-sentence`: Hỗ trợ tinh chỉnh câu chữ theo 3 văn phong: `professional` (chuyên nghiệp), `concise` (ngắn gọn, súc tích), `persuasive` (thuyết phục, giàu hành động).

---

### 💾 Phase 3: Quản Lý Trạng Thái Bản Soạn Thảo & Nhân Bản (Document State & Duplication)
- [ ] **API Tự Động Lưu Bản Nháp (Auto-save Snapshot)**:
  - [ ] Bảng `cv_drafts` (user_id, template_id, content_json, updated_at).
  - [ ] Endpoint `POST /api/cv/auto-save`: Lưu nhanh dữ liệu đang soạn thảo của người dùng.
  - [ ] Endpoint `GET /api/cv/draft/{templateId}`: Phục hồi lại dữ liệu dở dang khi người dùng quay lại.
- [ ] **API Nhân Bản Tài Liệu (Duplicate Document)**:
  - [ ] Endpoint `POST /api/cv/duplicate/{id}`: Sao chép nhanh một CV/Cover Letter hiện có sang bản ghi mới, giúp người dùng tùy biến nộp nhiều công ty.

---

### 🌐 Phase 4: Hệ Thống Chia Sẻ CV Trực Tuyến Công Khai (Public Portfolio Sharing)
- [ ] **Quản Lý Link Chia Sẻ Công Khai**:
  - [ ] Tạo bảng `cv_shares` (id, cv_id, user_id, share_token UUID, is_active, view_count, created_at).
  - [ ] Endpoint `POST /api/cv/share/{cvId}`: Kích hoạt hoặc hủy chế độ chia sẻ công khai, sinh token duy nhất.
- [ ] **Public View Endpoint (Bỏ Qua Xác Thực JWT)**:
  - [ ] Endpoint `GET /api/public/cv/{shareToken}`: Cấu hình `permitAll()` trong `SecurityConfig.java` để bất kỳ ai có link (nhà tuyển dụng) đều xem được CV mà không cần đăng nhập.
  - [ ] Tự động tăng biến đếm `view_count` phục vụ thống kê lượt xem hồ sơ.

---

### 🛡️ Phase 5: Thống Kê Nâng Cao Cho Admin & Chuẩn Hóa Mã Nguồn
- [ ] **Admin Analytics Service**:
  - [ ] Thống kê số lượng CV đã tạo, số lần tải PDF, tỷ lệ tạo bằng AI vs Template theo ngày/tuần/tháng.
- [ ] **Chuẩn Hóa DTO & Validation**:
  - [ ] Tách các DTO nội tuyến trong Controller ra các class riêng biệt trong package `dto/`.
  - [ ] Bổ sung Jakarta Bean Validation (`@NotBlank`, `@Email`, `@Size`) cho toàn bộ request body.
