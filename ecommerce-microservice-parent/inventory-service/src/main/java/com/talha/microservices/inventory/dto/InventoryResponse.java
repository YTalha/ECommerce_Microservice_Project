package com.talha.microservices.inventory.dto;

public record InventoryResponse(Long id, String skuCode, Integer quantity) {
}
