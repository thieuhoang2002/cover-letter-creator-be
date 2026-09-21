# 📋 PROJECT ROADMAP & AUDIT CHECKLIST — BACKEND (TODO.md)

Theo dõi chi tiết lộ trình phát triển, các tính năng đã hoàn thiện và danh sách nhiệm vụ kỹ thuật nâng cấp toàn diện dự án **Cover Letter Creator Backend** trên nhánh `dev`.

---

## ✅ 1. Trạng Thái Đã Hoàn Thành (Production & Branch `dev`)

### 1.1. Hạ Tầng & Triển Khai
- [x] **Triển khai Render Live Web Service**: `https://cover-letter-creator-be-eadm.onrender.com`.
- [x] **Docker Alpine Production**: Multi-stage build Maven 3.9 + JRE 21 Alpine, tích hợp font DejaVu & fontconfig hỗ trợ tiếng Việt đầy đủ.
- [x] **Tối ưu RAM Container**: Cấu hình `-XX:+UseContainerSupport -Xmx384m` ổn định trên Render Free tier 512MB RAM.
- [x] **Cơ sở dữ liệu TiDB Cloud Serverless**: MySQL 8.0 wire-compatible trên AWS ap-southeast-1 kèm bắt buộc SSL/TLS.
- [x] **Bộ Dữ Liệu 11 Mẫu Sẵn Có**: 6 Cover Letter (3 Nhà Nước + 3 Hiện Đại) và 5 Modern CV (2 Nhà Nước + 3 Hiện Đại).

### 1.2. Xác Thực, Phân Quyền & Bảo Mật Nâng Cao
- [x] **JWT Token Chuẩn Hóa**: Claim role trả về định dạng chữ thường (`user` / `admin`).
- [x] **Spring Security 6 RBAC**:
  - [x] Phân quyền chặt chẽ các endpoint Admin (`hasRole("ADMIN")`): `/api/users/profile`, tạo/sửa/xóa mẫu templates.
  - [x] Cho phép xem mẫu công khai (`permitAll()`): `GET /api/templates/**`, `GET /api/templates-modern/**`.
  - [x] Khớp chính xác endpoint profile người dùng (`/api/users/profile/me`).
- [x] **Rate Limiting (Bucket4j / Filter)**:
  - [x] Giới hạn tần suất gọi API đăng nhập, đăng ký, quên mật khẩu và tạo CV với Groq AI.
  - [x] Trả về mã lỗi `HTTP 429 Too Many Requests` kèm header `Retry-After`.
- [x] **Quy Trình Mật Khẩu An Toàn**:
  - [x] Sửa lỗi token reset password hết hạn sớm do lệch múi giờ (UTC vs GMT+7).
  - [x] Bổ sung endpoint `GET /api/users/profile/has-password` kiểm tra tài khoản đã có mật khẩu chưa.
  - [x] Bổ sung endpoint `POST /api/users/profile/change-password-without-old` dành riêng cho người dùng đăng nhập bằng Google/GitHub chưa tạo mật khẩu.

### 1.3. AI & Lưu Trữ PDF
- [x] **Groq Cloud API**: Model chính `openai/gpt-oss-120b` kết hợp tự động fallback sang `llama-3.3-70b-versatile`.
- [x] **Lưu Trữ Đám Mây Cloudflare R2**: Bucket S3-compatible `cover-letter-cv-storage` kết nối qua AWS SDK S3.
- [x] **Xuất PDF Binary Stream**: Trả file trực tiếp qua luồng byte `application/pdf` kèm header `Content-Disposition`.
- [x] **Deduplication Guard**: Chống spam click tạo trùng lặp bản ghi PDF trong khung 10 giây.

---

## 🎯 2. Nhiệm Vụ Kế Tiếp (TODO Ngày Mai)

### 📤 2.1. API Upload Avatar Người Dùng Lên Cloudflare R2
- [ ] **Endpoint `POST /api/users/profile/avatar`**:
  - [ ] Tiếp nhận file ảnh dạng `MultipartFile` từ người dùng đã đăng nhập (lấy email từ JWT).
  - [ ] Validation định dạng file MIME (`image/jpeg`, `image/png`, `image/webp`).
  - [ ] Giới hạn dung lượng tối đa `<= 2MB`.
  - [ ] Upload lên Cloudflare R2 bucket tại thư mục `avatars/` với tên file UUID ngẫu nhiên.
  - [ ] Cập nhật trường `avatar_url` trong bảng `users`.
  - [ ] Trả về JSON chứa URL ảnh công khai mới trên Cloudflare R2.

### 🛡️ 2.2. Kiểm Tra & Chuẩn Hóa Jakarta Bean Validation Toàn Diện (BE)
- [ ] **Rà soát & bổ sung Annotation Bean Validation cho toàn bộ DTOs**:
  - [ ] `LoginRequest`: `@NotBlank`, `@Email`.
  - [ ] `RegisterRequest`: `@NotBlank`, `@Email`, `@Size(min = 6)`.
  - [ ] `ChangePasswordRequest`: `@NotBlank`, `@Size(min = 6)`.
  - [ ] `TemplateDTO`: `@NotBlank(message = "Tên mẫu không được để trống")`.
  - [ ] `FollowedCVDTO`: `@NotBlank`, `@Size(max = 255)`.
