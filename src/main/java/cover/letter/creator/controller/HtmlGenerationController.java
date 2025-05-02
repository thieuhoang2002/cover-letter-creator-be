package cover.letter.creator.controller;

import cover.letter.creator.dto.HtmlCvRequest;
import cover.letter.creator.service.OpenRouterAIService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class HtmlGenerationController {

    @Autowired
    private OpenRouterAIService openRouterAiService;

    @PostMapping("/generate-cv")
    public ResponseEntity<Map<String, String>> generateHtmlCV(@RequestBody HtmlCvRequest request) {
        String htmlContent = openRouterAiService.generateHtmlFromRequest(request);
        return ResponseEntity.ok(Map.of("content", htmlContent));
    }
}
