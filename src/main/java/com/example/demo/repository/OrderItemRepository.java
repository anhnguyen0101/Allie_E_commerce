package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

	@org.springframework.data.jpa.repository.Query("SELECT oi.product.id as productId, oi.product.name as productName, SUM(oi.quantity) as totalSold FROM OrderItem oi GROUP BY oi.product.id, oi.product.name ORDER BY SUM(oi.quantity) DESC")
	java.util.List<BestSellerProjection> findBestSellingProducts(org.springframework.data.domain.Pageable pageable);

	// Projection for best-seller query
	interface BestSellerProjection {
		Long getProductId();
		String getProductName();
		Long getTotalSold();
	}

}
