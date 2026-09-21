# TODO.md — Cover Letter Creator Backend

Theo dõi tiến độ phát triển, các tính năng đã hoàn thiện, tồn đọng kỹ thuật và danh sách nhiệm vụ cần thực hiện cho Backend Spring Boot.

---

## ✅ Các Hạng Mục Đã Hoàn Thành (Completed)

### 1. Hạ Tầng Đám Mây & Triển Khai Production (Cloud Deployment & DevOps)
- [x] **Container hóa Docker Production-ready**:
  - [x] Dockerfile multi-stage (Maven 3.9.9 + JDK 21 Alpine builder, Eclipse Temurin 21 JRE Alpine runner).
  - [x] Tích hợp `fontconfig` và `ttf-dejavu` đảm bảo iText 4.0.3 render chính xác font chữ tiếng Việt khi xuất PDF.
  - [x] Tối ưu hóa JVM cho gói Render Free 512MB RAM: `-XX:+UseContainerSupport -Xmx384m`.
  - [x] Cấu hình Maven Mirror (`settings.xml` trỏ Google Cloud Maven Central) giải quyết triệt để lỗi rate-limiting `429 Too Many Requests`.
- [x] **Triển khai Render Web Service**:
  - [x] Deploy live backend tại: `https://cover-letter-creator-be-eadm.onrender.com`.
  - [x] Thiết lập endpoint kiểm tra sức khỏe công khai: `GET /api/ai/health` phục vụ giám sát keep-alive định kỳ qua `cron-job.org`.
- [x] **Di trú Cơ sở Dữ liệu Sang TiDB Cloud Serverless**:
  - [x] Chuyển đổi thành công từ MySQL XAMPP cục bộ lên **TiDB Cloud Serverless** (AWS ap-southeast-1, MySQL 8.0 wire-compatible).
  - [x] Bật bảo mật SSL/TLS bắt buộc `sslMode=VERIFY_IDENTITY`.
  - [x] Tự động cập nhật bảng biểu JPA Hibernate (`spring.jpa.hibernate.ddl-auto=update`).
- [x] **Bộ Dữ Liệu Mẫu Phong Phú (11 Templates)**:
  - [x] **6 mẫu Cover Letter**: 3 mẫu Chuẩn Cơ Quan Nhà Nước (Hành chính, Giảng dạy, Kế toán) + 3 mẫu Hiện Đại (Kỹ sư Phần mềm, Marketing, Nhân sự).
  - [x] **5 mẫu Modern CV**: 2 mẫu Chuẩn Cơ Quan Nhà Nước (Cán bộ Viên chức, Giáo dục) + 3 mẫu Hiện Đại (Công nghệ Thông tin, Kinh doanh, Thiết kế).
  - [x] Bộ script SQL khởi tạo tự động `seed_templates.sql`.

### 2. Bảo Mật, JWT & CORS Linh Hoạt
- [x] **JWT Token HMAC-SHA512 Siêu Bền Vững (Resilient Signing Key)**:
  - [x] Nâng cấp `JwtUtil.java` hỗ trợ đa định dạng Base64, Base64URL.
  - [x] Tự động băm an toàn qua **SHA-512 digest** cho mọi chuỗi ký tự, triệt tiêu hoàn toàn lỗi `Illegal base64 character: '_'`.
- [x] **CORS Toàn Diện (Cross-Origin Resource Sharing)**:
  - [x] Cấu hình `setAllowedOriginPatterns("*")` cho phép linh hoạt mọi domain Vercel (`*.vercel.app`) và các port localhost (`5173`, `5174`, `3000`).
  - [x] Hỗ trợ đầy đủ `AllowCredentials(true)` và expose các headers quan trọng (`Content-Disposition`, `Authorization`).
- [x] **Tách Biệt Môi Trường & Bảo Vệ Khóa Bí Mật**:
  - [x] File cấu hình mẫu an toàn `application.properties.example`.
  - [x] Toàn bộ secrets (`SPRING_DATASOURCE_*`, `JWT_SECRET`, `API_KEY`, `CLOUDFLARE_R2_*`, `GITHUB_*`, `GOOGLE_*`) được nạp qua Environment Variables.
  - [x] Vượt qua 100% kiểm tra bảo mật của GitHub Secret Scanning.

