package cover.letter.creator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cover.letter.creator.model.PasswordResetToken;
import cover.letter.creator.model.User;
import cover.letter.creator.repository.PasswordResetTokenRepository;
import cover.letter.creator.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    public String createPasswordResetToken(User user) {
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        resetToken.setUsed(false);
        tokenRepository.save(resetToken);
        return token;
    }

    public boolean validatePasswordResetToken(String token) {
        var optionalToken = tokenRepository.findByToken(token);
        if (optionalToken.isEmpty()) return false;

        PasswordResetToken resetToken = optionalToken.get();
        return !resetToken.isUsed() && resetToken.getExpiryDate().isAfter(LocalDateTime.now());
    }

    public boolean resetPassword(String token, String newPassword) {
        var optionalToken = tokenRepository.findByToken(token);
        if (optionalToken.isEmpty()) return false;

        PasswordResetToken resetToken = optionalToken.get();
        if (resetToken.isUsed() || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) return false;

        User user = resetToken.getUser();
        user.setPassword(newPassword); // Lưu ý: nên mã hóa mật khẩu ở đây
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        return true;
    }
}
