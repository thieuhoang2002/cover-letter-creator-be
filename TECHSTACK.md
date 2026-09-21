# TECHSTACK.md — Cover Letter Creator Backend

## Platform & Language

| Item | Version |
|---|---|
| Java | 21 |
| Maven | Wrapper (mvnw) |
| Spring Boot | **3.4.3** |

---

## Core Dependencies (pom.xml)

### Web & MVC
| Dependency | ArtifactId | Ghi chú |
|---|---|---|
| Spring Boot Web | `spring-boot-starter-web` | REST API, embedded Tomcat |

### Security & Auth
| Dependency | Version | Ghi chú |
|---|---|---|
| `spring-boot-starter-security` | 3.4.3 | Spring Security |
| `spring-boot-starter-oauth2-client` | 3.4.3 | GitHub & Google OAuth2 login |
| `jjwt-api` | 0.12.5 | JWT tạo & xác thực token |
| `jjwt-impl` | 0.12.5 | Runtime implementation |
| `jjwt-jackson` | 0.12.5 | Jackson serializer cho JWT |
| `jakarta.validation-api` | 3.1.1 | Bean Validation (JSR-380) |

### Data Access
| Dependency | ArtifactId | Ghi chú |
|---|---|---|
| Spring Data JPA | `spring-boot-starter-data-jpa` | ORM với Hibernate |
| Spring JDBC | `spring-boot-starter-jdbc` | JDBC trực tiếp |
| Spring Data JDBC | `spring-boot-starter-data-jdbc` | Spring Data JDBC |
| MySQL Connector | `mysql-connector-j` | Runtime, MySQL 8+ |

### Code Generation
| Dependency | ArtifactId | Ghi chú |
|---|---|---|
| Lombok | `lombok` | `@Data`, `@Builder`, v.v. |

### PDF Generation
| Dependency | ArtifactId | Version | Ghi chú |
|---|---|---|---|
| iText html2pdf | `html2pdf` | **4.0.3** | HTML → PDF conversion |

### Google APIs
| Dependency | ArtifactId | Version | Ghi chú |
|---|---|---|---|
| Google API Client | `google-api-client` | 1.25.0 | Base client |
| Google Drive API | `google-api-services-drive` | v3-rev197-1.25.0 | Upload/delete files |
| Google HTTP Client Jackson2 | `google-http-client-jackson2` | 1.46.3 | HTTP transport |
| Google HTTP Client | `google-http-client` | 1.43.0 | Core HTTP |
| Google OAuth Client | `google-oauth-client` | 1.34.1 | OAuth2 flows |
| Google Auth Library | `google-auth-library-oauth2-http` | 1.33.1 | Service Account credentials |

### Email
| Dependency | ArtifactId | Version | Ghi chú |
|---|---|---|---|
| Spring Mail | `spring-boot-starter-mail` | 3.4.3 | JavaMailSender, SMTP Gmail |

### File Upload
| Dependency | ArtifactId | Version | Ghi chú |
|---|---|---|---|
| Commons FileUpload | `commons-fileupload` | 1.5 | Hỗ trợ upload file |

### Testing
| Dependency | ArtifactId | Ghi chú |
|---|---|---|
| `spring-boot-starter-test` | Unit & Integration tests |
| `spring-security-test` | Security test utilities |

---

## External Services

| Service | Mục đích | Config key |
|---|---|---|
| **Groq Cloud API** | Generate HTML CV từ prompt (Model: `llama-3.3-70b-versatile`) | `api.key` trong `application.properties` |
| **PDF Stream Engine** | iText html2pdf 4.0.3 (Direct Download / S3 Cloudflare R2 Ready) | Native Stream / S3 compatible |
| **Gmail SMTP** | Gửi email reset mật khẩu | `spring.mail.*` |
| **MySQL** | Database chính | `spring.datasource.*` |
| **GitHub OAuth2** | Social login | `spring.security.oauth2.client.registration.github.*` |
| **Google OAuth2** | Social login | `spring.security.oauth2.client.registration.google.*` |

---

## Build Plugins

| Plugin | Ghi chú |
|---|---|
| `maven-compiler-plugin` | Lombok annotation processor path được cấu hình |
| `spring-boot-maven-plugin` | Main class: `cover.letter.creator.CoverLetterCreatorApplication`, Lombok excluded khỏi fat jar |

---

## Kiến trúc Package

```
cover.letter.creator
├── config/          # SecurityConfig, JwtUtil, JwtAuthenticationFilter, GoogleDriveConfig, WebConfig
├── controller/      # 13 REST controllers
├── dto/             # Data Transfer Objects (request/response)
├── model/           # JPA Entities
├── repository/      # Spring Data JPA Repositories
└── service/         # Business Logic (13 services, bao gồm GroqAIService)
```

---

## Trạng thái biên dịch

> ✅ **BUILD SUCCESS** — `mvnw compile` exit code 0, 65 source files compiled thành công 100%, 0 warning.
