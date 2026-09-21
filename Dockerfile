# ==========================================
# Bước 1: Build source code bằng Maven & JDK 21
# ==========================================
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy cấu hình Maven
COPY pom.xml .

# Tải dependencies trước để cache layer
RUN mvn dependency:go-offline -B || true

# Copy toàn bộ mã nguồn
COPY src ./src

# Tự động tạo application.properties từ application.properties.example nếu chưa có
RUN cp src/main/resources/application.properties.example src/main/resources/application.properties

# Đóng gói file JAR
RUN mvn clean package -DskipTests

# ==========================================
# Bước 2: Chạy ứng dụng với JRE 21 nhẹ (Alpine)
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Cài đặt fontconfig hỗ trợ iText render font tiếng Việt khi tạo PDF
RUN apk add --no-cache fontconfig ttf-dejavu

# Copy file JAR từ build stage
COPY --from=build /app/target/*.jar app.jar

# Mở cổng 8080
EXPOSE 8080

# Tối ưu RAM cho Render Free Tier (512MB RAM tối đa)
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-Xmx384m", "-jar", "app.jar"]
