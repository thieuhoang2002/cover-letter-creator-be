package cover.letter.creator.dto;

import lombok.Data;
import java.util.Date;
import java.util.Set;

@Data
public class UserProfileDTO {
    private Integer id;
    private String role;
    private String name;
    private String email;
    private String avatarUrl;
    private Date birthday;
    private String address;
    private String phone;
    private String specialization;
    private Set<SkillDTO> skills;
    private Set<ExperienceDTO> experiences;
    private Set<EducationDTO> educations;
    private Set<CertificateDTO> certificates;
    private Set<HobbyDTO> hobbies;
    private Set<TemplateDTO> lovedTemplates;
    private Set<TemplateModernCVDTO> lovedModernTemplates;
}
