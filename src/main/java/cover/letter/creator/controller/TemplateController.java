package cover.letter.creator.controller;

import cover.letter.creator.model.Template;
import cover.letter.creator.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/templates")
@CrossOrigin 
public class TemplateController {
    @Autowired
    private TemplateService templateService;

    @GetMapping
    public List<Template> getAllTemplates() {
        return templateService.getAllTemplates();
    }
    
    @GetMapping("/all")
    public List<Template> getTemplatesActive(){
    	return templateService.getActiveTemplates();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Template> getTemplateById(@PathVariable Integer id) {
        Optional<Template> template = templateService.getTemplateAndIncreaseView(id);
        return template.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Template createTemplate(@RequestBody Template template) {
        return templateService.saveTemplate(template);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Template> updateTemplate(@PathVariable Integer id, @RequestBody Template template) {
        Optional<Template> existingTemplate = templateService.getTemplateById(id);
        if (existingTemplate.isPresent()) {
            template.setId(id);
            return ResponseEntity.ok(templateService.saveTemplate(template));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Integer id) {
        if (templateService.getTemplateById(id).isPresent()) {
            templateService.deleteTemplate(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/top-viewed")
    public List<Template> getTopViewedTemplates() {
        return templateService.getTopViewedTemplates();
    }

}