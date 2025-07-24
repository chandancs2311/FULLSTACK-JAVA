package com.example.ShopEase.dto;

import lombok.*;

@Data
public class CartItemRequest {
    private Long userId;
    private Long productId;
    private int quantity;
    private Long cartId;  // Optional, can be auto-detected by userId
}
