package cover.letter.creator.controller;

import cover.letter.creator.dto.HtmlCvRequest;
import cover.letter.creator.service.DeepSeekAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class HtmlGenerationController {

    private final DeepSeekAIService deepSeekAIService;

    @Autowired
    public HtmlGenerationController(DeepSeekAIService deepSeekAIService) {
        this.deepSeekAIService = deepSeekAIService;
    }

    @PostMapping("/generate-cv")
    public ResponseEntity<Map<String, String>> generateHtmlCV(@RequestBody HtmlCvRequest request) {
        try {
            String htmlContent = deepSeekAIService.generateHtmlFromRequest(request);
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
}