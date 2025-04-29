package cover.letter.creator.controller;

import cover.letter.creator.config.JwtUtil;
import cover.letter.creator.dto.ChangePasswordRequest;
import cover.letter.creator.dto.ChangePasswordWithoutOldRequest;
import cover.letter.creator.dto.UserProfileUpdateRequest;
import cover.letter.creator.model.Template;
import cover.letter.creator.model.User;
import cover.letter.creator.service.TemplateService;
import cover.letter.creator.service.UserService;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/users/profile")
@CrossOrigin 
public class UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
    @Autowired
    private UserService userService;
    
    @Autowired
    private TemplateService templateService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        return ResponseEntity.ok(registeredUser);
    }
    
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        if (userService.getUserById(id).isPresent()) {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    
//    @GetMapping("/me")
//    public ResponseEntity<User> getCurrentUser(@RequestHeader("Authorization") String token) {
//        try {
//            String jwt = token.replace("Bearer ", "");
//            String email = jwtUtil.extractEmail(jwt);
//            Optional<User> user = userService.getUserByEmail(email);
//            return user.map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//    }
    
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            String email = jwtUtil.extractEmail(jwt);
            
            // Lấy User + Loved Templates
            Optional<User> user = userService.getUserByEmail(email);

            if (user.isPresent()) {
                logger.info("User: {}", user.get().getEmail());
                logger.info("Loved Templates: {}", user.get().getLovedTemplates().size());
                user.get().getLovedTemplates().forEach(t -> logger.info("Template: {}", t.getName()));

                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            logger.error("Error getting current user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    
    
    @PostMapping("/me/love-template/{templateId}")
    public ResponseEntity<String> toggleFavoriteTemplate(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer templateId) {
        try {
            String jwt = token.replace("Bearer ", "");
            String email = jwtUtil.extractEmail(jwt);
            Optional<User> userOpt = userService.getUserByEmail(email);

            if (!userOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            User user = userOpt.get();
            Optional<Template> templateOpt = templateService.getTemplateById(templateId);

            if (!templateOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Template not found");
            }

            Template template = templateOpt.get();
            userService.toggleFavoriteTemplate(user, template);

            return ResponseEntity.ok("Favorite toggled successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error toggling favorite: " + e.getMessage());
        }
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody ChangePasswordRequest request) {

        try {
            String jwt = token.replace("Bearer ", "");
            String email = jwtUtil.extractEmail(jwt);

            boolean success = userService.changePassword(email, request.getOldPassword(), request.getNewPassword());

            return success
                    ? ResponseEntity.ok("Đổi mật khẩu thành công!")
                    : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mật khẩu cũ sai!");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error changing password: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi đổi mật khẩu!");
        }
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateCurrentUserProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody UserProfileUpdateRequest request) {
        try {
            String jwt = token.replace("Bearer ", "");
            String email = jwtUtil.extractEmail(jwt);

            User updatedUser = userService.updateUserProfile(email, request);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            logger.error("Error updating profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/change-password-without-old")
    public ResponseEntity<String> changePasswordWithoutOld(
            @RequestHeader("Authorization") String token,
            @RequestBody ChangePasswordWithoutOldRequest request) {
        try {
            String jwt = token.replace("Bearer ", "");
            String email = jwtUtil.extractEmail(jwt);

            userService.changePasswordWithoutOld(email, request.getNewPassword());

            return ResponseEntity.ok("Đổi mật khẩu thành công!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error changing password without old: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi đổi mật khẩu!");
        }
    }
}

