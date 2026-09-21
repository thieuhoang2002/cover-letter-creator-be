package cover.letter.creator.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cover.letter.creator.repository.UserRepository;
import cover.letter.creator.service.EmailService;
import cover.letter.creator.service.PasswordResetService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin 
public class PasswordResetController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Vui lòng cung cấp địa chỉ email.");
        }

        String cleanEmail = email.trim().toLowerCase();
        var userOptional = userRepository.findByEmail(cleanEmail);
        if (userOptional.isPresent()) {
            try {
                String token = passwordResetService.createPasswordResetToken(userOptional.get());
                emailService.sendPasswordResetEmail(cleanEmail, token);
            } catch (Exception e) {
                // Log internal error but don't leak stack trace to user
            }
        }

        // Return generic message to prevent User Enumeration attacks
        return ResponseEntity.ok("Nếu email của bạn tồn tại trong hệ thống, chúng tôi đã gửi hướng dẫn đặt lại mật khẩu vào hòm thư.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token") != null ? body.get("token").trim() : null;
        String newPassword = body.get("newPassword");

        if (token == null || token.isBlank() || newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body("Dữ liệu yêu cầu không hợp lệ.");
        }

        if (newPassword.length() < 8) {
            return ResponseEntity.badRequest().body("Mật khẩu mới phải có tối thiểu 8 ký tự.");
        }

        boolean success = passwordResetService.resetPassword(token, newPassword);
        if (!success) {
            return ResponseEntity.badRequest().body("Liên kết đặt lại mật khẩu không hợp lệ hoặc đã hết hạn.");
        }

        return ResponseEntity.ok("Mật khẩu đã được cập nhật thành công.");
    }
}
