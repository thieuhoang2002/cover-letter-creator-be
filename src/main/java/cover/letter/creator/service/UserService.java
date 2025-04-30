package cover.letter.creator.service;

import cover.letter.creator.dto.UserProfileUpdateRequest;
import cover.letter.creator.model.Template;
import cover.letter.creator.model.User;
import cover.letter.creator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import cover.letter.creator.model.TemplateModernCV;


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
    
    //Hàm của Long
    public User createUser(User user) {
    	user.setPassword(passwordEncoder.encode(user.getPassword()));
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
        if (updatedUser.getAddress() != null) existingUser.setAddress(updatedUser.getAddress());
        if (updatedUser.getPhone() != null) existingUser.setPhone(updatedUser.getPhone());
        if (updatedUser.getBirthday() != null) existingUser.setBirthday(updatedUser.getBirthday());
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

            // Kiểm tra trạng thái hiện tại
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

            userRepository.save(user);  // Cập nhật lại dữ liệu trong entity
            logger.info("User {} favorite toggled successfully", user.getEmail());

        } catch (Exception e) {
            logger.error("Error toggling favorite template {} for user {}: {}", 
                    template != null ? template.getId() : "null", 
                    user != null ? user.getEmail() : "null", 
                    e.getMessage(), e);
            throw e;
        }
    }
    
    //yeu thich mau hien dai
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

        // So sánh mật khẩu cũ
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false; // Mật khẩu cũ không đúng
        }

        // Không cho đổi nếu mật khẩu mới giống mật khẩu cũ
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu mới không được trùng với mật khẩu hiện tại");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    @Transactional
    public User updateUserProfile(String email, UserProfileUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getName() != null) user.setName(request.getName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());
        if (request.getBirthday() != null) user.setBirthday(request.getBirthday());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getSchool() != null) user.setSchool(request.getSchool());
        if (request.getSpecialization() != null) user.setSpecialization(request.getSpecialization());

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

}