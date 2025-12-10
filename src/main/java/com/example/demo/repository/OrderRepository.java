package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Order;
import com.example.demo.entity.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o")
    java.math.BigDecimal sumTotalRevenue();
}
