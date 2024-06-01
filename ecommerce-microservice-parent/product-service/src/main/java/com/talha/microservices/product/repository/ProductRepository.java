package com.talha.microservices.product.repository;

import com.talha.microservices.product.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    List<Product> findByNameContainingOrDescriptionContaining(String nameKeyword, String descriptionKeyword);
}
