package cover.letter.creator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import cover.letter.creator.config.JwtUtil;
import cover.letter.creator.service.CustomUserDetailsService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin 
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Lấy UserDetails để truy xuất role
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        String role = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

        // Tạo token với email và role
        String token = jwtUtil.generateToken(loginRequest.getEmail(), role);
        return ResponseEntity.ok(token);
    }
}

class LoginRequest {
    private String email;
    private String password;

    // Getters và Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}