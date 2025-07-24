package com.example.ShopEase.dto;
import lombok.*;
@Data
public class OrderItemRequest {
    private Long orderId;
    private Long productId;
    private int quantity;
}

