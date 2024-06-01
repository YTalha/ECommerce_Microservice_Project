package com.talha.microservices.inventory.dto;

public record InventoryRequest(String skuCode, Integer quantity) {
}
