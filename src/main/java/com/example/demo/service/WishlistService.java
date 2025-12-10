package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.RequiredArgsConstructor;

import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.entity.User;
import com.example.demo.entity.Product;
import com.example.demo.dto.product.ProductResponse;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
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

    @Transactional
    public List<ProductResponse> addToWishlist(Long productId) {
        User user = getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        user.getWishlist().add(product);
        userRepository.save(user);
        return user.getWishlist().stream().map(productService::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public List<ProductResponse> removeFromWishlist(Long productId) {
        User user = getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        user.getWishlist().remove(product);
        userRepository.save(user);
        return user.getWishlist().stream().map(productService::toResponse).collect(Collectors.toList());
    }

    public List<ProductResponse> getWishlist() {
        User user = getCurrentUser();
        return user.getWishlist().stream().map(productService::toResponse).collect(Collectors.toList());
    }
}
