package com.example.demo.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import com.example.demo.entity.User;

@Service
/**
 * Simple JWT generator service.
 *
 * Main concept:
 * - Produces HS256 (HMAC-SHA256) JSON Web Tokens for a given `User`.
 * - This implementation is minimal and dependency-free (no external JWT library).
 *
 * Responsibilities:
 * - Create a compact JWT with `sub` (email), `role`, `iat`, and `exp` claims.
 * - NOTE: secret is hard-coded here for simplicity — move to secure configuration for real apps.
 */
public class JwtService {

    // NOTE: replace with a secure secret and load from configuration in production
    private final String secret = "change-me-to-a-secure-secret";
    private final long expirationSeconds = 3600; // 1 hour

    public String generateToken(User user) {
        try {
            String header = base64UrlEncode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
            long now = System.currentTimeMillis() / 1000L;
            long exp = now + expirationSeconds;
            String payloadJson = String.format("{\"sub\":\"%s\",\"role\":\"%s\",\"iat\":%d,\"exp\":%d}",
                    escape(user.getEmail()), escape(user.getRole()), now, exp);
            String payload = base64UrlEncode(payloadJson);
            String unsignedToken = header + "." + payload;
            String signature = hmacSha256(unsignedToken, secret);
            return unsignedToken + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate JWT", e);
        }
    }

    private String base64UrlEncode(String str) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }

    private String hmacSha256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] sig = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(sig);
    }

    // minimal escaping for JSON strings (email/role); for complex cases use a JSON library
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
