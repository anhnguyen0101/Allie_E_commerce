package com.example.demo.service;

import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.dto.cart.CartResponse;
import com.example.demo.dto.cart.CartItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public CartItemResponse addToCart(Long userId, Long productId, Integer quantity) {
        log.info("🛒 [CartService] addToCart - userId: {}, productId: {}, quantity: {}", userId, productId, quantity);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.error("❌ [CartService] User not found: {}", userId);
                return new RuntimeException("User not found");
            });
        
        log.info("✅ [CartService] User found: {}", user.getEmail());
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> {
                log.error("❌ [CartService] Product not found: {}", productId);
                return new RuntimeException("Product not found");
            });
        
        log.info("✅ [CartService] Product found: {}", product.getName());

        // Check if item already exists in cart
        CartItem cartItem = cartItemRepository
            .findByUserIdAndProductId(userId, productId)
            .orElse(null);

        if (cartItem != null) {
            log.info("🛒 [CartService] Item already in cart, updating quantity from {} to {}", 
                     cartItem.getQuantity(), cartItem.getQuantity() + quantity);
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            log.info("🛒 [CartService] Creating new cart item");
            cartItem = CartItem.builder()
                .user(user)
                .product(product)
                .quantity(quantity)
                .build();
        }

        CartItem saved = cartItemRepository.save(cartItem);
        log.info("✅ [CartService] Cart item saved successfully: {}", saved.getId());
        
        return toCartItemResponse(saved);
    }

    public CartResponse getCart(Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        
        List<CartItemResponse> itemResponses = items.stream()
            .map(this::toCartItemResponse)
            .collect(Collectors.toList());

        return CartResponse.builder()
            .items(itemResponses)
            .build();
    }

    @Transactional
    public void removeFromCart(Long userId, Long productId) {
        cartItemRepository.deleteByUserIdAndProductId(userId, productId);
    }

    @Transactional
    public CartItemResponse updateQuantity(Long userId, Long productId, Integer quantity) {
        CartItem cartItem = cartItemRepository
            .findByUserIdAndProductId(userId, productId)
            .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
            return null;
        }

        cartItem.setQuantity(quantity);
        CartItem updated = cartItemRepository.save(cartItem);
        return toCartItemResponse(updated);
    }

    private CartItemResponse toCartItemResponse(CartItem item) {
        return CartItemResponse.builder()
            .productId(item.getProduct().getId())
            .name(item.getProduct().getName())
            .description(item.getProduct().getDescription())
            .price(item.getProduct().getPrice())
            .imageUrl(item.getProduct().getImageUrl())
            .quantity(item.getQuantity())
            .build();
    }
}
