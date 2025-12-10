package com.example.demo.dto.cart;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {
    private Long productId;
    private String productName;
    private BigDecimal price;
    private int quantity;
    private BigDecimal subtotal;
}
