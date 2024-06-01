package com.talha.microservices.order.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Component;
import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

@Slf4j
@Component
public interface InventoryClient {

    Logger log = LoggerFactory.getLogger(InventoryClient.class);

    @GetExchange("/api/inventory/stock")
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackIsInStock")
    @Retry(name = "inventory")
    boolean isInStock(@RequestParam String skuCode, @RequestParam Integer quantity);

    default boolean fallbackIsInStock(String code, Integer quantity, Throwable throwable) {
        log.info("Cannot get inventory for skucode {}, failure reason: {}", code, throwable.getMessage());
        return false;
    }

    @PostExchange("/api/inventory/reduce")
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackReduceStock")
    @Retry(name = "inventory")
    void reduceStock(@RequestParam String skuCode, @RequestParam Integer quantity);

    default void fallbackReduceStock(String skuCode, Integer quantity, Throwable throwable) {
        log.info("Cannot reduce stock for skucode {}, failure reason: {}", skuCode, throwable.getMessage());
    }
}
