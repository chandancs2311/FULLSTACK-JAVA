package com.example.ShopEase.dto;

import lombok.Data;

import java.util.List;

@Data
public class AdminUserCartOrderDTO {
    private Long userId;
    private String userName;
    private String email;

    private List<CartItemDTO> cartItems;
    private List<OrderItemDTO> orderItems;
}
