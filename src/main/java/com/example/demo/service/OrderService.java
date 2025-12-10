package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.RequiredArgsConstructor;

import com.example.demo.entity.User;
import com.example.demo.entity.Product;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.dto.order.OrderItemResponse;
import com.example.demo.dto.order.OrderResponse;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;

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
    public OrderResponse checkout() {
        User user = getCurrentUser();
        List<CartItem> cart = user.getCart();
        if (cart == null || cart.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem ci : cart) {
            Product p = ci.getProduct();
            if (p == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
            BigDecimal price = p.getPrice();
            int qty = ci.getQuantity();
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(qty));
            total = total.add(subtotal);

            OrderItem oi = OrderItem.builder()
                    .order(order)
                    .product(p)
                    .quantity(qty)
                    .priceAtPurchase(price)
                    .build();
            order.getItems().add(oi);
        }

        order.setTotal(total);
        Order saved = orderRepository.save(order);

        // clear cart
        user.getCart().clear();
        userRepository.save(user);

        // map to DTO
        List<OrderItemResponse> items = saved.getItems().stream().map(oi ->
            OrderItemResponse.builder()
                .productId(oi.getProduct().getId())
                .productName(oi.getProduct().getName())
                .quantity(oi.getQuantity())
                .priceAtPurchase(oi.getPriceAtPurchase())
                .subtotal(oi.getPriceAtPurchase().multiply(BigDecimal.valueOf(oi.getQuantity())))
                .build()
        ).collect(Collectors.toList());

        return OrderResponse.builder()
                .orderId(saved.getId())
                .orderDate(saved.getOrderDate())
                .total(saved.getTotal())
                .items(items)
                .build();
    }

    public List<OrderResponse> getUserOrders() {
        User user = getCurrentUser();
        List<Order> orders = orderRepository.findByUser(user);
        return orders.stream().map(o -> {
            List<OrderItemResponse> items = o.getItems().stream().map(oi ->
                OrderItemResponse.builder()
                    .productId(oi.getProduct().getId())
                    .productName(oi.getProduct().getName())
                    .quantity(oi.getQuantity())
                    .priceAtPurchase(oi.getPriceAtPurchase())
                    .subtotal(oi.getPriceAtPurchase().multiply(BigDecimal.valueOf(oi.getQuantity())))
                    .build()
            ).collect(Collectors.toList());
            return OrderResponse.builder()
                    .orderId(o.getId())
                    .orderDate(o.getOrderDate())
                    .total(o.getTotal())
                    .items(items)
                    .build();
        }).collect(Collectors.toList());
    }
}
