package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
/**
 * Utility component for handling JWT operations used by Spring Security.
 *
 * Main concept:
 * - Provide methods to generate tokens for authenticated users and to validate/parse incoming tokens.
 * - Uses HS256 (HMAC-SHA256) to sign tokens and simple JSON parsing for extracted claims.
 *
 * Responsibilities:
 * - `generateToken(UserDetails)`: create a signed JWT for a given user principal.
 * - `extractUsername(token)`: return the `sub` claim (username/email).
 * - `validateToken(token, user)`: verify signature, ensure token is not expired and matches the given user.
 *
 * Note: This is a lightweight implementation for educational/testing purposes. For production, use a
 * battle-tested JWT library (e.g., jjwt, java-jwt) and load secrets from secure configuration.
 */
public class JwtUtils {

    @Value("${jwt.secret:myVerySecureSecretKeyThatIsAtLeast256BitsLongForHS256Algorithm12345678}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private Long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        Date expirationDate = extractExpiration(token);
        boolean expired = expirationDate.before(new Date());

        log.info("🔐 [JwtUtils] Token expiration check:");
        log.info("🔐 [JwtUtils] Expiration date: {}", expirationDate);
        log.info("🔐 [JwtUtils] Current date: {}", new Date());
        log.info("🔐 [JwtUtils] Is expired: {}", expired);

        return expired;
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        
        // ✅ ADD ROLE TO JWT CLAIMS
        if (userDetails.getAuthorities() != null && !userDetails.getAuthorities().isEmpty()) {
            String role = userDetails.getAuthorities().iterator().next().getAuthority();
            claims.put("role", role);
            log.info("✅ [JwtUtils] Adding role to token: {}", role);
        }
        
        String token = createToken(claims, userDetails.getUsername());

        log.info("✅ [JwtUtils] ========================================");
        log.info("✅ [JwtUtils] TOKEN GENERATED");
        log.info("✅ [JwtUtils] ========================================");
        log.info("✅ [JwtUtils] Username: {}", userDetails.getUsername());
        log.info("✅ [JwtUtils] Token (first 50 chars): {}...", token.substring(0, Math.min(50, token.length())));
        log.info("✅ [JwtUtils] Expiration time: {} ms ({} hours)", expiration, expiration / 3600000);
        log.info("✅ [JwtUtils] Expires at: {}", new Date(System.currentTimeMillis() + expiration));
        log.info("✅ [JwtUtils] ========================================");

        return token;
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    // ✅ ADD METHOD TO EXTRACT ROLE FROM TOKEN
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        log.info("🔐 [JwtUtils] ========================================");
        log.info("🔐 [JwtUtils] VALIDATING TOKEN");
        log.info("🔐 [JwtUtils] ========================================");

        try {
            final String username = extractUsername(token);
            final String expectedUsername = userDetails.getUsername();

            log.info("🔐 [JwtUtils] Token username: {}", username);
            log.info("🔐 [JwtUtils] Expected username: {}", expectedUsername);
            log.info("🔐 [JwtUtils] Usernames match: {}", username.equals(expectedUsername));

            boolean usernameMatches = username.equals(expectedUsername);
            boolean notExpired = !isTokenExpired(token);
            boolean valid = usernameMatches && notExpired;

            log.info("🔐 [JwtUtils] Token not expired: {}", notExpired);
            log.info("🔐 [JwtUtils] Token valid: {}", valid);
            log.info("🔐 [JwtUtils] ========================================");

            return valid;
        } catch (Exception e) {
            log.error("❌ [JwtUtils] Token validation exception: {}", e.getMessage(), e);
            log.error("❌ [JwtUtils] ========================================");
            return false;
        }
    }
}
