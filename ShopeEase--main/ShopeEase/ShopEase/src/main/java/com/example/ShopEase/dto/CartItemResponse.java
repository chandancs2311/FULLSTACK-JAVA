package com.example.ShopEase.dto;

import lombok.*;
import java.math.BigDecimal;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartItemResponse {
    private Long id;
    private String productName;
    private BigDecimal price;
    private int quantity;
    private BigDecimal totalPrice;
    private Long cartId;

}

