package com.talha.microservices.order.controller;

import com.talha.microservices.order.dto.OrderRequest;
import com.talha.microservices.order.dto.OrderResponse;
import com.talha.microservices.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Sipariş Verme Metodu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sipariş başarıyla oluşturuldu",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek",
                    content = @Content)})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest) {
            OrderResponse orderResponse = orderService.placeOrder(orderRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
    }

    @Operation(summary = "Tüm Siparişleri Listeleme Metodu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Siparişler başarıyla getirildi",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class))})})
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders();
    }

    @Operation(summary = "ID'ye Göre Sipariş Listeleme Metodu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sipariş başarıyla getirildi",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Sipariş bulunamadı",
                    content = @Content)})
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
            OrderResponse orderResponse = orderService.getOrderById(id);
            return ResponseEntity.ok(orderResponse);
    }

    @Operation(summary = "Siparişi Silme Metodu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sipariş başarıyla silindi",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Sipariş bulunamadı",
                    content = @Content)})
    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteOrderById(@PathVariable Long id) {
            orderService.deleteOrderById(id);
            return ResponseEntity.ok("Order deleted successfully");
    }
}
