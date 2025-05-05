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
public class OpenRouterAIService {

    private static final Logger logger = LoggerFactory.getLogger(OpenRouterAIService.class);

    @Value("${api.key}")
    private String apiKey;

    private final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateHtmlFromRequest(HtmlCvRequest req) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("HTTP-Referer", "http://localhost:8080");
        headers.set("X-Title", "CV Generator");

        String prompt = buildPrompt(req);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "openai/gpt-4-turbo");
        body.put("messages", List.of(Map.of(
                "role", "user",
                "content", prompt
        )));

        try {
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            logger.info("Sending request to OpenRouter AI API with model: openai/gpt-4-turbo");
            ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, entity, Map.class);

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                logger.error("Invalid response from OpenRouter AI: Status {}, Body {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("Failed to generate CV: Invalid response from AI service");
            }

            Map<String, Object> responseBody = response.getBody();
            logger.debug("Response body: {}", responseBody);

            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            if (choices == null || choices.isEmpty()) {
                logger.error("No choices in API response: {}", responseBody);
                // Fallback to a different model
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
            logger.error("HTTP error from OpenRouter AI: Status {}, Response {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error generating CV: HTTP error - " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error generating CV: {}", e.getMessage(), e);
            throw new RuntimeException("Error generating CV: " + e.getMessage(), e);
        }
    }

    private String tryFallbackModel(HtmlCvRequest req, HttpHeaders headers, String prompt) {
        logger.info("Attempting fallback model: openai/gpt-3.5-turbo");
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> body = new HashMap<>();
        body.put("model", "openai/gpt-3.5-turbo");
        body.put("messages", List.of(Map.of(
                "role", "user",
                "content", prompt
        )));

        try {
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, entity, Map.class);

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                logger.error("Fallback model failed: Status {}, Body {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("Fallback model failed: Invalid response");
            }

            Map<String, Object> responseBody = response.getBody();
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

        String prompt = String.format("""
            Create a modern HTML CV based on the following JSON data:

            ```json
            %s
            ```

            Requirements:
            - Layout: %s
            - Font: %s
            - Style: %s
            - Theme: %s
            - Position Applied: %s
            - Output Format: %s
            - Preserve Placeholders: %s

            Output only the HTML with inline CSS, no additional explanations.
            """,
            userDataJson,
            req.getLayout(),
            req.getFont(),
            req.getStyles(),
            req.getTheme(),
            req.getPosition(),
            req.getResponseFormat(),
            String.join(", ", req.getPlaceholders())
        );

        logger.debug("Generated prompt: {}", prompt);
        return prompt;
    }
}