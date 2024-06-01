package com.talha.microservices.product.service;


import com.talha.microservices.product.dto.ProductRequest;
import com.talha.microservices.product.dto.ProductResponse;
import com.talha.microservices.product.exception.ProductNotFoundException;
import com.talha.microservices.product.model.Product;
import com.talha.microservices.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest productRequest) {
        try {
        Product product = Product.builder()
                .name(productRequest.name())
                .description(productRequest.description())
                .price(productRequest.price())
                .build();
        productRepository.save(product);
        log.info("Product created succesfully");
        return mapToProductResponse(product);
        }
        catch (IllegalArgumentException e) {
            log.error("Product create failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while creating product: {}", e.getMessage());
            throw new RuntimeException("Failed to create product", e);
        }
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse updateProduct(String id, ProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        product.setName(productRequest.name());
        product.setDescription(productRequest.description());
        product.setPrice(productRequest.price());

        productRepository.save(product);
        log.info("Product id: {} name: {} is updated", product.getId(), product.getName());

        return mapToProductResponse(product);
    }

    public void deleteProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        productRepository.delete(product);
        log.info("Product id: {} name: {} has been deleted", product.getId(), product.getName());
    }

    public List<ProductResponse> createProducts(List<ProductRequest> productRequests) {
        List<ProductResponse> productResponses = new ArrayList<>();

        for (ProductRequest productRequest : productRequests) {
            try {
                Product product = new Product();
                product.setName(productRequest.name());
                product.setDescription(productRequest.description());
                product.setPrice(productRequest.price());

                Product savedProduct = productRepository.save(product);
                log.info("Product id: {} name: {} has been created", savedProduct.getId(), savedProduct.getName());

                productResponses.add(mapToProductResponse(savedProduct));

            } catch (Exception e) {
                log.error("Failed to create product with name: {}", productRequest.name(), e);
                throw new RuntimeException("Failed to create product: " + productRequest.name(), e);
            }
        }
        return productResponses;
    }

    public List<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        try {
            List<Product> products = productRepository.findByPriceBetween(minPrice, maxPrice);
            return products.stream()
                    .map(this::mapToProductResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to retrieve products in the price range: {} - {}", minPrice, maxPrice, e);
            throw new RuntimeException("Failed to retrieve products in the price range: " + minPrice + " - " + maxPrice, e);
        }
    }

    public List<ProductResponse> searchProducts(String keyword) {
        try {
            List<Product> products = productRepository.findByNameContainingOrDescriptionContaining(keyword, keyword);
            return products.stream()
                    .map(this::mapToProductResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to search products with keyword: {}", keyword, e);
            throw new RuntimeException("Failed to search products with keyword: " + keyword, e);
        }
    }

    private ProductResponse mapToProductResponse(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice());
    }
}
