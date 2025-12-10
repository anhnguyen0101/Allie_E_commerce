package com.example.demo.repository;

import com.example.demo.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    @EntityGraph(attributePaths = {"product", "user"})
    List<CartItem> findByUserId(Long userId);
    
    @EntityGraph(attributePaths = {"product", "user"})
    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);
    
    void deleteByUserIdAndProductId(Long userId, Long productId);
}
