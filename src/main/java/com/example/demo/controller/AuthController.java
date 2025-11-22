package com.example.demo.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.RequiredArgsConstructor;

import com.example.demo.dto.auth.RegisterRequest;
import com.example.demo.dto.auth.LoginRequest;
import com.example.demo.dto.auth.TokenResponse;
import com.example.demo.service.AuthService;

/**
 * Controller exposing authentication endpoints (/api/auth).
 * - POST /api/auth/register
 * - POST /api/auth/login
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@RequestBody RegisterRequest request) {
        TokenResponse token = authService.register(request);
        return ResponseEntity.created(URI.create("/api/auth/register")).body(token);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        TokenResponse token = authService.login(request);
        return ResponseEntity.ok(token);
    }
}
