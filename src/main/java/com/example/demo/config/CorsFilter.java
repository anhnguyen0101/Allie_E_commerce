package com.example.demo.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    @PostConstruct
    public void init() {
        log.info("🌐 ========================================");
        log.info("🌐 [CorsFilter] CORS FILTER INITIALIZED!");
        log.info("🌐 [CorsFilter] Will intercept ALL requests");
        log.info("🌐 ========================================");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        
        log.info("🌐 ========== CORS FILTER ==========");
        log.info("🌐 [CorsFilter] {} {}", request.getMethod(), request.getRequestURI());
        log.info("🌐 [CorsFilter] Origin: {}", request.getHeader("Origin"));
        log.info("🌐 [CorsFilter] Authorization: {}", request.getHeader("Authorization") != null ? "EXISTS" : "NULL");
        
        // Set CORS headers
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "3600");
        
        // If OPTIONS request, respond immediately
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            log.info("✅ [CorsFilter] OPTIONS request - responding with 200");
            response.setStatus(HttpServletResponse.SC_OK);
            log.info("🌐 ========== END ==========");
            return;
        }
        
        log.info("🌐 [CorsFilter] Passing request to next filter");
        log.info("🌐 ========== END ==========");
        chain.doFilter(req, res);
    }
}
