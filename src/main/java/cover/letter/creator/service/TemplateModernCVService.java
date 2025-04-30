package cover.letter.creator.service;

import cover.letter.creator.model.TemplateModernCV;
import cover.letter.creator.repository.TemplateModernCVRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TemplateModernCVService {
    @Autowired
    private TemplateModernCVRepository templateModernCVRepository;

    public List<TemplateModernCV> getAllTemplates() {
        return templateModernCVRepository.findAll();
    }
    
    public List<TemplateModernCV> getActiveTemplates() {
        return templateModernCVRepository.findByStatus("active"); // Sử dụng phương thức vừa thêm
    }

    public Optional<TemplateModernCV> getTemplateById(Integer id) {
        return templateModernCVRepository.findById(id);
    }

    public TemplateModernCV saveTemplate(TemplateModernCV template) {
        return templateModernCVRepository.save(template);
    }

    public void deleteTemplate(Integer id) {
    	templateModernCVRepository.deleteById(id);
    }
    
    public Optional<TemplateModernCV> getTemplateAndIncreaseView(Integer id) {
        Optional<TemplateModernCV> optionalTemplate = templateModernCVRepository.findById(id);
        optionalTemplate.ifPresent(template -> {
            template.setViews(template.getViews() + 1); // Tăng view
            templateModernCVRepository.save(template); // Lưu lại
        });
        return optionalTemplate;
    }
    
    
    public List<TemplateModernCV> getTopViewedTemplates() {
        return templateModernCVRepository.findTop5ByStatusOrderByViewsDesc("active");
    }


}