package com.example.demo.controller;

import com.example.demo.service.OrderService;
import com.example.demo.dto.order.OrderResponse;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    private final UserRepository userRepository;

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        
        var user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        OrderResponse order = orderService.checkout(user.getId());
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        
        var user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<OrderResponse> orders = orderService.getUserOrders(user.getId());
        return ResponseEntity.ok(orders);
    }
}
