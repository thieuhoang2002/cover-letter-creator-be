# 🐳 Hướng dẫn Container hóa & Triển khai với Docker (DOCKER.md)

Tài liệu này hướng dẫn chi tiết cách đóng gói và khởi chạy dự án **Cover Letter Creator Backend** (Spring Boot 3.4.3, Java 21, Groq AI, Cloudflare R2) bằng Docker và Docker Compose.

---

## 1. Yêu cầu hệ thống

- Đã cài đặt **Docker Desktop** (trên Windows/macOS) hoặc **Docker Engine** (trên Linux).
- Đã cài đặt **Docker Compose** v2 trở lên.

---

## 2. File Cấu Hình Dockerfile

Tạo file `Dockerfile` tại thư mục gốc backend:

```dockerfile
# ==========================================
# Bước 1: Build source code bằng Maven & JDK 21
# ==========================================
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml và tải dependencies cache
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN mvn dependency:go-offline -B

# Copy toàn bộ mã nguồn và tiến hành compile/package
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================
# Bước 2: Chạy ứng dụng với JRE 21 nhẹ và bảo mật
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Cài đặt fontconfig hỗ trợ iText render font tiếng Việt khi tạo PDF
RUN apk add --no-cache fontconfig ttf-dejavu

# Copy file JAR từ bước build
COPY --from=build /app/target/CoverLetterCreator-0.0.1-SNAPSHOT.jar app.jar

# Mở cổng 8080 của ứng dụng
EXPOSE 8080

# Chạy ứng dụng Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 3. Khởi chạy trọn gói bằng Docker Compose (Backend + MySQL)

Tạo file `docker-compose.yml` tại thư mục gốc backend:

```yaml
version: '3.8'

services:
  # Service 1: MySQL Database
  mysql-db:
    image: mysql:8.0
    container_name: cover-letter-mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword123
      MYSQL_DATABASE: cover_letter_creator_db
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    networks:
      - app-network

  # Service 2: Spring Boot Backend
  backend:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: cover-letter-backend
    restart: always
    depends_on:
      - mysql-db
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql-db:3306/cover_letter_creator_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: rootpassword123
      API_KEY: your_groq_api_key_here
      GROQ_MODEL: openai/gpt-oss-120b
      GROQ_FALLBACK_MODEL: llama-3.3-70b-versatile
      CLOUDFLARE_R2_ACCOUNT_ID: your_cloudflare_account_id
      CLOUDFLARE_R2_ACCESS_KEY: your_cloudflare_access_key
      CLOUDFLARE_R2_SECRET_KEY: your_cloudflare_secret_key
      CLOUDFLARE_R2_BUCKET_NAME: cover-letter-cv-storage
      CLOUDFLARE_R2_PUBLIC_URL: https://pub-your-bucket-id.r2.dev
    ports:
      - "8080:8080"
    networks:
      - app-network

volumes:
  mysql_data:

networks:
  app-network:
    driver: bridge
```

### Các lệnh vận hành:
```bash
# Khởi động dịch vụ trong nền
docker compose up -d --build

# Xem log thời gian thực của backend
docker compose logs -f backend

# Dừng và giải phóng tài nguyên
docker compose down
```
