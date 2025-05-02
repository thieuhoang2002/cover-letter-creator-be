package cover.letter.creator.service;

import cover.letter.creator.dto.HtmlCvRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OpenRouterAIService {

    @Value("${api.key}")
    private String apiKey;

    private final String API_URL = "https://openrouter.ai/api/v1/chat/completions";

    public String generateHtmlFromRequest(HtmlCvRequest req) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("HTTP-Referer", "http://localhost:8080");
        headers.set("X-Title", "CV Generator");

        // Chuyển request object thành prompt tiếng Anh phù hợp
        String prompt = buildPrompt(req);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "openai/gpt-3.5-turbo");
        body.put("messages", List.of(Map.of(
                "role", "user",
                "content", prompt
        )));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, entity, Map.class);

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

        return message.get("content").toString();
    }

    private String buildPrompt(HtmlCvRequest req) {
        return String.format("""
            Tôi cần bạn tạo một mẫu CV **HTML hiện đại** với các thông tin sau:
            
            ### 🎯 MỤC TIÊU
            - Tạo một CV bố cục **%s** với font chữ chính là **%s**.
            - Sử dụng **%s**, không file CSS riêng.
            - Áp dụng tông màu tối (ví dụ: xanh dương gradient → vàng cam).
            - Đảm bảo **tương thích mobile-first** với `@media` queries.
            
            ### 📋 CẤU TRÚC CV
            1. **Cột trái (40%%)**:
               - Ảnh đại diện được hiển thị ở giữa.
               - Tên đầy đủ, chuyên ngành, vị trí ứng tuyển (in hoa, nổi bật).
               - Danh sách thông tin cá nhân với icon: ✉️, 📞, 📍, 🎂.
            
            2. **Cột phải (60%%)**:
               - Giới thiệu bản thân (1 đoạn).
               - Kinh nghiệm làm việc (liệt kê công ty, vị trí, mô tả công việc).
               - Học vấn & chứng chỉ (chia 2 cột nhỏ trên desktop, xếp dọc trên mobile).
            
            ### 🧪 PLACEHOLDERS
            - Các phần sau đây phải **duy trì dưới dạng placeholder**:
              + `[Ảnh đại diện]` → URL của ảnh.
              + `[Họ và tên]`, `[Chuyên ngành]`, `[Vị trí ứng tuyển]`.
              + `[Email]`, `[Số điện thoại]`, `[Địa chỉ]`, `[Ngày sinh]`.
              + `[Kỹ năng]`, `[Sở thích]` (dưới dạng danh sách `ul/li`).
              + `[Kinh nghiệm làm việc]`, `[Học vấn]`, `[Chứng chỉ]`.
            
            ### 🎨 GIAO DIỆN CHI TIẾT
            - Màu nền chính: **đen tối với gradient cam → vàng**.
            - Mô tả mỗi mục có shadow nhẹ và bo góc mềm mại.
            - Khi hover vào card (kinh nghiệm, học vấn), hiệu ứng zoom nhẹ.
            - Trên mobile: Chuyển bố cục sang **stacked%%**, cột phải xếp dưới.
            
            ### 📄 KẾT QUẢ CẦN TRẢ VỀ
            - Chỉ trả về mã nguồn HTML + inline CSS (không có explanation).
            - Đảm bảo **tính đầy đủ** của cấu trúc (không thiếu section nào).
            - Đặt **[Vị trí ứng tuyển]** ở dòng tiêu đề chính sau tên.
            
            #### 🧱 Ví dụ dữ liệu giả lập:
            - Kỹ năng: [Kỹ năng]
            - Sở thích: [Sở thích]
            - Chứng chỉ: [Chứng chỉ]

            (Theme: %s, Style: %s, Format: %s)
            (Vị trí ứng tuyển: %s)
            Các placeholder: %s
            """,
            req.getLayout(),
            req.getFont(),
            req.getStyles(),
            req.getTheme(),
            req.getStyles(),
            req.getResponse_format(),
            req.getPosition(),
            String.join(", ", req.getPlaceholders())
        );
    }

}
