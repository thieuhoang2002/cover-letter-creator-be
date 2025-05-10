package cover.letter.creator.service;

import cover.letter.creator.dto.*;
import cover.letter.creator.model.*;
import cover.letter.creator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    @Transactional
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User registerUser(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (user.getRole() == null) {
            user.setRole("user");
        }
        return userRepository.save(user);
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if(user.getRole() == null) {
        	user.setRole("user");
        }
        return userRepository.save(user);
    }

    public User updateUser(Integer id, User updatedUser) {
        Optional<User> existingUserOpt = userRepository.findById(id);
        if (!existingUserOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy người dùng với ID: " + id);
        }

        User existingUser = existingUserOpt.get();
        if (updatedUser.getName() != null) existingUser.setName(updatedUser.getName());
        if (updatedUser.getEmail() != null) existingUser.setEmail(updatedUser.getEmail());
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        if (updatedUser.getRole() != null) existingUser.setRole(updatedUser.getRole());
        if (updatedUser.getAvatarUrl() != null) existingUser.setAvatarUrl(updatedUser.getAvatarUrl());
        if (updatedUser.getAddress() != null) existingUser.setAddress(updatedUser.getAddress());
        if (updatedUser.getPhone() != null) existingUser.setPhone(updatedUser.getPhone());
        if (updatedUser.getBirthday() != null) existingUser.setBirthday(updatedUser.getBirthday());
        if (updatedUser.getSpecialization() != null) existingUser.setSpecialization(updatedUser.getSpecialization());
        return userRepository.save(existingUser);
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void toggleFavoriteTemplate(User user, Template template) {
        try {
            if (user == null || template == null) {
                logger.error("User or Template is null");
                throw new IllegalArgumentException("User and Template cannot be null");
            }

            boolean isFavorite = entityManager.createQuery(
                    "SELECT COUNT(t) > 0 FROM User u JOIN u.lovedTemplates t WHERE u.id = :userId AND t.id = :templateId",
                    Boolean.class)
                    .setParameter("userId", user.getId())
                    .setParameter("templateId", template.getId())
                    .getSingleResult();

            if (isFavorite) {
                user.getLovedTemplates().remove(template);
                logger.info("Removed template {} from favorites for user {}", template.getId(), user.getEmail());
            } else {
                user.getLovedTemplates().add(template);
                logger.info("Added template {} to favorites for user {}", template.getId(), user.getEmail());
            }

            userRepository.save(user);
            logger.info("User {} favorite toggled successfully", user.getEmail());
        } catch (Exception e) {
            logger.error("Error toggling favorite template {} for user {}: {}", 
                    template != null ? template.getId() : "null", 
                    user != null ? user.getEmail() : "null", 
                    e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public void toggleFavoriteModernTemplate(User user, TemplateModernCV modernTemplate) {
        try {
            if (user == null || modernTemplate == null) {
                logger.error("User or Modern Template is null");
                throw new IllegalArgumentException("User and Modern Template cannot be null");
            }

            boolean isFavorite = entityManager.createQuery(
                    "SELECT COUNT(t) > 0 FROM User u JOIN u.lovedTemplatesModern t WHERE u.id = :userId AND t.id = :templateId",
                    Boolean.class)
                    .setParameter("userId", user.getId())
                    .setParameter("templateId", modernTemplate.getId())
                    .getSingleResult();

            if (isFavorite) {
                user.getLovedTemplatesModern().remove(modernTemplate);
                logger.info("Removed modern template {} from favorites for user {}", modernTemplate.getId(), user.getEmail());
            } else {
                user.getLovedTemplatesModern().add(modernTemplate);
                logger.info("Added modern template {} to favorites for user {}", modernTemplate.getId(), user.getEmail());
            }

            userRepository.save(user);
            logger.info("User {} favorite modern template toggled successfully", user.getEmail());
        } catch (Exception e) {
            logger.error("Error toggling modern template {} for user {}: {}",
                    modernTemplate != null ? modernTemplate.getId() : "null",
                    user != null ? user.getEmail() : "null",
                    e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public boolean changePassword(String email, String oldPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu mới không được trùng với mật khẩu hiện tại");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

//    @Transactional
//    public User updateUserProfile(String email, UserProfileUpdateRequest request) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        if (request.getName() != null) user.setName(request.getName());
//        if (request.getEmail() != null) user.setEmail(request.getEmail());
//        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());
//        if (request.getBirthday() != null) user.setBirthday(request.getBirthday());
//        if (request.getAddress() != null) user.setAddress(request.getAddress());
//        if (request.getPhone() != null) user.setPhone(request.getPhone());
//        if (request.getSpecialization() != null) user.setSpecialization(request.getSpecialization());
//
//        return userRepository.save(user);
//    }
    
    @Transactional
    public User updateUserProfile(String email, UserProfileUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Thông tin cơ bản
        if (request.getName() != null) user.setName(request.getName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());
        if (request.getBirthday() != null) user.setBirthday(request.getBirthday());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getSpecialization() != null) user.setSpecialization(request.getSpecialization());

        // Xử lý skills
        if (request.getSkills() != null) {
            if (request.getSkills().isEmpty()) {
                user.getSkills().clear();
            } else {
                user.getSkills().removeIf(existingSkill -> 
                    request.getSkills().stream().noneMatch(dto -> dto.getId() != null && dto.getId().equals(existingSkill.getId()))
                );

                for (SkillDTO dto : request.getSkills()) {
                    Skill skill = user.getSkills().stream()
                        .filter(s -> s.getId() != null && s.getId().equals(dto.getId()))
                        .findFirst()
                        .orElse(new Skill());
                    skill.setName(dto.getName());
                    skill.setUser(user);
                    if (skill.getId() == null) {
                        user.getSkills().add(skill);
                    }
                }
            }
        }

        // Xử lý experiences
        if (request.getExperiences() != null) {
            if (request.getExperiences().isEmpty()) {
                user.getExperiences().clear();
            } else {
                user.getExperiences().removeIf(existingExp -> 
                    request.getExperiences().stream().noneMatch(dto -> dto.getId() != null && dto.getId().equals(existingExp.getId()))
                );

                for (ExperienceDTO dto : request.getExperiences()) {
                    Experience exp = user.getExperiences().stream()
                        .filter(e -> e.getId() != null && e.getId().equals(dto.getId()))
                        .findFirst()
                        .orElse(new Experience());
                    exp.setCompany(dto.getCompany());
                    exp.setRole(dto.getRole());
                    exp.setTime(dto.getTime());
                    exp.setDescription(dto.getDescription());
                    exp.setUser(user);
                    if (exp.getId() == null) {
                        user.getExperiences().add(exp);
                    }
                }
            }
        }

        // Xử lý educations
        if (request.getEducations() != null) {
            if (request.getEducations().isEmpty()) {
                user.getEducations().clear();
            } else {
                user.getEducations().removeIf(existingEdu -> 
                    request.getEducations().stream().noneMatch(dto -> dto.getId() != null && dto.getId().equals(existingEdu.getId()))
                );

                for (EducationDTO dto : request.getEducations()) {
                    Education edu = user.getEducations().stream()
                        .filter(e -> e.getId() != null && e.getId().equals(dto.getId()))
                        .findFirst()
                        .orElse(new Education());
                    edu.setSchool(dto.getSchool());
                    edu.setDegree(dto.getDegree());
                    edu.setFieldOfStudy(dto.getFieldOfStudy());
                    edu.setTime(dto.getTime());
                    edu.setUser(user);
                    if (edu.getId() == null) {
                        user.getEducations().add(edu);
                    }
                }
            }
        }

        // Xử lý certificates
        if (request.getCertificates() != null) {
            if (request.getCertificates().isEmpty()) {
                user.getCertificates().clear();
            } else {
                user.getCertificates().removeIf(existingCert -> 
                    request.getCertificates().stream().noneMatch(dto -> dto.getId() != null && dto.getId().equals(existingCert.getId()))
                );

                for (CertificateDTO dto : request.getCertificates()) {
                    Certificate cert = user.getCertificates().stream()
                        .filter(c -> c.getId() != null && c.getId().equals(dto.getId()))
                        .findFirst()
                        .orElse(new Certificate());
                    cert.setName(dto.getName());
                    cert.setIssuer(dto.getIssuer());
                    cert.setIssueDate(dto.getIssueDate());
                    cert.setUser(user);
                    if (cert.getId() == null) {
                        user.getCertificates().add(cert);
                    }
                }
            }
        }

        // Xử lý hobbies
        if (request.getHobbies() != null) {
            if (request.getHobbies().isEmpty()) {
                user.getHobbies().clear();
            } else {
                user.getHobbies().removeIf(existingHobby -> 
                    request.getHobbies().stream().noneMatch(dto -> dto.getId() != null && dto.getId().equals(existingHobby.getId()))
                );

                for (HobbyDTO dto : request.getHobbies()) {
                    Hobby hobby = user.getHobbies().stream()
                        .filter(h -> h.getId() != null && h.getId().equals(dto.getId()))
                        .findFirst()
                        .orElse(new Hobby());
                    hobby.setName(dto.getName());
                    hobby.setUser(user);
                    if (hobby.getId() == null) {
                        user.getHobbies().add(hobby);
                    }
                }
            }
        }

        return userRepository.save(user);
    }

    @Transactional
    public void changePasswordWithoutOld(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu mới không được trùng với mật khẩu hiện tại");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // Phương thức mới để lấy thông tin người dùng kèm các thực thể liên quan
    @Transactional
    public UserProfileDTO getUserProfileWithDetails(String email) {
    	User user = userRepository.findByEmailWithDetails(email)
    	        .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + email));

        // Debug
//        System.out.println("Email User: " + user.getEmail());
//        System.out.println("ID User: " + user.getId());
//        System.out.println("Skills count: " + user.getSkills().size());
//        System.out.println("Experiences count: " + user.getExperiences().size());
//        System.out.println("Educations count: " + user.getEducations().size());
//        System.out.println("Certificates count: " + user.getCertificates().size());
//        System.out.println("Hobbies count: " + user.getHobbies().size());
//        System.out.println("Loved Templates count: " + user.getLovedTemplates().size());
//        System.out.println("Loved Modern Templates count: " + user.getLovedTemplatesModern().size());
//        
//        System.out.print("TOI DAY ROI");
        
        // Chuyển đổi sang DTO
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setRole(user.getRole());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setBirthday(user.getBirthday());
        dto.setAddress(user.getAddress());
        dto.setPhone(user.getPhone());
        dto.setSpecialization(user.getSpecialization());

        // Sao chép và chuyển đổi Skills
        Set<Skill> skillsCopy = new HashSet<>(user.getSkills());
        Set<SkillDTO> skillDTOs = skillsCopy.stream().map(skill -> {
            SkillDTO skillDTO = new SkillDTO();
            skillDTO.setId(skill.getId());
            skillDTO.setName(skill.getName());
            return skillDTO;
        }).collect(Collectors.toSet());
        dto.setSkills(skillDTOs);

        // Sao chép và chuyển đổi Experiences
        Set<Experience> experiencesCopy = new HashSet<>(user.getExperiences());
        Set<ExperienceDTO> experienceDTOs = experiencesCopy.stream().map(exp -> {
            ExperienceDTO expDTO = new ExperienceDTO();
            expDTO.setId(exp.getId());
            expDTO.setCompany(exp.getCompany());
            expDTO.setRole(exp.getRole());
            expDTO.setTime(exp.getTime());
            expDTO.setDescription(exp.getDescription());
            return expDTO;
        }).collect(Collectors.toSet());
        dto.setExperiences(experienceDTOs);

        // Sao chép và chuyển đổi Educations
        Set<Education> educationsCopy = new HashSet<>(user.getEducations());
        Set<EducationDTO> educationDTOs = educationsCopy.stream().map(edu -> {
            EducationDTO eduDTO = new EducationDTO();
            eduDTO.setId(edu.getId());
            eduDTO.setSchool(edu.getSchool());
            eduDTO.setDegree(edu.getDegree());
            eduDTO.setFieldOfStudy(edu.getFieldOfStudy());
            eduDTO.setTime(edu.getTime());
            return eduDTO;
        }).collect(Collectors.toSet());
        dto.setEducations(educationDTOs);

        // Sao chép và chuyển đổi Certificates
        Set<Certificate> certificatesCopy = new HashSet<>(user.getCertificates());
        Set<CertificateDTO> certificateDTOs = certificatesCopy.stream().map(cert -> {
            CertificateDTO certDTO = new CertificateDTO();
            certDTO.setId(cert.getId());
            certDTO.setName(cert.getName());
            certDTO.setIssuer(cert.getIssuer());
            certDTO.setIssueDate(cert.getIssueDate());
            return certDTO;
        }).collect(Collectors.toSet());
        dto.setCertificates(certificateDTOs);

        // Sao chép và chuyển đổi Hobbies
        Set<Hobby> hobbiesCopy = new HashSet<>(user.getHobbies());
        Set<HobbyDTO> hobbyDTOs = hobbiesCopy.stream().map(hobby -> {
            HobbyDTO hobbyDTO = new HobbyDTO();
            hobbyDTO.setId(hobby.getId());
            hobbyDTO.setName(hobby.getName());
            return hobbyDTO;
        }).collect(Collectors.toSet());
        dto.setHobbies(hobbyDTOs);
        
     // Sao chép và chuyển đổi Loved Templates
        Set<Template> lovedTemplates = user.getLovedTemplates();
        Set<TemplateDTO> lovedTemplateDTOs = (lovedTemplates != null && !lovedTemplates.isEmpty())
            ? lovedTemplates.stream().map(template -> {
                TemplateDTO templateDTO = new TemplateDTO();
                templateDTO.setId(template.getId());
                templateDTO.setName(template.getName());
                templateDTO.setType(template.getType());
                templateDTO.setContent(template.getContent());
                templateDTO.setImage(template.getImage());
                templateDTO.setViews(template.getViews());
                templateDTO.setUpdateDate(template.getUpdateDate());
                templateDTO.setStatus(template.getStatus());
                templateDTO.setFavorite(template.isFavorite()); // Đảm bảo ánh xạ trạng thái yêu thích
                return templateDTO;
            }).collect(Collectors.toSet())
            : new HashSet<>();

        dto.setLovedTemplates(lovedTemplateDTOs);

        // Sao chép và chuyển đổi Loved Modern Templates
        Set<TemplateModernCV> lovedModernTemplates = user.getLovedTemplatesModern();
        Set<TemplateModernCVDTO> lovedModernTemplateDTOs = (lovedModernTemplates != null && !lovedModernTemplates.isEmpty())
            ? lovedModernTemplates.stream().map(modernTemplate -> {
                TemplateModernCVDTO modernTemplateDTO = new TemplateModernCVDTO();
                modernTemplateDTO.setId(modernTemplate.getId());
                modernTemplateDTO.setName(modernTemplate.getName());
                modernTemplateDTO.setType(modernTemplate.getType());
                modernTemplateDTO.setContent(modernTemplate.getContent());
                modernTemplateDTO.setImage(modernTemplate.getImage());
                modernTemplateDTO.setViews(modernTemplate.getViews());
                modernTemplateDTO.setUpdateDate(modernTemplate.getUpdateDate());
                modernTemplateDTO.setStatus(modernTemplate.getStatus());
                modernTemplateDTO.setFavorite(modernTemplate.isFavorite()); // Đảm bảo ánh xạ trạng thái yêu thích
                return modernTemplateDTO;
            }).collect(Collectors.toSet())
            : new HashSet<>();

        dto.setLovedModernTemplates(lovedModernTemplateDTOs);

        return dto;
    }

    // Phương thức để thêm Skill
    @Transactional
    public Skill addSkill(Integer userId, Skill skill) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));
        skill.setUser(user);
        user.getSkills().add(skill);
        userRepository.save(user);
        return skill;
    }

    // Phương thức để xóa Skill
    @Transactional
    public void removeSkill(Integer userId, Integer skillId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));
        Skill skillToRemove = user.getSkills().stream()
                .filter(skill -> skill.getId().equals(skillId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kỹ năng với ID: " + skillId));
        user.getSkills().remove(skillToRemove);
        userRepository.save(user);
    }

    // Phương thức để thêm Experience
    @Transactional
    public Experience addExperience(Integer userId, Experience experience) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));
        experience.setUser(user);
        user.getExperiences().add(experience);
        userRepository.save(user);
        return experience;
    }

    // Phương thức để xóa Experience
    @Transactional
    public void removeExperience(Integer userId, Integer experienceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));
        Experience experienceToRemove = user.getExperiences().stream()
                .filter(exp -> exp.getId().equals(experienceId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kinh nghiệm với ID: " + experienceId));
        user.getExperiences().remove(experienceToRemove);
        userRepository.save(user);
    }

    // Phương thức để thêm Education
    @Transactional
    public Education addEducation(Integer userId, Education education) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));
        education.setUser(user);
        user.getEducations().add(education);
        userRepository.save(user);
        return education;
    }

    // Phương thức để xóa Education
    @Transactional
    public void removeEducation(Integer userId, Integer educationId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));
        Education educationToRemove = user.getEducations().stream()
                .filter(edu -> edu.getId().equals(educationId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học vấn với ID: " + educationId));
        user.getEducations().remove(educationToRemove);
        userRepository.save(user);
    }

    // Phương thức để thêm Certificate
    @Transactional
    public Certificate addCertificate(Integer userId, Certificate certificate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));
        certificate.setUser(user);
        user.getCertificates().add(certificate);
        userRepository.save(user);
        return certificate;
    }

    // Phương thức để xóa Certificate
    @Transactional
    public void removeCertificate(Integer userId, Integer certificateId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));
        Certificate certificateToRemove = user.getCertificates().stream()
                .filter(cert -> cert.getId().equals(certificateId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chứng chỉ với ID: " + certificateId));
        user.getCertificates().remove(certificateToRemove);
        userRepository.save(user);
    }

    // Phương thức để thêm Hobby
    @Transactional
    public Hobby addHobby(Integer userId, Hobby hobby) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));
        hobby.setUser(user);
        user.getHobbies().add(hobby);
        userRepository.save(user);
        return hobby;
    }

    // Phương thức để xóa Hobby
    @Transactional
    public void removeHobby(Integer userId, Integer hobbyId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));
        Hobby hobbyToRemove = user.getHobbies().stream()
                .filter(hobby -> hobby.getId().equals(hobbyId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sở thích với ID: " + hobbyId));
        user.getHobbies().remove(hobbyToRemove);
        userRepository.save(user);
    }
}