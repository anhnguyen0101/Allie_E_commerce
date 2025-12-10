package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.example.demo.dto.cart.CartResponse;
import com.example.demo.service.CartService;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<CartResponse> add(@RequestParam Long productId, @RequestParam int quantity) {
        CartResponse resp = cartService.addToCart(productId, quantity);
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/update")
    public ResponseEntity<CartResponse> update(@RequestParam Long productId, @RequestParam int quantity) {
        CartResponse resp = cartService.updateCart(productId, quantity);
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<CartResponse> remove(@PathVariable Long productId) {
        CartResponse resp = cartService.removeFromCart(productId);
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public ResponseEntity<CartResponse> get() {
        CartResponse resp = cartService.getCart();
        return ResponseEntity.ok(resp);
    }
}
