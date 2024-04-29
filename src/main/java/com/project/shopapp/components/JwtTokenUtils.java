package com.project.shopapp.components;

import com.project.shopapp.exceptions.InvalidParamException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {
    // lưu trong biến môi trường
    @Value("${jwt.expiration}")
    private int expiration;
    @Value("${jwt.secretKey}")
    private String secretKey;

    public String generateToken(com.project.shopapp.models.User user) throws InvalidParamException {
        // properties => claims : là mã hóa các thuộc tính trong user\
        // Map claims để thêm các thuộc tính cần mã hóa
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("phoneNumber", user.getPhoneNumber());

        // this.generatedSecretKey(); // lấy secret key để lưu vào biến môi trường
        try {
            // mã hóa từng cái trong claims
            String token = Jwts.builder()
                    .setClaims(claims) // để lấy claims ra phải có hàm extractClaims
                    .setSubject(user.getPhoneNumber()) // để sau này dùng getSubjec lấy cái phoneNumber ra
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L)) // chuyển từ s sang date
                    .signWith(getSignInkey(), SignatureAlgorithm.HS256)
                    .compact();

            return token;
        } catch (Exception e) {
            // có thể dùng Logger để log ra thay dòng dưới
            throw  new InvalidParamException("Cannot create jwts token: " + e.getMessage());
        }
    }

    // BVnuwU06oZ8DDE90I1FS1rynKWUNSPHepFpcEgWQgKw= : secret key

    private Key getSignInkey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }
    private String generatedSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[32];
        random.nextBytes(keyBytes);
        String secretKey = Encoders.BASE64.encode(keyBytes);
        return secretKey;
    }

    // lấy ra tất cả claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInkey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // chỉ lấy 1 claim
    // cần lấy ra claim cụ thể nào thì truyền thuộc tính vào claimResolver
    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimResolver.apply(claims);
    }
    // lấy phoneNumber từ token
    public String extractPhoneNumber(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // check expiration / kiểm tra token hết hạn chưa
    public boolean isTokenExpired(String token) {
        Date expirationDate = this.extractClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }

    // kiểm tra token còn hợp lệ ko
    public boolean validateToken(String token, UserDetails userDetails) {
        String phoneNumber = extractPhoneNumber(token);
        return (phoneNumber.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
}
