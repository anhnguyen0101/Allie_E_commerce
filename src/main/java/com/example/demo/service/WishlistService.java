package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.entity.Product;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.dto.wishlist.WishlistItemResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistService {
    
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<WishlistItemResponse> getWishlist(Long userId) {
        log.info("❤️ [WishlistService] Getting wishlist for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Force initialization of wishlist within transaction
        List<WishlistItemResponse> wishlist = user.getWishlist().stream()
            .map(product -> WishlistItemResponse.builder()
                .productId(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .build())
            .collect(Collectors.toList());
        
        log.info("✅ [WishlistService] Wishlist loaded: {} items", wishlist.size());
        return wishlist;
    }

    @Transactional
    public WishlistItemResponse addToWishlist(Long userId, Long productId) {
        log.info("❤️ [WishlistService] Adding product {} to wishlist for user {}", productId, userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        user.getWishlist().add(product);
        userRepository.save(user);
        
        log.info("✅ [WishlistService] Product added to wishlist");
        
        return WishlistItemResponse.builder()
            .productId(product.getId())
            .name(product.getName())
            .description(product.getDescription())
            .price(product.getPrice())
            .imageUrl(product.getImageUrl())
            .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
            .build();
    }

    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {
        log.info("❤️ [WishlistService] Removing product {} from wishlist for user {}", productId, userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.getWishlist().removeIf(product -> product.getId().equals(productId));
        userRepository.save(user);
        
        log.info("✅ [WishlistService] Product removed from wishlist");
    }
}
