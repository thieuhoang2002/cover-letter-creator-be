package cover.letter.creator.controller;

import cover.letter.creator.model.User;
import cover.letter.creator.service.UserService;
import cover.letter.creator.config.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class SocialLoginController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String githubClientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String githubClientSecret;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/github-login")
    public ResponseEntity<String> githubLogin(@RequestBody GithubLoginRequest githubLoginRequest) {
        try {
            // Gửi yêu cầu lấy access token từ GitHub
            String tokenUrl = "https://github.com/login/oauth/access_token" +
                "?client_id=" + githubClientId +
                "&client_secret=" + githubClientSecret +
                "&code=" + githubLoginRequest.getCode();

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenUrl))
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            System.out.println("Access Token Response: " + responseBody); // Log phản hồi

            JsonNode tokenNode = objectMapper.readTree(responseBody);
            if (tokenNode.has("error")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Lỗi lấy access token: " + tokenNode.get("error_description").asText());
            }
            String accessToken = tokenNode.get("access_token").asText();

            if (accessToken == null || accessToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Không lấy được access token từ GitHub");
            }

            // Lấy thông tin user từ GitHub API
            HttpRequest userRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/user"))
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/vnd.github+json") // Đảm bảo định dạng JSON
                .GET()
                .build();

            HttpResponse<String> userResponse = client.send(userRequest, HttpResponse.BodyHandlers.ofString());
            String userData = userResponse.body();
            System.out.println("GitHub User Data: " + userData); // Log dữ liệu user

            JsonNode userNode = objectMapper.readTree(userData);
            if (userNode.has("message")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Lỗi từ GitHub API: " + userNode.get("message").asText());
            }

            // Trích xuất email, name, login
            String email = userNode.has("email") && !userNode.get("email").isNull() ? userNode.get("email").asText() : null;
            String name = userNode.has("name") && !userNode.get("name").isNull() ? userNode.get("name").asText() : null;
            String login = userNode.get("login").asText();
            String avatarUrl = userNode.get("avatar_url").asText();

            if (login == null || login.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không thể lấy login từ GitHub");
            }
            if (email == null || email.isEmpty()) {
                email = login + "@github.com"; // Tạo email tạm
            }

            // Kiểm tra và tạo user
            Optional<User> existingUser = userService.getUserByEmail(email);
            User user;
            String role;

            if (existingUser.isPresent()) {
                user = existingUser.get();
                role = user.getRole();
             // Cập nhật avatar_url nếu có thay đổi
                if (avatarUrl != null && !avatarUrl.equals(user.getAvatarUrl())) {
                    user.setAvatarUrl(avatarUrl);
                    userService.updateUser(user);
                }
            } else {
                user = new User();
                user.setEmail(email);
                user.setName(name != null ? name : login);
                user.setPassword("");
                user.setAvatarUrl(avatarUrl); // Lưu avatar_url
                role = "user";
                user.setRole(role);
                userService.registerUser(user);
            }

//         // Tạo JWT với avatar_url
//            Map<String, Object> claims = new HashMap<>();
//            claims.put("role", role);
//            claims.put("avatar_url", avatarUrl);
//            String jwt = jwtUtil.generateTokenWithClaims(email, claims);
            
            String jwt = jwtUtil.generateToken(email, role); // JWT không cần avatar_url
            return ResponseEntity.ok(jwt);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Lỗi khi xử lý đăng nhập GitHub: " + e.getMessage());
        }
    }
}

class GithubLoginRequest {
    private String code;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}