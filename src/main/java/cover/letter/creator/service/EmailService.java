package cover.letter.creator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        String resetUrl = "http://localhost:5173/reset-password?token=" + resetToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Yêu cầu đặt lại mật khẩu");
        message.setText("Chúng tôi nhận được yêu cầu đặt lại mật khẩu của bạn.\n\n" +
                        "Hãy nhấp vào liên kết sau để đặt lại mật khẩu:\n" + resetUrl + 
                        "\n\nLiên kết này sẽ hết hạn sau 1 giờ.\n" +
                        "Nếu bạn không yêu cầu, hãy bỏ qua email này.");

        mailSender.send(message);
    }
}
