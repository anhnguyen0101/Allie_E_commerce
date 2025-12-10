package com.example.demo.controller;

import com.example.demo.service.CartService;
import com.example.demo.dto.cart.CartResponse;
import com.example.demo.dto.cart.CartItemResponse;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class CartController {
    
    private final CartService cartService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(Principal principal) {
        log.info("🛒 [CartController] GET /api/cart - Principal: {}", principal != null ? principal.getName() : "null");
        
        if (principal == null) {
            log.warn("❌ [CartController] No principal - returning 401");
            return ResponseEntity.status(401).build();
        }
        
        var user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        log.info("✅ [CartController] User found: {}", user.getEmail());
        CartResponse cart = cartService.getCart(user.getId());
        log.info("✅ [CartController] Cart loaded: {} items", cart.getItems().size());
        
        return ResponseEntity.ok(cart);
    }

    @PostMapping
    public ResponseEntity<CartItemResponse> addToCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            Principal principal) {
        
        log.info("🛒 ========== ADD TO CART REQUEST ==========");
        log.info("🛒 [CartController] POST /api/cart");
        log.info("🛒 [CartController] Product ID: {}", productId);
        log.info("🛒 [CartController] Quantity: {}", quantity);
        log.info("🛒 [CartController] Principal: {}", principal);
        log.info("🛒 [CartController] Principal name: {}", principal != null ? principal.getName() : "NULL");
        
        if (principal == null) {
            log.error("❌ [CartController] NO PRINCIPAL - Returning 401");
            return ResponseEntity.status(401).build();
        }
        
        try {
            var user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> {
                    log.error("❌ [CartController] User not found for email: {}", principal.getName());
                    return new RuntimeException("User not found");
                });
            
            log.info("✅ [CartController] User found: ID={}, Email={}, Role={}", 
                     user.getId(), user.getEmail(), user.getRole());
            
            CartItemResponse item = cartService.addToCart(user.getId(), productId, quantity);
            
            log.info("✅ [CartController] Item added successfully: {}", item);
            log.info("🛒 ========== ADD TO CART SUCCESS ==========");
            
            return ResponseEntity.ok(item);
            
        } catch (Exception e) {
            log.error("❌ [CartController] ERROR: {}", e.getMessage(), e);
            log.error("🛒 ========== ADD TO CART FAILED ==========");
            throw e;
        }
    }

    @PutMapping
    public ResponseEntity<CartItemResponse> updateQuantity(
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            Principal principal) {
        
        log.info("🛒 [CartController] PUT /api/cart - productId: {}, quantity: {}, principal: {}", 
                 productId, quantity, principal != null ? principal.getName() : "null");
        
        if (principal == null) {
            log.warn("❌ [CartController] No principal - returning 401");
            return ResponseEntity.status(401).build();
        }
        
        var user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        log.info("✅ [CartController] User found: {}, updating product {} quantity to {}", 
                 user.getEmail(), productId, quantity);
        
        CartItemResponse item = cartService.updateQuantity(user.getId(), productId, quantity);
        log.info("✅ [CartController] Product quantity updated successfully");
        
        return ResponseEntity.ok(item);
    }

    @DeleteMapping
    public ResponseEntity<Void> removeFromCart(
            @RequestParam Long productId,
            Principal principal) {
        
        log.info("🛒 [CartController] DELETE /api/cart - productId: {}, principal: {}", 
                 productId, principal != null ? principal.getName() : "null");
        
        if (principal == null) {
            log.warn("❌ [CartController] No principal - returning 401");
            return ResponseEntity.status(401).build();
        }
        
        var user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        log.info("✅ [CartController] User found: {}, removing product {} from cart", user.getEmail(), productId);
        
        cartService.removeFromCart(user.getId(), productId);
        log.info("✅ [CartController] Product removed from cart successfully");
        
        return ResponseEntity.noContent().build();
    }
}
