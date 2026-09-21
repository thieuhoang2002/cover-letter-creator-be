# HANDOVER.md — Cover Letter Creator Backend

## 1. Yêu cầu môi trường

| Yêu cầu | Phiên bản |
|---|---|
| Java | **21** (JDK 21) |
| Maven | Không cần cài riêng — dùng `mvnw` có sẵn |
| MySQL | 8.0+ |
| Kết nối internet | Để gọi DeepSeek API, Google Drive API, Gmail SMTP |

---

## 2. Chuẩn bị trước khi chạy

### 2.1. Database
- Tạo database MySQL nếu chạy local:
  ```sql
  CREATE DATABASE cover_letter_creator_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
  ```
- Cấu hình trong `src/main/resources/application.properties`:
  ```properties
  # Comment dòng deploy, bỏ comment dòng local:
  spring.datasource.url=jdbc:mysql://localhost:3306/cover_letter_creator_db
  spring.datasource.username=root
  spring.datasource.password=123456
  ```
  > **Lưu ý:** `spring.jpa.hibernate.ddl-auto=update` — Hibernate sẽ tự tạo/cập nhật bảng khi khởi động.

### 2.2. Google Drive Service Account
- Đặt file JSON của Service Account vào:  
  `src/main/resources/calendar-438415-5bdb470fb244.json`
- File này phải có quyền truy cập Google Drive API.

### 2.3. Font chữ cho PDF
- Đặt 4 file font vào `src/main/resources/fonts/`:
  - `Times_New_Roman.ttf`
  - `Times_New_Roman_Bold.ttf`
  - `Times_New_Roman_Italic.ttf`
  - `Times_New_Roman_Bold_Italic.ttf`

---

## 3. Chạy ứng dụng

### Cách 1: Maven Wrapper (khuyến nghị)
```bash
# Windows PowerShell:
.\mvnw.cmd spring-boot:run

# Windows CMD:
mvnw.cmd spring-boot:run
```

### Cách 2: Build JAR rồi chạy
```bash
.\mvnw.cmd package -DskipTests
java -jar target/CoverLetterCreator-0.0.1-SNAPSHOT.jar
```

### Cách 3: Chạy từ IDE
- Mở `CoverLetterCreatorApplication.java`, nhấn Run.

---

## 4. Port và URL mặc định

| Item | Giá trị |
|---|---|
| Port | **8080** |
| Base URL | `http://localhost:8080` |
| Context path | `/` (không có prefix) |

---

## 5. Mẫu cURL test API

### 5.1. Health Check AI
```bash
curl -X GET http://localhost:8080/api/ai/health
```
**Expected:** `{"status":"healthy","service":"CV Generator API"}`

---

### 5.2. Đăng ký tài khoản
```bash
curl -X POST http://localhost:8080/api/users/profile/register \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Test User\",\"email\":\"test@example.com\",\"password\":\"123456\",\"role\":\"user\"}"
```

---

### 5.3. Đăng nhập (lấy JWT token)
```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"test@example.com\",\"password\":\"123456\"}"
```
**Expected:** Một chuỗi JWT token. Lưu token này để dùng cho các request tiếp theo.

```
# Lưu token vào biến (PowerShell):
$TOKEN = "eyJhbGci..."

# Lưu token vào biến (Bash):
TOKEN="eyJhbGci..."
```

---

### 5.4. Lấy profile người dùng hiện tại
```bash
# PowerShell:
curl -X GET http://localhost:8080/api/users/profile/me `
  -H "Authorization: Bearer $TOKEN"

# Bash/CMD:
curl -X GET http://localhost:8080/api/users/profile/me \
  -H "Authorization: Bearer $TOKEN"
