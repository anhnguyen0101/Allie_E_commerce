package com.example.demo.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.RequiredArgsConstructor;

import com.example.demo.dto.cart.CartItemResponse;
import com.example.demo.dto.cart.CartResponse;
import com.example.demo.entity.User;
import com.example.demo.entity.Product;
import com.example.demo.entity.CartItem;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.CartItemRepository;

@Service
@RequiredArgsConstructor
public class CartService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        String email;
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            email = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private CartResponse buildCartResponse(User user) {
        List<CartItemResponse> items = user.getCart().stream().map(ci -> {
            Product p = ci.getProduct();
            BigDecimal price = p.getPrice();
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(ci.getQuantity()));
            return CartItemResponse.builder()
                    .productId(p.getId())
                    .productName(p.getName())
                    .price(price)
                    .quantity(ci.getQuantity())
                    .subtotal(subtotal)
                    .build();
        }).collect(Collectors.toList());
        BigDecimal total = items.stream().map(CartItemResponse::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return CartResponse.builder().items(items).total(total).build();
    }

    @Transactional
    public CartResponse addToCart(Long productId, int quantity) {
        if (quantity <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be greater than zero");
        User user = getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        Optional<CartItem> existing = cartItemRepository.findByUserAndProduct(user, product);
        if (existing.isPresent()) {
            CartItem ci = existing.get();
            ci.setQuantity(ci.getQuantity() + quantity);
            cartItemRepository.save(ci);
        } else {
            CartItem ci = CartItem.builder().user(user).product(product).quantity(quantity).build();
            user.getCart().add(ci);
            userRepository.save(user);
        }

        // reload user to ensure relationships are initialized
        User refreshed = userRepository.findById(user.getId()).orElse(user);
        return buildCartResponse(refreshed);
    }

    @Transactional
    public CartResponse updateCart(Long productId, int quantity) {
        if (quantity <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be greater than zero");
        User user = getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        CartItem ci = cartItemRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));
        ci.setQuantity(quantity);
        cartItemRepository.save(ci);

        User refreshed = userRepository.findById(user.getId()).orElse(user);
        return buildCartResponse(refreshed);
    }

    @Transactional
    public CartResponse removeFromCart(Long productId) {
        User user = getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        cartItemRepository.findByUserAndProduct(user, product).ifPresent(ci -> {
            user.getCart().remove(ci);
            cartItemRepository.delete(ci);
            userRepository.save(user);
        });

        User refreshed = userRepository.findById(user.getId()).orElse(user);
        return buildCartResponse(refreshed);
    }

    public CartResponse getCart() {
        User user = getCurrentUser();
        return buildCartResponse(user);
    }

    @Transactional
    public void clearCart() {
        User user = getCurrentUser();
        user.getCart().clear();
        userRepository.save(user);
    }
}
