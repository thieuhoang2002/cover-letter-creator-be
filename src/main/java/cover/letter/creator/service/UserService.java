package cover.letter.creator.service;

import cover.letter.creator.model.User;
import cover.letter.creator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inject PasswordEncoder

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email); // Đảm bảo repository có phương thức này
    }

    public User registerUser(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (user.getRole() == null) {
            user.setRole("user"); // Gán mặc định nếu không có role
        }
        return userRepository.save(user);
    }

    public User updateUser(Integer id, User updatedUser) {
        Optional<User> existingUserOpt = userRepository.findById(id);
        if (!existingUserOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy người dùng với ID: " + id);
        }

        User existingUser = existingUserOpt.get();
        if (updatedUser.getName() != null) {
            existingUser.setName(updatedUser.getName());
        }
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        if (updatedUser.getAddress() != null) {
            existingUser.setAddress(updatedUser.getAddress());
        }
        if (updatedUser.getPhone() != null) {
            existingUser.setPhone(updatedUser.getPhone());
        }
        if (updatedUser.getBirthday() != null) {
            existingUser.setBirthday(updatedUser.getBirthday());
        }
        return userRepository.save(existingUser);
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
    
    public User updateUser(User user) {
        return userRepository.save(user); // Cập nhật user, bao gồm avatar_url
    }
}