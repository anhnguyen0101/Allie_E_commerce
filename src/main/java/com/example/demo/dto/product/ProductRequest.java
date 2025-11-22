package com.example.demo.dto.product;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating/updating products via API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private BigDecimal price;

    /** Category id to associate (optional). */
    private Long categoryId;

}
