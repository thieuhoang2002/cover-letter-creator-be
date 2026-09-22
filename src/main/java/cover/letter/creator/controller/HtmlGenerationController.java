package cover.letter.creator.controller;

import cover.letter.creator.dto.HtmlCvRequest;
import cover.letter.creator.service.GroqAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class HtmlGenerationController {

    private final GroqAIService groqAIService;

    @Autowired
    public HtmlGenerationController(GroqAIService groqAIService) {
        this.groqAIService = groqAIService;
    }

    @PostMapping("/generate-cv")
    public ResponseEntity<Map<String, String>> generateHtmlCV(@RequestBody HtmlCvRequest request) {
        try {
            String htmlContent = groqAIService.generateHtmlFromRequest(request);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "content", htmlContent
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "healthy",
            "service", "CV Generator API"
        ));
    }

    /** GET /api/ai/queue-status — FE dùng để hiển thị thông tin hàng đợi */
    @GetMapping("/queue-status")
    public ResponseEntity<Map<String, Object>> getQueueStatus() {
        return ResponseEntity.ok(groqAIService.getQueueStatus());
    }
}