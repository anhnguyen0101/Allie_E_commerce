package com.example.demo.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.example.demo.dto.admin.AdminStatsResponse;
import com.example.demo.dto.admin.BestSellerResponse;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.OrderItemRepository;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository; // NOTE: this is the file with custom query types

    public AdminStatsResponse getStats() {
        long totalUsers = userRepository.count();
        long totalProducts = productRepository.count();
        long totalOrders = orderRepository.count();
        BigDecimal totalRevenue = orderRepository.sumTotalRevenue();
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        // get top 5 best selling products via repository projection
        List<BestSellerResponse> bestSellers = orderItemRepository.findBestSellingProducts(PageRequest.of(0, 5)).stream()
                .map(p -> BestSellerResponse.builder()
                        .productId(p.getProductId())
                        .productName(p.getProductName())
                        .totalSold(p.getTotalSold() != null ? p.getTotalSold() : 0L)
                        .build())
                .collect(Collectors.toList());

        return AdminStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalProducts(totalProducts)
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .bestSellingProducts(bestSellers)
                .build();
    }
}
