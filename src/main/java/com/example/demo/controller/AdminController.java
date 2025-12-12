package com.example.demo.controller;

import com.example.demo.dto.admin.ProductRequest;
import com.example.demo.dto.admin.CategoryRequest;
import com.example.demo.dto.admin.UserResponse;
import com.example.demo.dto.product.ProductResponse;
import com.example.demo.dto.category.CategoryResponse;
import com.example.demo.dto.order.OrderResponse;
import com.example.demo.dto.order.OrderItemResponse;
import com.example.demo.entity.Product;
import com.example.demo.entity.Category;
import com.example.demo.entity.User;
import com.example.demo.entity.Order;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    // ============================================
    // PRODUCTS MANAGEMENT
    // ============================================

    @GetMapping("/products")
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("🔐 [AdminController] GET /api/admin/products - page={}, size={}", page, size);
        
        Page<Product> products = productRepository.findAll(PageRequest.of(page, size));
        Page<ProductResponse> response = products.map(this::toProductResponse);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/products")
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        log.info("📦 [AdminController] POST /api/admin/products");
        log.info("📦 [AdminController] Request: {}", request);
        
        // Validate category exists
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        // Create product
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .category(category)
                .build();
        
        Product savedProduct = productRepository.save(product);
        
        log.info("✅ [AdminController] Product created: {}", savedProduct.getId());
        
        return ResponseEntity.ok(toProductResponse(savedProduct));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequest request) {
        
        log.info("📦 [AdminController] PUT /api/admin/products/{}", id);
        log.info("📦 [AdminController] Request: {}", request);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);
        
        Product updatedProduct = productRepository.save(product);
        
        log.info("✅ [AdminController] Product updated: {}", updatedProduct.getId());
        
        return ResponseEntity.ok(toProductResponse(updatedProduct));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.info("🔐 [AdminController] DELETE /api/admin/products/{}", id);
        
        productRepository.deleteById(id);
        
        return ResponseEntity.ok().build();
    }

    // ============================================
    // CATEGORIES MANAGEMENT
    // ============================================

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        log.info("🔐 [AdminController] GET /api/admin/categories");
        
        List<Category> categories = categoryRepository.findAll();
        List<CategoryResponse> response = categories.stream()
                .map(this::toCategoryResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest request) {
        log.info("🔐 [AdminController] POST /api/admin/categories - name={}", request.getName());
        
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        
        Category saved = categoryRepository.save(category);
        
        return ResponseEntity.ok(toCategoryResponse(saved));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.info("🔐 [AdminController] DELETE /api/admin/categories/{}", id);
        
        categoryRepository.deleteById(id);
        
        return ResponseEntity.ok().build();
    }

    // ============================================
    // ORDERS MANAGEMENT
    // ============================================

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        log.info("📦 [AdminController] GET /api/admin/orders");
        
        List<Order> orders = orderRepository.findAll();
        List<OrderResponse> response = orders.stream()
            .map(this::toOrderResponse)
            .collect(Collectors.toList());
        
        log.info("✅ [AdminController] Orders loaded: {}", response.size());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> body) {
        
        String newStatus = body.get("status");
        log.info("📦 [AdminController] PUT /api/admin/orders/{}/status", orderId);
        log.info("📦 [AdminController] New status: {}", newStatus);
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus(Order.OrderStatus.valueOf(newStatus));
        Order updated = orderRepository.save(order);
        
        log.info("✅ [AdminController] Order status updated");
        return ResponseEntity.ok(toOrderResponse(updated));
    }

    // ============================================
    // USERS MANAGEMENT
    // ============================================

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("👥 [AdminController] GET /api/admin/users");
        
        List<User> users = userRepository.findAll();
        List<UserResponse> response = users.stream()
            .map(user -> UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build())
            .collect(Collectors.toList());
        
        log.info("✅ [AdminController] Users loaded: {}", response.size());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{userId}/promote")
    public ResponseEntity<UserResponse> promoteUser(@PathVariable Long userId) {
        log.info("👥 [AdminController] PUT /api/admin/users/{}/promote", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setRole(User.Role.ADMIN);
        User updated = userRepository.save(user);
        
        log.info("✅ [AdminController] User promoted to ADMIN: {}", updated.getEmail());
        
        return ResponseEntity.ok(UserResponse.builder()
            .id(updated.getId())
            .name(updated.getName())
            .email(updated.getEmail())
            .role(updated.getRole().name())
            .build());
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        log.info("👥 [AdminController] DELETE /api/admin/users/{}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Prevent deleting yourself
        // You might want to add this check based on current authenticated user
        
        userRepository.delete(user);
        log.info("✅ [AdminController] User deleted: {}", user.getEmail());
        
        return ResponseEntity.ok().build();
    }

    // ============================================
    // DASHBOARD STATS
    // ============================================

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("📊 [AdminController] GET /api/admin/stats");
        
        long totalProducts = productRepository.count();
        long totalOrders = orderRepository.count();
        long totalUsers = userRepository.count();
        long pendingOrders = orderRepository.findAll().stream()
            .filter(order -> order.getStatus() == Order.OrderStatus.PENDING)
            .count();
        
        Map<String, Object> stats = Map.of(
            "totalProducts", totalProducts,
            "totalOrders", totalOrders,
            "totalUsers", totalUsers,
            "pendingOrders", pendingOrders
        );
        
        log.info("✅ [AdminController] Stats: {}", stats);
        
        return ResponseEntity.ok(stats);
    }

    // ============================================
    // HELPER METHODS
    // ============================================

    private CategoryResponse toCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription()) // ✅ NOW WORKS
                .build();
    }

    private ProductResponse toProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null) // ✅ NOW WORKS
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .build();
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
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
            .userName(order.getUser().getName())
            .totalAmount(order.getTotalAmount())
            .status(order.getStatus().name())
            .createdAt(order.getCreatedAt())
            .items(items)
            .build();
    }
}
