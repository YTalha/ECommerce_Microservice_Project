package com.talha.microservices.inventory.controller;

import com.talha.microservices.inventory.dto.InventoryRequest;
import com.talha.microservices.inventory.dto.InventoryResponse;
import com.talha.microservices.inventory.service.InventoryService;
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
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "Yeni Envanter Ekleme Metodu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Envanter başarıyla eklendi",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InventoryResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Geçersiz istek",
                    content = @Content)})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<InventoryResponse> addInventory(@RequestBody InventoryRequest inventoryRequest){
        InventoryResponse inventoryResponse = inventoryService.addInventory(inventoryRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryResponse);
    }

    @Operation(summary = "Belirli Envanteri Güncelleme Metodu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Envanter başarıyla güncellendi",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InventoryResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Envanter bulunamadı",
                    content = @Content)})
    @PutMapping("/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<InventoryResponse> updateInventory(@PathVariable("id") Long id, @RequestBody InventoryRequest inventoryRequest){
            InventoryResponse inventoryResponse = inventoryService.updateInventory(id, inventoryRequest);
            return ResponseEntity.ok(inventoryResponse);
    }

    @Operation(summary = "Tüm Envanterleri Listeleme Metodu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Envanterler başarıyla getirildi",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = InventoryResponse.class))})})
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> getAllInventory(){
        return inventoryService.getAllInventory();
    }

    @Operation(summary = "Stok Durumunu Kontrol Etme Metodu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stok durumu başarıyla kontrol edildi",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))})})
    @GetMapping("/stock")
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@RequestParam String skuCode, @RequestParam Integer quantity) {
        return inventoryService.isInStock(skuCode, quantity);
    }

    @Operation(summary = "Stok Azaltma Metodu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stok başarıyla azaltıldı",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Envanter bulunamadı",
                    content = @Content)})
    @PostMapping("/reduce")
    @ResponseStatus(HttpStatus.OK)
    public void reduceStock(@RequestParam String skuCode, @RequestParam Integer quantity) {
        inventoryService.reduceStock(skuCode, quantity);
    }

    @Operation(summary = "Envanteri Silme Metodu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Envanter başarıyla silindi",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Envanter bulunamadı",
                    content = @Content)})
    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteInventoryById(@PathVariable Long id) {
            inventoryService.deleteInventoryById(id);
            return ResponseEntity.ok("Inventory deleted successfully");
    }
}
