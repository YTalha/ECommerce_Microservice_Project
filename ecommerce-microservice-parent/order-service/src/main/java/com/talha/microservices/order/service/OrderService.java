package com.talha.microservices.order.service;

import com.talha.microservices.order.client.InventoryClient;
import com.talha.microservices.order.dto.OrderRequest;
import com.talha.microservices.order.dto.OrderResponse;
import com.talha.microservices.order.exception.OrderNotFoundException;
import com.talha.microservices.order.exception.ProductNotInStockException;
import com.talha.microservices.order.model.Order;
import com.talha.microservices.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final InventoryClient inventoryClient;

    public OrderResponse placeOrder(OrderRequest orderRequest){

        var isProductInStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());

        if(isProductInStock){
                Order order = Order.builder()
                        .orderNumber(UUID.randomUUID().toString())
                        .price(orderRequest.price())
                        .skuCode(orderRequest.skuCode())
                        .quantity(orderRequest.quantity())
                        .build();
                orderRepository.save(order);
                inventoryClient.reduceStock(orderRequest.skuCode(), orderRequest.quantity());
                log.info("Order placed succesfully and stock reduced");
                return mapToOrderResponse(order);
        } else {
            String errorMessage = "Product with SkuCode " + orderRequest.skuCode() + " is not in stock";
            log.error(errorMessage);
            throw new ProductNotInStockException(errorMessage);
        }
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
        return mapToOrderResponse(order);
    }

    public void deleteOrderById(Long id) {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));
            orderRepository.delete(order);
            log.info("Order id: {} name: {} has been deleted", order.getId(), order.getSkuCode());
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return new OrderResponse(order.getId(), order.getOrderNumber(), order.getSkuCode(), order.getPrice(), order.getQuantity());
    }
}
