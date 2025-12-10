package com.example.demo.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.example.demo.security.JwtUtils;

@Component
@RequiredArgsConstructor
@Slf4j
/**
 * Servlet filter that validates JWTs on incoming HTTP requests.
 *
 * Main concept:
 * - Inspect the `Authorization` header for a Bearer token, validate it, and if valid
 *   populate the Spring Security `SecurityContext` with an authenticated principal.
 *
 * Responsibilities:
 * - Extract token from header, verify signature and expiry via `JwtUtils`.
 * - Load `UserDetails` using `UserDetailsService` and set authentication for the request.
 */
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        log.info("🔐 [JwtFilter] ========== REQUEST ==========");
        log.info("🔐 [JwtFilter] {} {}", method, requestURI);
        
        final String authHeader = request.getHeader("Authorization");
        log.info("🔐 [JwtFilter] Authorization header: {}", authHeader != null ? "EXISTS" : "NULL");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.info("🔐 [JwtFilter] Token extracted (first 20 chars): {}...", token.substring(0, Math.min(20, token.length())));
            
            try {
                String username = jwtUtils.extractUsername(token);
                log.info("🔐 [JwtFilter] Username from token: {}", username);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    log.info("🔐 [JwtFilter] UserDetails loaded: {}", userDetails.getUsername());
                    log.info("🔐 [JwtFilter] Authorities: {}", userDetails.getAuthorities());
                    
                    if (jwtUtils.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        
                        log.info("✅ [JwtFilter] Authentication set successfully");
                        log.info("✅ [JwtFilter] User: {}, Authorities: {}", 
                                 userDetails.getUsername(), userDetails.getAuthorities());
                    } else {
                        log.error("❌ [JwtFilter] Token validation FAILED");
                    }
                } else {
                    if (username == null) {
                        log.warn("⚠️ [JwtFilter] Username is NULL from token");
                    }
                    if (SecurityContextHolder.getContext().getAuthentication() != null) {
                        log.info("ℹ️ [JwtFilter] Authentication already exists");
                    }
                }
            } catch (Exception e) {
                log.error("❌ [JwtFilter] Exception during token validation: {}", e.getMessage(), e);
            }
        } else {
            log.warn("⚠️ [JwtFilter] No Bearer token found in Authorization header");
        }
        
        log.info("🔐 [JwtFilter] ========== END ==========");
        filterChain.doFilter(request, response);
    }
}
