package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.example.demo.dto.product.ProductResponse;
import com.example.demo.service.WishlistService;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping("/{productId}")
    public ResponseEntity<List<ProductResponse>> add(@PathVariable Long productId) {
        List<ProductResponse> list = wishlistService.addToWishlist(productId);
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<List<ProductResponse>> remove(@PathVariable Long productId) {
        List<ProductResponse> list = wishlistService.removeFromWishlist(productId);
        return ResponseEntity.ok(list);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> get() {
        List<ProductResponse> list = wishlistService.getWishlist();
        return ResponseEntity.ok(list);
    }
}
