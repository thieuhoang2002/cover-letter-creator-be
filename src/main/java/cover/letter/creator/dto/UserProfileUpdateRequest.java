package cover.letter.creator.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class UserProfileUpdateRequest {
    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên không được vượt quá 100 ký tự")
    private String name;

    @Email(message = "Email không đúng định dạng")
    private String email;

    private String avatarUrl;
    private Date birthday;

    @Size(max = 200, message = "Địa chỉ không được vượt quá 200 ký tự")
    private String address;

    @Size(max = 20, message = "Số điện thoại không được vượt quá 20 ký tự")
    private String phone;

    @Size(max = 100, message = "Chuyên ngành không được vượt quá 100 ký tự")
    private String specialization;

    private Set<SkillDTO> skills;
    private Set<ExperienceDTO> experiences;
    private Set<EducationDTO> educations;
    private Set<CertificateDTO> certificates;
    private Set<HobbyDTO> hobbies;

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
}
