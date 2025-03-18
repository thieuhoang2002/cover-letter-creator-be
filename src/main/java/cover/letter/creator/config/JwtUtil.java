package cover.letter.creator.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureAlgorithm;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;
@Component
public class JwtUtil {

    @Value("${jwt.secret}") // Đọc từ application.properties hoặc biến môi trường
    private String SECRET_KEY;

    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 giờ

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Tạo token
    public String generateToken(String email, String role) {
        return Jwts.builder()
            .subject(email) // Thay thế setSubject()
            .claim("role", role)
            .issuedAt(new Date()) // Thay thế setIssuedAt()
            .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Thay thế setExpiration()
            .signWith(getSigningKey(), Jwts.SIG.HS512)
            .compact();
    }

    public String generateTokenWithClaims(String email, Map<String, Object> claims) {
        return Jwts.builder()
            .subject(email)
            .claims(claims)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(getSigningKey(), Jwts.SIG.HS512)
            .compact();
    }
    
    // Lấy email từ token
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    // Lấy role từ token
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }
    
    // Xác thực token
    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public String extractAvatarUrl(String token) {
        return extractClaims(token).get("avatar_url", String.class);
    }

    private Claims extractClaims(String token) {
        return Jwts.parser() // ✅ Sử dụng API mới
            .verifyWith(getSigningKey()) // ✅ Xác thực token với khóa bí mật
            .build()
            .parseSignedClaims(token) // ✅ Thay thế parseClaimsJws()
            .getPayload(); // ✅ Thay thế getBody()
    }

}
