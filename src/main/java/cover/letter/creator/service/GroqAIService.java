package cover.letter.creator.service;

import cover.letter.creator.dto.HtmlCvRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GroqAIService {

    private static final Logger logger = LoggerFactory.getLogger(GroqAIService.class);

    @Value("${api.key}")
    private String apiKey;

    @Value("${groq.model:openai/gpt-oss-120b}")
    private String PRIMARY_MODEL;

    @Value("${groq.fallback-model:llama-3.3-70b-versatile}")
    private String FALLBACK_MODEL;

    // Groq OpenAI-compatible Chat Completions endpoint
    private final String API_URL = "https://api.groq.com/openai/v1/chat/completions";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateHtmlFromRequest(HtmlCvRequest req) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            logger.error("Groq API Key is missing or empty");
            throw new RuntimeException("API Key is not configured properly");
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey.trim());

        String prompt = buildPrompt(req);

        Map<String, Object> body = new HashMap<>();
        body.put("model", PRIMARY_MODEL);
        body.put("messages", List.of(Map.of(
                "role", "user",
                "content", prompt
        )));
        body.put("temperature", 0.6);

        int maxRetries = 3;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
                logger.info("Sending request to Groq API with model: {} (Attempt {})", PRIMARY_MODEL, retryCount + 1);

                ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                        API_URL,
                        HttpMethod.POST,
                        entity,
                        new ParameterizedTypeReference<Map<String, Object>>() {}
                );

                if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                    logger.error("Invalid response from Groq API: Status {}, Body {}", response.getStatusCode(), response.getBody());
                    throw new RuntimeException("Failed to generate CV: Invalid response from AI service");
                }

                Map<String, Object> responseBody = response.getBody();
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (choices == null || choices.isEmpty()) {
                    logger.error("No choices in API response: {}", responseBody);
                    return tryFallbackModel(req, headers, prompt);
                }

                @SuppressWarnings("unchecked")
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                if (message == null || message.get("content") == null) {
                    logger.error("No message content in API response: {}", responseBody);
                    throw new RuntimeException("Failed to generate CV: No content in message");
                }

                String content = message.get("content").toString();
                logger.info("Successfully generated CV content via Groq");
                return content;

            } catch (HttpClientErrorException e) {
                logger.error("HTTP error from Groq API: Status {}, Response {}", e.getStatusCode(), e.getResponseBodyAsString());
                if ((e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) && retryCount < maxRetries - 1) {
                    logger.warn("Rate limited or unauthorized error, retrying... (Attempt {})", retryCount + 1);
                    retryCount++;
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ie) {
                        logger.error("Retry interrupted: {}", ie.getMessage());
                    }
                } else {
                    return tryFallbackModel(req, headers, prompt);
                }
            } catch (Exception e) {
                logger.error("Unexpected error generating CV: {}", e.getMessage(), e);
                throw new RuntimeException("Error generating CV: " + e.getMessage(), e);
            }
        }

        throw new RuntimeException("Failed to generate CV after " + maxRetries + " attempts");
    }

    private String tryFallbackModel(HtmlCvRequest req, HttpHeaders headers, String prompt) {
        logger.info("Attempting fallback model on Groq: {}", FALLBACK_MODEL);
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> body = new HashMap<>();
        body.put("model", FALLBACK_MODEL);
        body.put("messages", List.of(Map.of(
                "role", "user",
                "content", prompt
        )));

        try {
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    API_URL,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                logger.error("Fallback model failed: Status {}, Body {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("Fallback model failed: Invalid response");
            }

            Map<String, Object> responseBody = response.getBody();
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            if (choices == null || choices.isEmpty()) {
                logger.error("No choices in fallback model response: {}", responseBody);
                throw new RuntimeException("Fallback model failed: No choices in response");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            if (message == null || message.get("content") == null) {
                logger.error("No message content in fallback model response: {}", responseBody);
                throw new RuntimeException("Fallback model failed: No content in message");
            }

            logger.info("Successfully generated CV with fallback model via Groq");
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

        String prompt = String.format("""
            Hãy tạo một CV thật đẹp (dạng html, tất cả bọc trong thẻ <div>...</div>, yêu cầu nội dung chỉ chứa đựng trong phạm vi an toàn của 1 trang A4 PDF (đảm bảo lúc in ra PDF không bị 2 trang là được), css inline (không sử dụng space-between, flex-wrap, column-gap, row-gap) và không cần giải thích gì thêm) theo yêu cầu và dữ liệu sau, bạn có thể bổ sung thêm cho CV phong phú, còn đây là dữ liệu bắt buộc:
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

        return prompt;
    }
}
