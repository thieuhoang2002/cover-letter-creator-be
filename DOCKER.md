# 🐳 Hướng dẫn Container hóa & Triển khai với Docker (DOCKER.md)

Tài liệu này hướng dẫn chi tiết cách đóng gói và khởi chạy dự án **Cover Letter Creator Backend** bằng Docker và Docker Compose.

---

## 1. Yêu cầu hệ thống

- Đã cài đặt **Docker Desktop** (hoặc Docker Engine trên Linux).
- Đã cài đặt **Docker Compose** (thường đi kèm Docker Desktop).

---

## 2. Tạo Dockerfile cho Backend

Tạo file có tên `Dockerfile` (không có đuôi mở rộng) tại thư mục gốc của dự án:

```dockerfile
# ==========================================
# Bước 1: Build source code bằng Maven & JDK 21
# ==========================================
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy file cấu hình maven và pom.xml để tải cache dependencies trước
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN mvn dependency:go-offline -B

# Copy toàn bộ mã nguồn vào và tiến hành đóng gói JAR
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================
# Bước 2: Chạy ứng dụng với JRE 21 nhẹ và bảo mật
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Cài đặt font để iText có thể render font chữ tiếng Việt khi tạo PDF
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

Tạo file `docker-compose.yml` tại thư mục gốc để chạy đồng thời cả Database MySQL và Backend Spring Boot:

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
    build: .
    container_name: cover-letter-backend
    restart: always
    depends_on:
      - mysql-db
    ports:
      - "8080:8080"
    environment:
      # Cấu hình Spring Boot ghi đè bằng biến môi trường Docker
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql-db:3306/cover_letter_creator_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: rootpassword123
      API_KEY: your_groq_api_key_here
      JWT_SECRET: your_jwt_secret_key_here
    networks:
      - app-network

volumes:
  mysql_data:

networks:
  app-network:
    driver: bridge
```

---

## 4. Các lệnh điều khiển Docker

### Khởi chạy toàn bộ hệ thống (chạy ngầm):
```bash
docker compose up -d --build
```

### Xem log thời gian thực của backend:
```bash
docker logs -f cover-letter-backend
```

### Dừng toàn bộ hệ thống:
```bash
docker compose down
```

### Dừng và xóa toàn bộ dữ liệu database volume:
```bash
docker compose down -v
```

---

## 5. Truy cập ứng dụng sau khi chạy

- **API Base URL:** `http://localhost:8080`
- **Health Check:** `http://localhost:8080/api/ai/health`
