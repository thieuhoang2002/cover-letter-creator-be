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
}
