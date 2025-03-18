package cover.letter.creator.controller;

import cover.letter.creator.model.User;
import cover.letter.creator.service.UserService;
import cover.letter.creator.config.JwtUtil;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class GoogleLoginController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId; // Lấy client-id từ application.properties

    @PostMapping("/google-login")
    public ResponseEntity<String> googleLogin(@RequestBody GoogleLoginRequest googleLoginRequest) {
        try {
            // Xác minh token Google bằng GoogleIdToken
            GoogleIdToken idToken = GoogleIdToken.parse(GsonFactory.getDefaultInstance(), googleLoginRequest.getToken());

            if (idToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token Google không hợp lệ");
            }

            // Lấy thông tin từ token Google
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name"); // Tên người dùng từ Google
            String picture = (String) payload.get("picture"); // Lấy URL ảnh đại diện
            System.out.println("Google User Data: " + payload);

            // Kiểm tra user trong database
            Optional<User> existingUser = userService.getUserByEmail(email);
            User user;
            String role;

            if (existingUser.isPresent()) {
                // User đã tồn tại
                user = existingUser.get();
                role = user.getRole();
                
             // Cập nhật avatar_url nếu có thay đổi
                if (picture != null && !picture.equals(user.getAvatarUrl())) {
                    user.setAvatarUrl(picture);
                    userService.updateUser(user);
                }
            } else {
                // Tạo user mới nếu chưa tồn tại
                user = new User();
                user.setEmail(email);
                user.setName(name);
                user.setPassword(""); // Không cần mật khẩu cho Google login
                user.setAvatarUrl(picture); // Lưu avatar_url
                role = "user"; // Gán mặc định là "user"
                user.setRole(role);
                userService.registerUser(user); // Lưu user mới
            }

//         // Tạo JWT với email, role và picture
//            Map<String, Object> claims = new HashMap<>();
//            claims.put("role", role);
//            claims.put("avatar_url", picture); // Dùng chung key "avatar_url" với GitHub
//            String jwt = jwtUtil.generateTokenWithClaims(email, claims);
//
//            return ResponseEntity.ok(jwt);
            String jwt = jwtUtil.generateToken(email, role); // JWT không cần avatar_url nữa
            return ResponseEntity.ok(jwt);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Lỗi khi xử lý đăng nhập Google: " + e.getMessage());
        }
    }

}

class GoogleLoginRequest {
    private String token;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}