```

---

### 5.5. Cập nhật profile (kể cả skills, experiences, v.v.)
```bash
curl -X PUT http://localhost:8080/api/users/profile/me \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"name\": \"Nguyen Van A\",
    \"phone\": \"0123456789\",
    \"address\": \"Ha Noi\",
    \"specialization\": \"Backend Developer\",
    \"skills\": [{\"name\": \"Spring Boot\"}, {\"name\": \"MySQL\"}],
    \"experiences\": [{\"company\": \"ABC Corp\", \"role\": \"Dev\", \"time\": \"2022-2024\", \"description\": \"Developed REST APIs\"}],
    \"educations\": [{\"school\": \"HUST\", \"degree\": \"Bachelor\", \"fieldOfStudy\": \"IT\", \"time\": \"2018-2022\"}],
    \"certificates\": [],
    \"hobbies\": [{\"name\": \"Coding\"}]
  }"
```

---

### 5.6. Lấy danh sách template đang active
```bash
curl -X GET http://localhost:8080/api/templates/all
```

---

### 5.7. Sinh CV bằng AI (DeepSeek)
```bash
curl -X POST http://localhost:8080/api/ai/generate-cv \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"position\": \"Backend Developer\",
    \"theme\": \"blue\",
    \"userData\": {
      \"name\": \"Nguyen Van A\",
      \"email\": \"test@example.com\",
      \"skills\": [{\"name\": \"Spring Boot\"}],
      \"experiences\": [],
      \"educations\": [],
      \"certificates\": [],
      \"hobbies\": []
    }
  }"
```
**Expected:** `{"status":"success","content":"<div>...HTML CV...</div>"}`

---

### 5.8. Xuất PDF và upload Google Drive
```bash
curl -X POST http://localhost:8080/api/ai-cv/pdf/generate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"htmlContent\": \"<div style='font-family:Arial'><h1>CV Test</h1></div>\",
    \"id\": \"1\",
    \"email\": \"test@example.com\",
    \"templateName\": \"AI Template\",
    \"date\": \"21/09/2026\"
  }"
```
**Expected:** `"File upload thành công lên Google Drive. File ID: <driveId>"`

---

### 5.9. Lấy danh sách AI CV PDF của user
```bash
curl -X GET http://localhost:8080/api/ai-cv/pdf/list/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

### 5.10. Quên mật khẩu
```bash
curl -X POST http://localhost:8080/api/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"test@example.com\"}"
```

---

### 5.11. Đăng nhập bằng Google (Frontend gửi Google ID Token)
```bash
curl -X POST http://localhost:8080/api/users/google-login \
  -H "Content-Type: application/json" \
  -d "{\"token\":\"<Google_ID_Token_từ_Frontend>\"}"
```

---

## 6. Postman Collection

Import file dưới đây vào Postman (tạo thủ công hoặc yêu cầu cấp thêm):
1. Tạo Environment với variable: `base_url = http://localhost:8080`, `token = <JWT>`
2. Trong Authorization tab chọn **Bearer Token**, điền `{{token}}`
3. Tất cả endpoint bảo mật cần header: `Authorization: Bearer {{token}}`

---

## 7. CORS đã cho phép

| Origin |
|---|
| `http://localhost:5173` (Vite dev server) |
| `https://cover-letter-creator-fe.vercel.app` (Production FE) |

---

## 8. Deploy (Railway)

Thông tin trong `note_deploy.txt` và `application.properties`:
- Deploy host: `shinkansen.proxy.rlwy.net:36673`
- Database: Railway MySQL
- Khi deploy production, đảm bảo bỏ comment đúng dòng `spring.datasource.*`

---

## 9. Troubleshooting thường gặp

| Lỗi | Nguyên nhân | Cách sửa |
|---|---|---|
| `Service account key file not found` | Thiếu file JSON Google Drive | Đặt file vào `src/main/resources/` đúng tên |
| `Font không tìm thấy` (PDF bị lỗi font) | Thiếu file `.ttf` trong `resources/fonts/` | Đặt đủ 4 file font |
| `401 Unauthorized` từ API | JWT hết hạn (10h) hoặc sai secret | Đăng nhập lại để lấy token mới |
| `Error generating or uploading PDF: ...` | Google Drive API lỗi hoặc hết quota | Kiểm tra Service Account permissions |
| `api.key` related error | DeepSeek API key sai/hết credit | Cập nhật `api.key` trong `application.properties` |
| MySQL connection refused | DB chưa chạy hoặc sai host/port | Kiểm tra `spring.datasource.*` |
