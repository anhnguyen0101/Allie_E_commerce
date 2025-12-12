package com.example.demo.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private Long orderId;  // Add this field
    private String userName; // ✅ ADD THIS
    private BigDecimal totalAmount;
    private BigDecimal total;  // Add this field
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime orderDate;  // Add this field
    private List<OrderItemResponse> items;
}