### 3. AI & Lưu Trữ Đám Mây (Groq Cloud & Cloudflare R2)
- [x] **Tích hợp Groq Cloud API**:
  - [x] Model chính: `openai/gpt-oss-120b` (tốc độ cao, suy luận sắc bén).
  - [x] Model dự phòng (Fallback): `llama-3.3-70b-versatile` tự động kích hoạt khi có lỗi quota.
  - [x] Prompt chuẩn hóa sinh HTML/CSS inline tương thích bộ render A4 của iText.
- [x] **Xuất PDF Dạng Binary Stream**:
  - [x] Loại bỏ hoàn toàn phụ thuộc vào Google Drive API.
  - [x] Trả file trực tiếp qua luồng byte `application/pdf`.
- [x] **Lưu Trữ Đám Mây Cloudflare R2**:
  - [x] Bucket S3-compatible `cover-letter-cv-storage` kết nối qua AWS SDK S3.
  - [x] Phân phối file tức thì qua CDN public URL.
- [x] **Deduplication Guard**: Chống spam click tạo trùng lặp bản ghi PDF trong khung thời gian 10 giây.

### 4. Chiến Lược Phân Nhánh Git (Branching Strategy)
- [x] **Nhánh `main`**: Ổn định, khóa cố định phục vụ CI/CD tự động lên Vercel và Render.
- [x] **Nhánh `dev`**: Nhánh phát triển tính năng mới, thử nghiệm lột xác sản phẩm trước khi merge vào `main`.

---

## 🚀 Danh Mục Phát Triển Tính Năng Mới Trên Nhánh `dev` (New Roadmap)

### 🔴 Ưu Tiên Cao (Phase 1 — Nâng Cấp Nền Tảng & AI Đỉnh Cao)
- [ ] **AI Streaming Response (Server-Sent Events / SSE)**:
  - Xây dựng endpoint `GET /api/ai/stream-cv` sử dụng WebMvc `SseEmitter` để stream trực tiếp từng từ từ Groq API về Frontend, tạo hiệu ứng chữ gõ tức thì.
- [ ] **AI ATS Resume Reviewer (Chấm Điểm CV Thông Minh)**:
  - API `POST /api/ai/review-cv`: Nhận nội dung CV + Mô tả công việc (Job Description), phân tích độ tương thích (0 - 100%), liệt kê điểm mạnh, điểm yếu và từ khóa còn thiếu.
- [ ] **AI Tone Rewriter**:
  - API `POST /api/ai/rewrite-sentence`: Viết lại đoạn văn bản theo phong cách yêu cầu (Chuyên nghiệp, Tự tin, Ngắn gọn, Thuyết phục).

### 🟡 Ưu Tiên Trung Bình (Phase 2 — Tiện Ích Người Dùng & Portfolio)
- [ ] **API Chia Sẻ CV Công Khai (Public Shareable Link)**:
  - Sinh slug / token duy nhất cho CV để người dùng có thể gửi link trực tiếp cho nhà tuyển dụng xem mà không cần đăng nhập.
- [ ] **API Sao Chép Bản Ghi (Duplicate CV / Cover Letter)**:
  - Cho phép người dùng nhân bản một CV đã tạo để tùy biến cho từng công ty khác nhau.
- [ ] **Tự Động Lưu Nháp (Auto-save API)**:
  - Endpoint lưu nhanh trạng thái đang soạn thảo của người dùng vào database theo cơ chế debounce.

### 🟢 Ưu Tiên Nâng Cao (Phase 3 — Tối Ưu & Mở Rộng)
- [ ] **Rate Limiting (Bucket4j)**: Giới hạn số lượt gọi AI theo IP / User để bảo vệ hạn ngạch API Groq.
- [ ] **Thống kê nâng cao cho Admin**: Tổng số lượt xem, số lần xuất PDF theo biểu đồ thời gian thực.
