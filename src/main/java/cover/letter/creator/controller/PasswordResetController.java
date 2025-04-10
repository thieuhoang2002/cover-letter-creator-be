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
        var userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Email không tồn tại");
        }

        String token = passwordResetService.createPasswordResetToken(userOptional.get());
        emailService.sendPasswordResetEmail(email, token);

        return ResponseEntity.ok("Đã gửi email khôi phục mật khẩu.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
    	String token = body.get("token");
    	String newPassword = body.get("newPassword");
        boolean success = passwordResetService.resetPassword(token, newPassword);
        if (!success) return ResponseEntity.badRequest().body("Token không hợp lệ hoặc đã hết hạn");

        return ResponseEntity.ok("Mật khẩu đã được cập nhật");
    }
}
