package com.talha.microservices.inventory.service;

import com.talha.microservices.inventory.dto.InventoryRequest;
import com.talha.microservices.inventory.dto.InventoryResponse;
import com.talha.microservices.inventory.exception.ProductNotFoundException;
import com.talha.microservices.inventory.exception.ProductNotInStockException;
import com.talha.microservices.inventory.model.Inventory;
import com.talha.microservices.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryResponse addInventory(InventoryRequest inventoryRequest) {
        try {
            Inventory inventory = Inventory.builder()
                    .skuCode(inventoryRequest.skuCode())
                    .quantity(inventoryRequest.quantity())
                    .build();
            inventoryRepository.save(inventory);
            log.info("Inventory added successfully");
            return mapToInventoryResponse(inventory);

        } catch (IllegalArgumentException e) {
            log.error("Failed to add inventory: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while adding inventory: {}", e.getMessage());
            throw new RuntimeException("Failed to add inventory", e);
        }
    }

    public List<InventoryResponse> getAllInventory() {
            return inventoryRepository.findAll()
                    .stream()
                    .map(this::mapToInventoryResponse)
                    .collect(Collectors.toList());
    }

    public InventoryResponse updateInventory(Long id, InventoryRequest inventoryRequest) {
            Inventory inventory = inventoryRepository.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException("Inventory not found with id: " + id));

            inventory.setSkuCode(inventoryRequest.skuCode());
            inventory.setQuantity(inventoryRequest.quantity());
            inventoryRepository.save(inventory);
            log.info("Inventory id: {} name: {} is updated", inventory.getId(), inventory.getSkuCode());
            return mapToInventoryResponse(inventory);
    }

    public void deleteInventoryById(Long id) {
            Inventory inventory = inventoryRepository.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException("Inventory not found with id: " + id));
            inventoryRepository.delete(inventory);
            log.info("Inventory id: {} name: {} has been deleted", inventory.getId(), inventory.getSkuCode());
    }

    public boolean isInStock(String skuCode, Integer quantity) {
        return inventoryRepository.existsBySkuCodeAndQuantityIsGreaterThanEqual(skuCode,quantity);
    }

    public void reduceStock(String skuCode, Integer quantity) {
        Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with SkuCode: " + skuCode));
        if (inventory.getQuantity() < quantity) {
            throw new ProductNotInStockException("Not enough stock for product with SkuCode: " + skuCode);
        }
        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventoryRepository.save(inventory);
        log.info("Stock for product with SkuCode: {} has been reduced by {}", skuCode, quantity);
    }

    private InventoryResponse mapToInventoryResponse(Inventory inventory) {
        return new InventoryResponse(inventory.getId(), inventory.getSkuCode(), inventory.getQuantity());
    }


}
