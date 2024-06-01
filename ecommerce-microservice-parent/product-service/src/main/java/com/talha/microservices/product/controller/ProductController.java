package com.talha.microservices.product.controller;

import com.talha.microservices.product.dto.ProductRequest;
import com.talha.microservices.product.dto.ProductResponse;
import com.talha.microservices.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Yeni Ürün Ekleme Metodu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ürün başarıyla oluşturuldu",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek",
                    content = @Content)})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productRequest) {
            ProductResponse productResponse = productService.createProduct(productRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(productResponse);
    }

    @Operation(summary = "Toplu Ürün Ekleme Metodu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ürünler başarıyla oluşturuldu",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek",
                    content = @Content)})
    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<ProductResponse>> createProducts(@RequestBody List<ProductRequest> productRequests) {
            List<ProductResponse> productResponses = productService.createProducts(productRequests);
            return ResponseEntity.status(HttpStatus.CREATED).body(productResponses);
    }

    @Operation(summary = "Tüm Ürünleri Listeleme Metodu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ürünler başarıyla getirildi",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class))})})
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    @Operation(summary = "Belirli Ürünü Güncelleme Metodu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ürün başarıyla güncellendi",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Ürün bulunamadı",
                    content = @Content)})
    @PutMapping("/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable("id") String id, @RequestBody ProductRequest productRequest) {
            ProductResponse productResponse = productService.updateProduct(id, productRequest);
            return ResponseEntity.ok(productResponse);
    }

    @Operation(summary = "Belirli Ürünü Silme Metodu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ürün başarıyla silindi",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Ürün bulunamadı",
                    content = @Content)})
    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteProductById(@PathVariable("id") String id) {
            productService.deleteProductById(id);
            return ResponseEntity.ok("Product deleted successfully");
    }

    @Operation(summary = "Fiyat Aralığına Göre Ürünleri Listeleme Metodu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ürünler başarıyla getirildi",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class))}),
            @ApiResponse(responseCode = "204", description = "Belirtilen fiyat aralığında ürün bulunamadı",
                    content = @Content)})
    @GetMapping("/price")
    public ResponseEntity<List<ProductResponse>> getProductsByPriceRange(@RequestParam BigDecimal minPrice, @RequestParam BigDecimal maxPrice) {
            List<ProductResponse> productResponses = productService.getProductsByPriceRange(minPrice, maxPrice);
            if (productResponses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(productResponses);
            }
            return ResponseEntity.ok(productResponses);
    }

    @Operation(summary = "Anahtar Kelimeye Göre Ürünleri Arama ve Listeleme Metodu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ürünler başarıyla getirildi",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class))}),
            @ApiResponse(responseCode = "204", description = "Belirtilen anahtar kelimeye göre ürün bulunamadı",
                    content = @Content)})
    @GetMapping("/search")
        public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String keyword) {
            List<ProductResponse> productResponses = productService.searchProducts(keyword);
            return ResponseEntity.ok(productResponses);
    }
}
