package cover.letter.creator.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import cover.letter.creator.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private RateLimitingFilter rateLimitingFilter;

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
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(
            "http://localhost:5173",
            "http://localhost:3000",
            "http://localhost:8080",
            "https://*.vercel.app",
            "https://cover-letter-creator-fe.vercel.app"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Content-Disposition", "Authorization", "Retry-After"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        	.cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 1. Public Authentication & Health endpoints
                .requestMatchers("/api/users/profile/register", "/api/users/login", "/api/users/github-login", "/api/users/google-login").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/ai/health").permitAll()

                // 2. Public Read-only Templates (Anyone can view lists & previews)
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/templates/**", "/api/templates-modern/**").permitAll()

                // 3. Admin ONLY Operations on Templates (Create / Edit / Delete)
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/templates/**", "/api/templates-modern/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/templates/**", "/api/templates-modern/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/templates/**", "/api/templates-modern/**").hasRole("ADMIN")

                // 4. Admin ONLY User Management
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/users/profile").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/users/profile").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/users/profile/**").hasRole("ADMIN")

                // 5. User Self-Service Operations (Profile & Password Change & Favorites & Avatar)
                .requestMatchers("/api/users/profile/me", "/api/users/profile/me/**").hasAnyRole("USER", "ADMIN", "VIP")
                .requestMatchers("/api/users/profile/change-password", "/api/users/profile/change-password-without-old", "/api/users/profile/has-password").hasAnyRole("USER", "ADMIN", "VIP")

                // 6. User Operations (PDF Export, AI, Follow CV, VIP)
                .requestMatchers("/api/pdf/**", "/api/modern-cv/pdf/**", "/api/ai-cv/pdf/**").hasAnyRole("USER", "ADMIN", "VIP")
                .requestMatchers("/api/ai/**").hasAnyRole("USER", "ADMIN", "VIP")
                .requestMatchers("/api/follow-cv/**").hasAnyRole("USER", "ADMIN", "VIP")
                .requestMatchers("/api/vip/**").hasAnyRole("USER", "ADMIN", "VIP")

                // 7. Admin ONLY — VIP Management
                .requestMatchers("/api/admin/vip-requests/**").hasRole("ADMIN")

                // 8. Any other request requires authentication
                .anyRequest().authenticated()
            )
            .exceptionHandling(exception -> exception
                    .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
            .oauth2Login(oauth2 -> oauth2
                    .userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService()))
                    .successHandler((request, response, authentication) -> {
                        DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();
                        String email = oidcUser.getAttribute("email") != null ? oidcUser.getAttribute("email") : oidcUser.getAttribute("login") + "@github.com";
                        String role = "user";
                        String token = jwtUtil.generateToken(email, role);
                        response.sendRedirect("https://cover-letter-creator-fe.vercel.app/auth-callback?token=" + token);
                    })
                )
            .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userDetailsService), UsernamePasswordAuthenticationFilter.class)
            .formLogin(form -> form.disable());

        return http.build();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}