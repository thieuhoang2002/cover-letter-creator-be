package cover.letter.creator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import cover.letter.creator.model.PasswordResetToken;
import cover.letter.creator.model.User;
import cover.letter.creator.repository.PasswordResetTokenRepository;
import cover.letter.creator.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class PasswordResetService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public String createPasswordResetToken(User user) {
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = tokenRepository.findByUser(user)
            .orElse(new PasswordResetToken());

        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));
        resetToken.setUsed(false);

        tokenRepository.save(resetToken);
        logger.info("Tạo token reset password thành công cho user: {}, token: {}", user.getEmail(), token);
        return token;
    }

    public boolean validatePasswordResetToken(String token) {
        if (token == null || token.isBlank()) return false;
        var optionalToken = tokenRepository.findByToken(token.trim());
        if (optionalToken.isEmpty()) return false;

        PasswordResetToken resetToken = optionalToken.get();
        LocalDateTime nowLocal = LocalDateTime.now();
        LocalDateTime nowUtc = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime expiry = resetToken.getExpiryDate();
        boolean isExpired = expiry.isBefore(nowLocal) && expiry.isBefore(nowUtc);

        return !resetToken.isUsed() && !isExpired;
    }

    public boolean resetPassword(String token, String newPassword) {
        if (token == null || token.isBlank()) {
            logger.warn("Yêu cầu reset password với token trống");
            return false;
        }

        var optionalToken = tokenRepository.findByToken(token.trim());
        if (optionalToken.isEmpty()) {
            logger.warn("Không tìm thấy token trong database: [{}]", token.trim());
            return false;
        }

        PasswordResetToken resetToken = optionalToken.get();
        if (resetToken.isUsed()) {
            logger.warn("Token reset password đã được sử dụng trước đó: [{}]", token.trim());
            return false;
        }

        LocalDateTime nowLocal = LocalDateTime.now();
        LocalDateTime nowUtc = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime expiry = resetToken.getExpiryDate();

        // Kiểm tra an toàn timezone: token chỉ coi là hết hạn nếu nhỏ hơn cả giờ Local và giờ UTC
        boolean isExpired = expiry.isBefore(nowLocal) && expiry.isBefore(nowUtc);
        if (isExpired) {
            logger.warn("Token đã hết hạn. Expiry: {}, nowLocal: {}, nowUtc: {}", expiry, nowLocal, nowUtc);
            return false;
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
        logger.info("Đặt lại mật khẩu thành công cho user: {}", user.getEmail());

        return true;
    }
}
