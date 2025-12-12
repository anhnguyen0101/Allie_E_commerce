package com.example.demo.service;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.User;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.dto.order.OrderResponse;
import com.example.demo.dto.order.OrderItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public List<OrderResponse> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return orders.stream()
            .map(this::toOrderResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse checkout(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // ❌ OPTIONAL: Block admins from shopping
        if (user.getRole() == User.Role.ADMIN) {
            throw new RuntimeException("Admins cannot place orders. Please use a regular user account.");
        }
        
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Calculate total
        BigDecimal total = cartItems.stream()
            .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create order
        Order order = Order.builder()
            .user(user)
            .totalAmount(total)
            .status(Order.OrderStatus.PENDING)
            .build();
        
        // Create order items from cart items
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = OrderItem.builder()
                .order(order)
                .product(cartItem.getProduct())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getProduct().getPrice())
                .build();
            order.getItems().add(orderItem);
        }

        Order savedOrder = orderRepository.save(order);
        
        // Clear cart after checkout
        cartItemRepository.deleteAll(cartItems);

        return toOrderResponse(savedOrder);
    }

    private OrderResponse toOrderResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
            .map(item -> OrderItemResponse.builder()
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .productImage(item.getProduct().getImageUrl())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .build())
            .collect(Collectors.toList());

        return OrderResponse.builder()
            .id(order.getId())
            .totalAmount(order.getTotalAmount())
            .status(order.getStatus().name())
            .createdAt(order.getCreatedAt())
            .items(items)
            .build();
    }
}
