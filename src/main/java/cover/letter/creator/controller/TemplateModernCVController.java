package cover.letter.creator.controller;

import cover.letter.creator.model.TemplateModernCV;
import cover.letter.creator.service.TemplateModernCVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/templates-modern")
@CrossOrigin
public class TemplateModernCVController {

    @Autowired
    private TemplateModernCVService modernCVService;

    @GetMapping
    public List<TemplateModernCV> getAllTemplates() {
        return modernCVService.getAllTemplates();
    }

    @GetMapping("/all")
    public List<TemplateModernCV> getActiveTemplates() {
        return modernCVService.getActiveTemplates();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TemplateModernCV> getTemplateById(@PathVariable Integer id) {
        Optional<TemplateModernCV> template = modernCVService.getTemplateAndIncreaseView(id);
        return template.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public TemplateModernCV createTemplate(@RequestBody TemplateModernCV template) {
        template.setUpdateDate(LocalDateTime.now());
        return modernCVService.saveTemplate(template);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TemplateModernCV> updateTemplate(@PathVariable Integer id, @RequestBody TemplateModernCV template) {
        Optional<TemplateModernCV> existingTemplateOpt = modernCVService.getTemplateById(id);
        if (existingTemplateOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        TemplateModernCV existingTemplate = existingTemplateOpt.get();

        existingTemplate.setName(template.getName());
        existingTemplate.setType(template.getType());
        existingTemplate.setContent(template.getContent());
        existingTemplate.setImage(template.getImage());
        existingTemplate.setStatus(template.getStatus());
        existingTemplate.setUpdateDate(LocalDateTime.now());

        return ResponseEntity.ok(modernCVService.saveTemplate(existingTemplate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Integer id) {
        if (modernCVService.getTemplateById(id).isPresent()) {
            modernCVService.deleteTemplate(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/top-viewed")
    public List<TemplateModernCV> getTopViewedTemplates() {
        return modernCVService.getTopViewedTemplates();
    }
}
