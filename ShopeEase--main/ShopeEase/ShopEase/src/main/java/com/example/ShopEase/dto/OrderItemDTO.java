package com.example.ShopEase.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private String productName;
    private int quantity;
    private BigDecimal price;
    private  BigDecimal totalPrice;
}
