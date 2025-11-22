package com.example.demo.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple wrapper for authentication token responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResponse {
    private String token;
}
