package cover.letter.creator.service;

import cover.letter.creator.dto.HtmlCvRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class DeepSeekAIService {

    private static final Logger logger = LoggerFactory.getLogger(DeepSeekAIService.class);

    @Value("${api.key}")
    private String apiKey;

    private final String API_URL = "https://api.deepseek.com/v1/chat/completions";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateHtmlFromRequest(HtmlCvRequest req) {
        logger.info("Loaded API Key: {}", apiKey);
        if (apiKey == null || apiKey.trim().isEmpty()) {
            logger.error("API Key is missing or empty");
            throw new RuntimeException("API Key is not configured properly");
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey.trim());

        String prompt = buildPrompt(req);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "deepseek-chat");
        body.put("messages", List.of(Map.of(
                "role", "user",
                "content", prompt
        )));

        int maxRetries = 3;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
                logger.info("Sending request to DeepSeek API with model: deepseek-chat (Attempt {})", retryCount + 1);
                ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, entity, Map.class);

                if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                    logger.error("Invalid response from DeepSeek API: Status {}, Body {}", response.getStatusCode(), response.getBody());
                    throw new RuntimeException("Failed to generate CV: Invalid response from AI service");
                }

                Map<String, Object> responseBody = response.getBody();
                logger.debug("Response body: {}", responseBody);

                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (choices == null || choices.isEmpty()) {
                    logger.error("No choices in API response: {}", responseBody);
                    return tryFallbackModel(req, headers, prompt);
                }

                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                if (message == null || message.get("content") == null) {
                    logger.error("No message content in API response: {}", responseBody);
                    throw new RuntimeException("Failed to generate CV: No content in message");
                }

                String content = message.get("content").toString();
                logger.info("Successfully generated CV content");
                return content;

            } catch (HttpClientErrorException e) {
                logger.error("HTTP error from DeepSeek API: Status {}, Response {}", e.getStatusCode(), e.getResponseBodyAsString());
                if (e.getStatusCode() == HttpStatus.UNAUTHORIZED && retryCount < maxRetries - 1) {
                    logger.warn("Unauthorized error, retrying... (Attempt {})", retryCount + 1);
                    retryCount++;
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ie) {
                        logger.error("Retry interrupted: {}", ie.getMessage());
                    }
                } else if (e.getStatusCode() == HttpStatus.PAYMENT_REQUIRED) {
                    logger.warn("Payment required, switching to fallback model");
                    return tryFallbackModel(req, headers, prompt);
                } else {
                    throw new RuntimeException("Error generating CV: HTTP error - " + e.getMessage(), e);
                }
            } catch (Exception e) {
                logger.error("Unexpected error generating CV: {}", e.getMessage(), e);
                throw new RuntimeException("Error generating CV: " + e.getMessage(), e);
            }
        }

        throw new RuntimeException("Failed to generate CV after " + maxRetries + " attempts");
    }

    private String tryFallbackModel(HtmlCvRequest req, HttpHeaders headers, String prompt) {
        logger.info("Attempting fallback model: deepseek-reasoner");
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> body = new HashMap<>();
        body.put("model", "deepseek-reasoner");
        body.put("messages", List.of(Map.of(
                "role", "user",
                "content", prompt
        )));

        try {
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            logger.info("Sending request to DeepSeek API with fallback model: deepseek-reasoner");
            ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, entity, Map.class);

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                logger.error("Fallback model failed: Status {}, Body {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("Fallback model failed: Invalid response");
            }

            Map<String, Object> responseBody = response.getBody();
            logger.debug("Fallback response body: {}", responseBody);

            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            if (choices == null || choices.isEmpty()) {
                logger.error("No choices in fallback model response: {}", responseBody);
                throw new RuntimeException("Fallback model failed: No choices in response");
            }

            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            if (message == null || message.get("content") == null) {
                logger.error("No message content in fallback model response: {}", responseBody);
                throw new RuntimeException("Fallback model failed: No content in message");
            }

            logger.info("Successfully generated CV with fallback model");
            return message.get("content").toString();

        } catch (Exception e) {
            logger.error("Fallback model error: {}", e.getMessage(), e);
            throw new RuntimeException("Error generating CV with fallback model: " + e.getMessage(), e);
        }
    }

    private String buildPrompt(HtmlCvRequest req) {
        String userDataJson;
        try {
            userDataJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(req.getUserData());
        } catch (Exception e) {
            logger.error("Failed to serialize user data: {}", e.getMessage(), e);
            userDataJson = "{}";
        }
        
        System.out.print(userDataJson);

        String exampleOutput = """
            Example output:
        	<div>
        	...
        	</div>
            """;

        String prompt = String.format("""
            Hãy tạo một CV thật đẹp (dạng html, tất cả bọc trong thẻ <div>...</div>, yêu cầu nội dung chỉ chứa đựng trong phạm vi an toàn của 1 trang A4 PDF (đảm bảo lúc in ra PDF không bị 2 trang là được), css inline (không sử dụng space-between) và không cần giải thích gì thêm) theo yêu cầu và dữ liệu sau, bạn có thể bổ sung thêm cho CV phong phú, còn đây là dữ liệu bắt buộc:
			- Vị trí ứng tuyển: %s
			- Chủ đề màu sắc: %s
			- Thông tin cá nhân:
            %s

            Chỉ cần trả về dạng html, tất cả bọc trong thẻ <div>...</div>, yêu cầu nội dung chỉ chứa đựng trong phạm vi an toàn của 1 trang A4 PDF (đảm bảo lúc in ra PDF không bị 2 trang là được), không giải thích gì thêm.
            """,
            req.getPosition(),
            req.getTheme(),
            userDataJson
        );

        logger.debug("Generated prompt: {}", prompt);
        System.out.print(prompt);
        return prompt;
    }
}