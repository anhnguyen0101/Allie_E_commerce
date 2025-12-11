package com.example.demo.controller;

import java.net.URI;
import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;

import com.example.demo.service.AuthService;
import com.example.demo.repository.UserRepository;
import com.example.demo.dto.auth.LoginRequest;
import com.example.demo.dto.auth.RegisterRequest;
import com.example.demo.dto.auth.TokenResponse;
import com.example.demo.dto.auth.UserInfoResponse;
import com.example.demo.entity.User;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")

public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody RegisterRequest request) {
        log.info("📝 ========================================");
        log.info("📝 [AuthController] POST /api/auth/register");
        log.info("📝 ========================================");
        log.info("📝 [AuthController] Request body - Name: {}", request.getName());
        log.info("📝 [AuthController] Request body - Email: {}", request.getEmail());
        log.info("📝 [AuthController] Request body - Password length: {}", request.getPassword() != null ? request.getPassword().length() : 0);
        
        try {
            TokenResponse token = authService.register(request);
            
            log.info("✅ ========================================");
            log.info("✅ [AuthController] REGISTRATION SUCCESS");
            log.info("✅ ========================================");
            log.info("✅ [AuthController] Token generated (first 20 chars): {}...", token.getToken().substring(0, 20));
            log.info("✅ [AuthController] User email: {}", token.getEmail());
            log.info("✅ [AuthController] User name: {}", token.getName());
            
            return ResponseEntity.created(URI.create("/api/auth/register")).body(token);
        } catch (IllegalArgumentException e) {
            log.error("❌ ========================================");
            log.error("❌ [AuthController] REGISTRATION FAILED - Validation Error");
            log.error("❌ ========================================");
            log.error("❌ [AuthController] Error: {}", e.getMessage());
            
            // Return 409 Conflict for duplicate email
            if (e.getMessage().contains("Email already in use")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("❌ ========================================");
            log.error("❌ [AuthController] REGISTRATION FAILED - Internal Error");
            log.error("❌ ========================================");
            log.error("❌ [AuthController] Error: {}", e.getMessage(), e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        TokenResponse token = authService.login(request);
        return ResponseEntity.ok(token);
    }

    /**
     * Returns info about the currently authenticated user (from JWT).
     * GET /api/auth/me
     */
    // @PreAuthorize("hasRole('USER')") // <-- REMOVE if present
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> me(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserInfoResponse dto = UserInfoResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
        return ResponseEntity.ok(dto);
    }
}
