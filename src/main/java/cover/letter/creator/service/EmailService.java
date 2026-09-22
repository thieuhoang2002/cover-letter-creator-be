package cover.letter.creator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @org.springframework.beans.factory.annotation.Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        String baseUrl = frontendUrl != null ? frontendUrl.replaceAll("/+$", "") : "http://localhost:5173";
        String resetUrl = baseUrl + "/reset-password?token=" + resetToken;
    	
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Yêu cầu đặt lại mật khẩu - Cover Letter Creator");
        message.setText("Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.\n\n" +
                        "Hãy nhấp vào liên kết sau để thiết lập mật khẩu mới:\n" + resetUrl + 
                        "\n\nLiên kết này chỉ có hiệu lực trong vòng 30 phút.\n" +
                        "Nếu bạn không yêu cầu đặt lại mật khẩu, xin vui lòng bỏ qua email này để bảo vệ tài khoản.");

        mailSender.send(message);
    }
}
