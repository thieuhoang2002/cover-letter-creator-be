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

    @com.fasterxml.jackson.annotation.JsonProperty("hasPassword")
    private Boolean hasPassword;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public Date getBirthday() { return birthday; }
    public void setBirthday(Date birthday) { this.birthday = birthday; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public Set<SkillDTO> getSkills() { return skills; }
    public void setSkills(Set<SkillDTO> skills) { this.skills = skills; }

    public Set<ExperienceDTO> getExperiences() { return experiences; }
    public void setExperiences(Set<ExperienceDTO> experiences) { this.experiences = experiences; }

    public Set<EducationDTO> getEducations() { return educations; }
    public void setEducations(Set<EducationDTO> educations) { this.educations = educations; }

    public Set<CertificateDTO> getCertificates() { return certificates; }
    public void setCertificates(Set<CertificateDTO> certificates) { this.certificates = certificates; }

    public Set<HobbyDTO> getHobbies() { return hobbies; }
    public void setHobbies(Set<HobbyDTO> hobbies) { this.hobbies = hobbies; }

    public Set<TemplateDTO> getLovedTemplates() { return lovedTemplates; }
    public void setLovedTemplates(Set<TemplateDTO> lovedTemplates) { this.lovedTemplates = lovedTemplates; }

    public Set<TemplateModernCVDTO> getLovedModernTemplates() { return lovedModernTemplates; }
    public void setLovedModernTemplates(Set<TemplateModernCVDTO> lovedModernTemplates) { this.lovedModernTemplates = lovedModernTemplates; }

    public Boolean getHasPassword() { return hasPassword; }
    public void setHasPassword(Boolean hasPassword) { this.hasPassword = hasPassword; }
}
