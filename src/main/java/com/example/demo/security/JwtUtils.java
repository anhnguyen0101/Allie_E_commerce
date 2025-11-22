package com.example.demo.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetails;

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

    // TODO: move to configuration and use a secure secret in production
    private final String secret = "change-me-to-a-secure-secret";
    private final long expirationSeconds = 3600; // 1 hour

    public String generateToken(UserDetails user) {
        try {
            String header = base64UrlEncode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
            long now = System.currentTimeMillis() / 1000L;
            long exp = now + expirationSeconds;
            String payloadJson = String.format("{\"sub\":\"%s\",\"role\":\"%s\",\"iat\":%d,\"exp\":%d}",
                    escape(user.getUsername()), "", now, exp);
            String payload = base64UrlEncode(payloadJson);
            String unsignedToken = header + "." + payload;
            String signature = hmacSha256(unsignedToken, secret);
            return unsignedToken + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Unable to generate JWT", e);
        }
    }

    public String extractUsername(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return null;
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            return extractStringClaim(payloadJson, "sub");
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validateToken(String token, UserDetails user) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;

            String unsigned = parts[0] + "." + parts[1];
            String signature = parts[2];
            String expectedSig = hmacSha256(unsigned, secret);
            if (!constantTimeEquals(expectedSig, signature)) return false;

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            String username = extractStringClaim(payloadJson, "sub");
            if (username == null || !username.equals(user.getUsername())) return false;

            Long exp = extractLongClaim(payloadJson, "exp");
            if (exp == null) return false;
            long now = System.currentTimeMillis() / 1000L;
            if (now > exp) return false;

            return true;
        } catch (Exception e) {
            return false;
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

    // minimal JSON helpers (sufficient for our simple payload)
    private String extractStringClaim(String json, String claim) {
        String pattern = "\"" + claim + "\":\"";
        int idx = json.indexOf(pattern);
        if (idx == -1) return null;
        int start = idx + pattern.length();
        int end = json.indexOf('"', start);
        if (end == -1) return null;
        return json.substring(start, end).replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private Long extractLongClaim(String json, String claim) {
        String pattern = "\"" + claim + "\":";
        int idx = json.indexOf(pattern);
        if (idx == -1) return null;
        int start = idx + pattern.length();
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)))) end++;
        try {
            return Long.parseLong(json.substring(start, end));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // basic constant time comparison to mitigate timing attacks
    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    // minimal escaping for JSON strings (username); for complex cases use a JSON lib
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
