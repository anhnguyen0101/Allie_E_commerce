package com.example.demo.controller;

import com.example.demo.service.WishlistService;
import com.example.demo.dto.wishlist.WishlistItemResponse;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class WishlistController {
    
    private final WishlistService wishlistService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<WishlistItemResponse>> getWishlist(Principal principal) {
        log.info("❤️ [WishlistController] GET /api/wishlist - Principal: {}", principal != null ? principal.getName() : "null");
        
        if (principal == null) {
            log.warn("❌ [WishlistController] No principal - returning 401");
            return ResponseEntity.status(401).build();
        }
        
        var user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        log.info("✅ [WishlistController] User found: {}", user.getEmail());
        List<WishlistItemResponse> wishlist = wishlistService.getWishlist(user.getId());
        log.info("✅ [WishlistController] Wishlist loaded: {} items", wishlist.size());
        
        return ResponseEntity.ok(wishlist);
    }

    @PostMapping("/{productId}")
    public ResponseEntity<WishlistItemResponse> addToWishlist(
            @PathVariable Long productId,
            Principal principal) {
        
        log.info("❤️ ========================================");
        log.info("❤️ [WishlistController] POST /api/wishlist/{}", productId);
        log.info("❤️ ========================================");
        log.info("❤️ [WishlistController] Product ID: {}", productId);
        log.info("❤️ [WishlistController] Principal: {}", principal != null ? principal.getName() : "NULL");
        
        if (principal == null) {
            log.error("❌ [WishlistController] NO PRINCIPAL - Returning 401");
            return ResponseEntity.status(401).build();
        }
        
        try {
            var user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> {
                    log.error("❌ [WishlistController] User not found for email: {}", principal.getName());
                    return new RuntimeException("User not found");
                });
            
            log.info("✅ [WishlistController] User found: ID={}, Email={}", user.getId(), user.getEmail());
            
            WishlistItemResponse item = wishlistService.addToWishlist(user.getId(), productId);
            
            log.info("✅ [WishlistController] Item added to wishlist successfully");
            log.info("❤️ ========================================");
            
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            log.error("❌ [WishlistController] ERROR: {}", e.getMessage(), e);
            throw e;
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFromWishlist(
            @PathVariable Long productId,
            Principal principal) {
        
        log.info("❤️ [WishlistController] DELETE /api/wishlist/{} - Principal: {}", productId, principal != null ? principal.getName() : "null");
        
        if (principal == null) {
            log.warn("❌ [WishlistController] No principal - returning 401");
            return ResponseEntity.status(401).build();
        }
        
        var user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        log.info("✅ [WishlistController] User found: {}", user.getEmail());
        wishlistService.removeFromWishlist(user.getId(), productId);
        log.info("✅ [WishlistController] Item removed from wishlist");
        
        return ResponseEntity.ok().build();
    }
}
