package cover.letter.creator.service;

import cover.letter.creator.model.Template;
import cover.letter.creator.repository.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TemplateService {
    @Autowired
    private TemplateRepository templateRepository;

    public List<Template> getAllTemplates() {
        return templateRepository.findAll();
    }
    
    public List<Template> getActiveTemplates() {
        return templateRepository.findByStatus("active"); // Sử dụng phương thức vừa thêm
    }

    public Optional<Template> getTemplateById(Integer id) {
        return templateRepository.findById(id);
    }

    public Template saveTemplate(Template template) {
        return templateRepository.save(template);
    }

    public void deleteTemplate(Integer id) {
        templateRepository.deleteById(id);
    }
}