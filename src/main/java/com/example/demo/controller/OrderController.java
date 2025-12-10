package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.example.demo.dto.order.OrderResponse;
import com.example.demo.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout() {
        OrderResponse resp = orderService.checkout();
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> list() {
        List<OrderResponse> list = orderService.getUserOrders();
        return ResponseEntity.ok(list);
    }
}
