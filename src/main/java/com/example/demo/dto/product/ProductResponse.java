package com.example.demo.dto.product;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing the JSON response shape for Product.
 *
 * Main concept:
 * - Shape the API response for product resources; decouple persistence model from API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    // unique identifier of the product
    private Long id;

    // human readable product name
    private String name;

    // detailed product description
    private String description;

    // monetary price (BigDecimal recommended for money)
    private BigDecimal price;

    // name of the category this product belongs to
    private String categoryName;

}
