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

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Groq AI Service với:
 * - Key Rotation: Round-Robin qua nhiều API key, tự động chuyển khi 429
 * - Concurrency Limiting: Semaphore giới hạn 3 luồng Groq đồng thời
 * - Fallback model nếu primary model thất bại
 */
@Service
public class GroqAIService {

    private static final Logger logger = LoggerFactory.getLogger(GroqAIService.class);

    /**
     * API keys dạng comma-separated: api.key=key1,key2,key3,...
     * Cứ thêm dấu phẩy và key mới là xong, không cần sửa code.
     */
    @Value("${api.key}")
    private String rawApiKeys;

    @Value("${groq.model:openai/gpt-oss-120b}")
    private String PRIMARY_MODEL;

    @Value("${groq.fallback-model:llama-3.3-70b-versatile}")
    private String FALLBACK_MODEL;

    /** Số luồng AI tối đa cùng lúc */
    @Value("${groq.max-concurrent:3}")
    private int maxConcurrent;

    /** Timeout (giây) chờ slot trống trong Semaphore */
    @Value("${groq.queue-timeout-seconds:45}")
    private int queueTimeoutSeconds;

    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Danh sách API key sau khi init
    private List<String> apiKeys;

    // Round-robin counter
    private final AtomicInteger keyIndex = new AtomicInteger(0);

    // Concurrency limiter
    private Semaphore semaphore;

    @PostConstruct
    public void init() {
        apiKeys = new ArrayList<>();
        if (rawApiKeys != null && !rawApiKeys.isBlank()) {
            for (String k : rawApiKeys.split(",")) {
                String trimmed = k.trim();
                if (!trimmed.isEmpty()) apiKeys.add(trimmed);
            }
        }

        if (apiKeys.isEmpty()) {
            logger.error("No Groq API keys configured! Set api.key=key1,key2,key3 in application.properties or env var.");
        } else {
            logger.info("GroqAIService initialized with {} API key(s)", apiKeys.size());
        }

        semaphore = new Semaphore(maxConcurrent, true);
        logger.info("Groq concurrency limit: {} concurrent requests, queue timeout: {}s", maxConcurrent, queueTimeoutSeconds);
    }

    /**
     * Lấy key tiếp theo theo Round-Robin
     */
    private String nextKey() {
        if (apiKeys.isEmpty()) return "";
        int idx = Math.abs(keyIndex.getAndIncrement() % apiKeys.size());
        return apiKeys.get(idx);
    }

    /**
     * Chuyển sang key tiếp theo (khi gặp 429)
     */
    private String rotateKey(String currentKey) {
        if (apiKeys.size() <= 1) return currentKey;
        int idx = Math.abs(keyIndex.getAndIncrement() % apiKeys.size());
        String next = apiKeys.get(idx);
        logger.info("Rotating Groq API key (current index → {})", idx);
        return next;
    }

    public String generateHtmlFromRequest(HtmlCvRequest req) {
        if (apiKeys.isEmpty()) {
            throw new RuntimeException("API Key is not configured properly");
        }

        // Acquire semaphore — wait up to queueTimeoutSeconds
        boolean acquired;
        int queueSize = maxConcurrent - semaphore.availablePermits();
        if (queueSize > 0) {
            logger.info("AI queue: {} request(s) ahead, waiting for slot...", semaphore.getQueueLength());
        }
        try {
            acquired = semaphore.tryAcquire(queueTimeoutSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("AI request interrupted while waiting in queue");
        }

        if (!acquired) {
            throw new RuntimeException("Hàng đợi AI hiện đang quá tải. Vui lòng thử lại sau ít phút.");
        }

        try {
            return doGenerate(req);
        } finally {
            semaphore.release();
        }
    }

    /** Trả về thông tin hàng đợi hiện tại (cho FE hiển thị) */
    public Map<String, Object> getQueueStatus() {
        int available = semaphore.availablePermits();
        int waiting = semaphore.getQueueLength();
        return Map.of(
                "maxConcurrent", maxConcurrent,
                "available", available,
                "waiting", waiting,
                "busy", maxConcurrent - available
        );
    }

    private String doGenerate(HtmlCvRequest req) {
        RestTemplate restTemplate = new RestTemplate();
        String prompt = buildPrompt(req);
        String currentKey = nextKey();

        int maxRetries = Math.min(3, apiKeys.size() + 1);
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Bearer " + currentKey);

                Map<String, Object> body = new HashMap<>();
                body.put("model", PRIMARY_MODEL);
                body.put("messages", List.of(Map.of("role", "user", "content", prompt)));
                body.put("temperature", 0.6);

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
                logger.info("Sending request to Groq API with model: {} (Attempt {})", PRIMARY_MODEL, retryCount + 1);

                ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                        API_URL, HttpMethod.POST, entity,
                        new ParameterizedTypeReference<Map<String, Object>>() {}
                );

                if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                    throw new RuntimeException("Failed to generate CV: Invalid response from AI service");
                }

                return extractContent(response.getBody());

            } catch (HttpClientErrorException e) {
                logger.error("HTTP error from Groq API: Status {}, Response {}", e.getStatusCode(), e.getResponseBodyAsString());

                if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS || e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                    logger.warn("Rate limited or unauthorized — rotating API key...");
                    currentKey = rotateKey(currentKey);
                    retryCount++;
                    try { Thread.sleep(1500); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                } else {
                    return tryFallbackModel(req, currentKey, prompt);
                }
            } catch (Exception e) {
                logger.error("Unexpected error generating CV: {}", e.getMessage(), e);
                throw new RuntimeException("Error generating CV: " + e.getMessage(), e);
            }
        }

        return tryFallbackModel(req, currentKey, prompt);
    }

    private String tryFallbackModel(HtmlCvRequest req, String apiKey, String prompt) {
        logger.info("Attempting fallback model on Groq: {}", FALLBACK_MODEL);
        RestTemplate restTemplate = new RestTemplate();

        // Try all keys for fallback
        for (String key : apiKeys) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Bearer " + key);

                Map<String, Object> body = new HashMap<>();
                body.put("model", FALLBACK_MODEL);
                body.put("messages", List.of(Map.of("role", "user", "content", prompt)));

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
                ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                        API_URL, HttpMethod.POST, entity,
                        new ParameterizedTypeReference<Map<String, Object>>() {}
                );

                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    logger.info("Successfully generated CV with fallback model via Groq");
                    return extractContent(response.getBody());
                }
            } catch (Exception e) {
                logger.warn("Fallback model failed with key: {}", e.getMessage());
            }
        }

        throw new RuntimeException("Tất cả mô hình AI và API key đều thất bại. Vui lòng thử lại sau.");
    }

    @SuppressWarnings("unchecked")
    private String extractContent(Map<String, Object> responseBody) {
        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
        if (choices == null || choices.isEmpty()) {
            throw new RuntimeException("No choices in API response");
        }
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        if (message == null || message.get("content") == null) {
            throw new RuntimeException("No message content in API response");
        }
        return message.get("content").toString();
    }

    private String buildPrompt(HtmlCvRequest req) {
        String userDataJson;
        try {
            userDataJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(req.getUserData());
        } catch (Exception e) {
            logger.error("Failed to serialize user data: {}", e.getMessage(), e);
            userDataJson = "{}";
        }

        return String.format("""
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
    }
}
