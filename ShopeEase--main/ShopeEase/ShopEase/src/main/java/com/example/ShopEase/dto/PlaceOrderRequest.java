package com.example.ShopEase.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceOrderRequest {
    private String shippingAddress;
    private Long userId;
}
