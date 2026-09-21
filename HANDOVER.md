# HANDOVER.md — Cover Letter Creator Backend

## 1. Yêu Cầu Môi Trường (Prerequisites)

| Yêu cầu | Phiên bản | Ghi chú |
|---|---|---|
| **Java** | **21** (JDK 21 LTS) | Bắt buộc để tương thích Spring Boot 3.4.3 |
| **Maven** | Không cần cài ngoài | Sử dụng trực tiếp wrapper `mvnw` hoặc `mvnw.cmd` |
| **MySQL** | 8.0+ / MariaDB | Khuyến nghị dùng XAMPP MySQL trên port 3306 |
| **Groq Cloud API Key** | Cung cấp bởi Groq | Đăng ký miễn phí tại https://console.groq.com/keys |
| **Cloudflare R2** | S3-Compatible Storage | Cấu hình Account ID, Access Key, Secret Key, Bucket |

---

## 2. Chuẩn Bị & Thiết Lập Cấu Hình

### 2.1. Thiết lập Cơ sở Dữ liệu
1. Khởi động MySQL trong **XAMPP Control Panel**.
2. Tạo database mới trong phpMyAdmin (`http://localhost/phpmyadmin`):
   ```sql
   CREATE DATABASE cover_letter_creator_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. Sau khi khởi động ứng dụng lần đầu (để Hibernate tự sinh bảng), tiến hành nạp các template mẫu:
   ```powershell
   C:\xampp\mysql\bin\mysql.exe -u root cover_letter_creator_db < ..\cover-letter-creator-fe\seed_templates.sql
   ```

### 2.2. Cấu hình Tham Số (`src/main/resources/application.properties`)
Đảm bảo các cấu hình sau đã có trong file:
```properties
# 1. Cổng máy chủ
server.port=8080

# 2. Kết nối Database
spring.datasource.url=jdbc:mysql://localhost:3306/cover_letter_creator_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update

# 3. Groq Cloud AI Service
api.key=your_groq_api_key_here
groq.model=openai/gpt-oss-120b
groq.fallback-model=llama-3.3-70b-versatile

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
*Kết quả kỳ vọng:*
```json
{"status":"UP","provider":"Groq Cloud API","models":["openai/gpt-oss-120b","llama-3.3-70b-versatile"]}
```

### 4.2. Lấy Danh Sách Template Mẫu
```bash
# Template Classic Cover Letter
curl -X GET http://localhost:8080/api/templates/all

# Template Modern CV
curl -X GET http://localhost:8080/api/templates-modern/all
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
      "phone": "0987654321",
      "address": "Hà Nội",
      "specialization": "React & TypeScript",
      "skills": [{"name": "React"}, {"name": "JavaScript"}, {"name": "TailwindCSS"}]
    }
  }'
```

### 4.4. Xuất File PDF và Tải Trực Tiếp
```bash
curl -X POST http://localhost:8080/api/ai-cv/pdf/generate \
  -H "Content-Type: application/json" \
  -d '{
    "htmlContent": "<div style=\"font-family: Times New Roman;\"><h1>Curriculum Vitae</h1><p>Họ tên: Nguyễn Văn A</p></div>",
    "id": 1,
    "email": "vana@example.com",
    "templateName": "AI-Generated CV for Frontend",
    "date": "21/09/2026"
  }' \
  --output test-download.pdf
```
*(File `test-download.pdf` sẽ được ghi thẳng xuống thư mục hiện hành và bản ghi sẽ được lưu trên Cloudflare R2).*

---

## 5. Các Vấn Đề Thường Gặp & Cách Khắc Phục (Troubleshooting)

1. **Lỗi `400 Bad Request: model_decommissioned`:**
   - Nguyên nhân: Groq đã tắt các model cũ như `mixtral-8x7b-32768`.
   - Khắc phục: Kiểm tra `application.properties`, đảm bảo `groq.model=openai/gpt-oss-120b`.
2. **Lỗi `Unknown column 'title' in 'field list'` khi thao tác bảng `ai_cv_pdf`:**
   - Bảng `ai_cv_pdf` lưu các trường: `id`, `user_id`, `url_google_drive` (chứa R2 URL), `created_at`.
   - Nếu cần truy vấn, dùng: `SELECT id, user_id, url_google_drive, created_at FROM ai_cv_pdf;`.
3. **Lỗi Font chữ tiếng Việt khi xuất PDF trên Linux/Docker:**
   - Đảm bảo cài gói `fontconfig` và `ttf-dejavu` trong Dockerfile, đồng thời thư mục `src/main/resources/fonts/` có đủ 4 file font Times New Roman.