- [ ] **Global Exception Handler (`@RestControllerAdvice`)**:
  - [ ] Bắt lỗi `MethodArgumentNotValidException` và trả về `Map<String, String>` chi tiết từng trường bị lỗi kèm HTTP 400 Bad Request.
  - [ ] Bắt lỗi `MaxUploadSizeExceededException` khi tải file vượt quá dung lượng cho phép.

### 📄 2.3. Cho Phép Upload CV Cá Nhân Từ Máy Khách Lên Cloudflare R2 (`FollowCV`)
- [ ] **Endpoint `POST /api/follow-cv/upload`**:
  - [ ] Nhận `MultipartFile` file PDF, kèm thông tin: `name`, `company`, `note`, `status`.
  - [ ] Server-side validation: Chỉ chấp nhận `application/pdf`, dung lượng tối đa `<= 10MB`.
  - [ ] Upload file PDF lên bucket Cloudflare R2 tại đường dẫn `uploaded-cvs/{userId}/{uuid}.pdf`.
  - [ ] Lưu bản ghi mới vào bảng `followed_cvs` với `url_google_drive` lưu link R2 URL.
  - [ ] Trả về đối tượng `FollowedCV` vừa được tạo thành công.

### 💎 2.4. Cơ Chế Kiểm Tra Quota Upload CV & Quản Lý Yêu Cầu VIP (Mock / Admin Phê Duyệt)
- [ ] **Giới hạn Quota Upload (Tối đa 3 file PDF CV cho tài khoản thường)**:
  - [ ] Kiểm tra trường phân loại hoặc role của User (`is_vip` hoặc role `vip`/`admin`).
  - [ ] Nếu là tài khoản thường (`user`): Đếm số lượng file PDF do khách tự upload trong bảng `followed_cvs`.
  - [ ] Nếu đã đạt ngưỡng **>= 3 file**: Ném ngoại lệ `QuotaExceededException` / trả về `HTTP 403 Forbidden` kèm thông điệp:
    > *"Bạn đã đạt giới hạn tối đa 3 file CV tải lên. Vui lòng gửi yêu cầu nâng cấp gói VIP để tiếp tục lưu trữ."*
- [ ] **Bảng Cơ Sở Dữ Liệu `vip_upgrade_requests`**:
  - [ ] Cột: `id`, `user_id`, `plan_name` (Pro / Enterprise), `user_note`, `status` (`PENDING`, `APPROVED`, `REJECTED`), `created_at`, `reviewed_at`.
- [ ] **Endpoint Gửi Yêu Cầu Nâng Cấp VIP Cho Khách**:
  - [ ] `POST /api/vip/request`: Tiếp nhận yêu cầu nâng cấp gói VIP, kiểm tra nếu đã có yêu cầu PENDING thì không cho gửi trùng lặp.
  - [ ] `GET /api/vip/my-status`: Kiểm tra trạng thái yêu cầu nâng cấp của user hiện tại.
- [ ] **Endpoint Quản Trị Phê Duyệt Yêu Cầu VIP (Dành Cho Admin)**:
  - [ ] `GET /api/admin/vip-requests`: Lấy danh sách toàn bộ yêu cầu nâng cấp gói (kèm bộ lọc trạng thái).
  - [ ] `PUT /api/admin/vip-requests/{id}/approve`: Duyệt yêu cầu, cập nhật trạng thái user thành VIP (nâng hạn ngạch upload).
  - [ ] `PUT /api/admin/vip-requests/{id}/reject`: Từ chối yêu cầu kèm ghi chú lý do.

### ⏳ 2.5. Hàng Đợi Điều Phối Tác Vụ AI (AI Request Queue & Concurrency Limiting)
- [ ] **Bộ Điều Phối Hàng Đợi Xử Lý AI (AI Concurrency Queue)**:
  - [ ] Giới hạn số lượng tác vụ Groq AI chạy đồng thời (Concurrency Limit = 2-3 tác vụ song song) để tránh chạm trần TPM/RPM của Groq API khi người dùng đông.
  - [ ] Sử dụng `PriorityBlockingQueue` hoặc `CompletableFuture` với Semaphore để quản lý hàng đợi các request tạo CV.
  - [ ] Trả về vị trí hiện tại trong hàng đợi (`queue_position`) cho client polling hoặc push qua Server-Sent Events (SSE).
- [ ] **Ưu Tiên Luồng Cho Tài Khoản VIP (Priority Scheduling)**:
  - [ ] Gán độ ưu tiên cao cho tài khoản VIP / Admin: Yêu cầu của VIP được đẩy lên đầu hàng đợi để thực thi ngay lập tức.
  - [ ] Tránh tình trạng starvation: Cơ chế timeout sau 45 giây nếu hàng đợi quá tải, trả về thông báo thân thiện kèm hoàn tác lượt tạo.
