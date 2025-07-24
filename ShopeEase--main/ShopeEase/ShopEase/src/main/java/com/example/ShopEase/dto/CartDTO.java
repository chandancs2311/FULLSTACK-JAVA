package com.example.ShopEase.dto;

import lombok.Data;
import java.util.List;

@Data
public class CartDTO {
    private Long cartId;
    private Long userId;
    private List<CartItemDTO> items;
    private double totalPrice;
}
