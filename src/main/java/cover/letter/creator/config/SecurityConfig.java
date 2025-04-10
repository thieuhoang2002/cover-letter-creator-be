package cover.letter.creator.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import cover.letter.creator.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OidcUserService oidcUserService() {
        OidcUserService oidcUserService = new OidcUserService();
        return oidcUserService;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        	.cors(cors -> cors.configure(http))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/users/profile/register", "/api/users/login", "/api/users/github-login").permitAll() //api quản lý login
                .requestMatchers("api/users/google-login").permitAll() //api login gg
                .requestMatchers("api/templates/all").permitAll() //api xem danh sách template
                .requestMatchers("/api/templates/**").authenticated() //api quản lý template
                .requestMatchers("/api/pdf/**").authenticated() //api xuất pdf
                .requestMatchers("/api/users/**").authenticated() //api quản lý thông tin users
                .requestMatchers("/api/drive/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated() // Các request khác yêu cầu xác thực
            )
            .exceptionHandling(exception -> exception
                    .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)) // Trả về 401 Unauthorized
                )
            .oauth2Login(oauth2 -> oauth2
                    .userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService())) // Cấu hình lấy thông tin người dùng
                    .successHandler((request, response, authentication) -> {
                        DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();
                        String email = oidcUser.getAttribute("email") != null ? oidcUser.getAttribute("email") : oidcUser.getAttribute("login") + "@github.com";
                        String role = "user"; // Gán mặc định, có thể lấy từ DB
                        String token = jwtUtil.generateToken(email, role);
                        response.sendRedirect("http://localhost:5137/auth-callback?token=" + token); // Chuyển hướng về frontend
                    })
                )
            .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userDetailsService), UsernamePasswordAuthenticationFilter.class)
            .formLogin(form -> form.disable()); // Tắt form login mặc định

        return http.build();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}