package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Order;
import com.example.demo.entity.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o")
    java.math.BigDecimal sumTotalRevenue();

    @EntityGraph(attributePaths = {"items", "items.product"})
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.product")
    List<Order> findAllWithItems();
}
