package cover.letter.creator.config;

import cover.letter.creator.service.RateLimitingService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    @Autowired
    private RateLimitingService rateLimitingService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        if ("POST".equalsIgnoreCase(method)) {
            String clientIp = getClientIp(request);

            // 1. Rate limit for Login
            if (path.endsWith("/api/users/login")) {
                if (!rateLimitingService.allowLogin(clientIp)) {
                    sendRateLimitResponse(response, "Bạn đã thử đăng nhập quá nhiều lần. Vui lòng chờ 1 phút rồi thử lại.");
                    return;
                }
            }
            // 2. Rate limit for Registration
            else if (path.endsWith("/api/users/profile/register")) {
                if (!rateLimitingService.allowRegister(clientIp)) {
                    sendRateLimitResponse(response, "Bạn đã đăng ký quá nhiều tài khoản từ địa chỉ mạng này. Vui lòng thử lại sau 10 phút.");
                    return;
                }
            }
            // 3. Rate limit for Forgot Password
            else if (path.endsWith("/api/auth/forgot-password")) {
                if (!rateLimitingService.allowForgotPassword(clientIp)) {
                    sendRateLimitResponse(response, "Bạn đã gửi yêu cầu khôi phục mật khẩu quá nhanh. Vui lòng chờ 10 phút trước khi thử lại.");
                    return;
                }
            }
            // 4. Rate limit for AI CV Generation
            else if (path.endsWith("/api/ai/generate-cv")) {
                String identifier = clientIp;
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    try {
                        String token = authHeader.substring(7);
                        String email = jwtUtil.extractEmail(token);
                        if (email != null && !email.isBlank()) {
                            identifier = email;
                        }
                    } catch (Exception ignored) {
                    }
                }

                if (!rateLimitingService.allowAiGeneration(identifier)) {
                    sendRateLimitResponse(response, "Hệ thống AI đang xử lý tối đa tần suất cho phép của bạn (5 lượt/phút). Vui lòng chờ ít giây.");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isBlank() || "unknown".equalsIgnoreCase(xfHeader)) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }

    private void sendRateLimitResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(429); // 429 Too Many Requests
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Retry-After", "60");
        String json = String.format("{\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"%s\"}", message);
        response.getWriter().write(json);
    }
}
