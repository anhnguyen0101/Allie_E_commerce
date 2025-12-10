package com.example.demo.seed;

import com.example.demo.dto.external.FakeStoreProduct;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;

@Component
public class ProductSeeder implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(ProductSeeder.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }
        String url = "https://fakestoreapi.com/products";
        FakeStoreProduct[] products = restTemplate.getForObject(url, FakeStoreProduct[].class);
        if (products == null) {
            logger.warn("No products fetched from Fake Store API.");
            return;
        }
        int seededCount = 0;
        for (FakeStoreProduct fsProduct : products) {
            Category category = categoryRepository.findByName(fsProduct.category).orElse(null);
            if (category == null) {
                category = new Category();
                category.setName(fsProduct.category);
                category = categoryRepository.save(category);
            }
            Product product = new Product();
            product.setName(fsProduct.title);
            product.setDescription(fsProduct.description);
            product.setPrice(BigDecimal.valueOf(fsProduct.price));
            product.setImageUrl(fsProduct.image);
            product.setCategory(category);
            productRepository.save(product);
            seededCount++;
        }
        logger.info("Seeded {} products", seededCount);
    }
}
