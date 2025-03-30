package cover.letter.creator.service;

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

//    @Transactional
//    public void toggleFavoriteTemplate(User user, Template template) {
//        try {
//            if (user == null) {
//                logger.error("User is null");
//                throw new IllegalArgumentException("User cannot be null");
//            }
//            if (template == null) {
//                logger.error("Template is null");
//                throw new IllegalArgumentException("Template cannot be null");
//            }
//
//            // Kiểm tra xem template đã là yêu thích chưa
//            boolean isFavorite = entityManager.createQuery(
//                    "SELECT COUNT(*) > 0 FROM User u JOIN u.lovedTemplates t WHERE u.id = :userId AND t.id = :templateId",
//                    Boolean.class)
//                    .setParameter("userId", user.getId())
//                    .setParameter("templateId", template.getId())
//                    .getSingleResult();
//
//            if (isFavorite) {
//                // Xóa khỏi bảng user_loved_templates
//                entityManager.createNativeQuery(
//                        "DELETE FROM user_loved_templates WHERE user_id = :userId AND template_id = :templateId")
//                        .setParameter("userId", user.getId())
//                        .setParameter("templateId", template.getId())
//                        .executeUpdate();
//                logger.info("Removed template {} from favorites for user {}", template.getId(), user.getEmail());
//            } else {
//                // Thêm vào bảng user_loved_templates
//                entityManager.createNativeQuery(
//                        "INSERT INTO user_loved_templates (user_id, template_id) VALUES (:userId, :templateId)")
//                        .setParameter("userId", user.getId())
//                        .setParameter("templateId", template.getId())
//                        .executeUpdate();
//                logger.info("Added template {} to favorites for user {}", template.getId(), user.getEmail());
//            }
//
//            // Không cần gọi save vì chúng ta đã thao tác trực tiếp trên database
//            logger.info("User {} favorite toggled successfully", user.getEmail());
//        } catch (Exception e) {
//            logger.error("Error toggling favorite template {} for user {}: {}", 
//                    template != null ? template.getId() : "null", 
//                    user != null ? user.getEmail() : "null", 
//                    e.getMessage(), e);
//            throw e;
//        }
//    }
    
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

    
}