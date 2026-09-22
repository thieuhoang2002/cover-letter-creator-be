# HANDOVER.md — Cover Letter Creator Backend

## 1. Yêu Cầu Môi Trường (Prerequisites)

| Yêu cầu | Phiên bản | Ghi chú |
|---|---|---|
| **Java** | **21** (JDK 21 LTS) | Bắt buộc để tương thích Spring Boot 3.4.3 |
| **Maven** | Không cần cài ngoài | Sử dụng trực tiếp wrapper `./mvnw` hoặc `.\mvnw.cmd` |
| **Database** | TiDB Cloud Serverless / MySQL 8.0+ | Hỗ trợ kết nối đám mây SSL hoặc local XAMPP |
| **Groq Cloud API Key** | Cung cấp bởi Groq | Đăng ký miễn phí tại https://console.groq.com/keys (hỗ trợ nhiều key phẩy) |
| **Cloudflare R2** | S3-Compatible Storage | Account ID, Access Key, Secret Key, Bucket `cover-letter-cv-storage` |

---

## 2. Chuẩn Bị & Thiết Lập Cấu Hình

### 2.1. Thiết lập Cơ sở Dữ liệu
1. **Đối với TiDB Cloud Serverless (Khuyên Dùng / Production):**
   - Đăng nhập TiDB Cloud Console -> Chọn Cluster -> Vào SQL Editor.
   - Chạy script cập nhật [`update_tidb_database.sql`](../update_tidb_database.sql) để tạo bảng mới (`followed_cvs`, `vip_upgrade_requests`, `password_reset_token`) mà không làm mất dữ liệu cũ.
2. **Đối với XAMPP Local MySQL:**
   - Khởi động MySQL trong XAMPP Control Panel.
   - Tạo DB: `CREATE DATABASE cover_letter_creator_db CHARACTER SET utf8mb4;`.
   - Nạp dữ liệu mẫu: `init_tidb_database.sql` và `seed_templates.sql`.

### 2.2. Cấu hình Tham Số (`src/main/resources/application.properties`)
```properties
# 1. Cổng máy chủ
server.port=8080

# 2. Kết nối Database (TiDB Cloud hoặc Local)
spring.datasource.url=jdbc:mysql://gateway01.ap-southeast-1.prod.aws.tidbcloud.com:4000/cover_letter_creator_db?useSSL=true&requireSSL=true
spring.datasource.username=your_tidb_user
spring.datasource.password=your_tidb_password
spring.jpa.hibernate.ddl-auto=update

# 3. Groq Cloud AI Service (Hỗ trợ nhiều key cách nhau bằng dấu phẩy)
api.key=gsk_key1,gsk_key2,gsk_key3
groq.model=openai/gpt-oss-120b
groq.fallback-model=llama-3.3-70b-versatile
groq.max-concurrent=3
groq.queue-timeout-seconds=45

# 4. Cloudflare R2 Storage
cloudflare.r2.account-id=your_cloudflare_account_id
cloudflare.r2.access-key=your_cloudflare_access_key
cloudflare.r2.secret-key=your_cloudflare_secret_key
cloudflare.r2.bucket-name=cover-letter-cv-storage
cloudflare.r2.public-url=https://pub-your-bucket-id.r2.dev
```

---

## 3. Khởi Chạy Ứng Dụng

### Sử dụng Terminal (Maven Wrapper)
```powershell
# Chạy trực tiếp từ thư mục cover-letter-creator-be:
.\mvnw.cmd spring-boot:run
```

### Build file JAR chạy độc lập
```powershell
.\mvnw.cmd clean package -DskipTests
java -jar target/CoverLetterCreator-0.0.1-SNAPSHOT.jar
```

---

## 4. Mẫu Lệnh Test API (cURL Commands)

### 4.1. Kiểm tra Sức Khỏe AI Service (Health Check)
```bash
curl -X GET http://localhost:8080/api/ai/health
```

### 4.2. Kiểm Tra Hạn Mức Upload CV (Quota)
```bash
curl -X GET http://localhost:8080/api/follow-cv/quota \
  -H "Authorization: Bearer <jwt_token>"
```
*Kết quả kỳ vọng:*
```json
{"used": 2, "max": 30, "remaining": 28, "isVip": true}
```

### 4.3. Gọi AI Sinh CV (HTML Format)
```bash
curl -X POST http://localhost:8080/api/ai/generate-cv \
  -H "Content-Type: application/json" \
  -d '{
    "position": "Frontend Developer",
    "theme": "blue",
    "userData": {
      "name": "Nguyễn Văn A",
      "email": "vana@example.com",
      "specialization": "React & TypeScript",
      "skills": [{"name": "React"}, {"name": "JavaScript"}]
    }
  }'
```

---

## 5. Các Vấn Đề Thường Gặp & Cách Khắc Phục (Troubleshooting)

1. **Lỗi `400 Bad Request: model_decommissioned`:**
   - Đảm bảo `groq.model=openai/gpt-oss-120b` (model chính) và `fallback=llama-3.3-70b-versatile`.
2. **Lỗi `429 Too Many Requests` khi gọi AI:**
   - Hệ thống tự động chuyển sang key tiếp theo trong danh sách `api.key`. Đảm bảo bạn nạp 2-3 key dự phòng trên Render Environment Variables.
3. **Lỗi Xóa CV trên R2 không ăn:**
   - Đã được khắc phục trong hàm `CloudflareR2Service.deleteFile`: S3 key giữ nguyên đầy đủ tiền tố thư mục ảo (`customer-cvs/{userId}/` và `avatars/`).
4. **Lỗi `Duplicate field FollowedCVController.FREE_UPLOAD_QUOTA`:**
   - Đã loại bỏ biến trùng lặp, code biên dịch sạch với `./mvnw clean compile`.
