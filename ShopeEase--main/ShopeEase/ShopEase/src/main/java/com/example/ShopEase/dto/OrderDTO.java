package com.example.ShopEase.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long orderId;
    private Long userId;
    private List<OrderItemDTO> items;
    private double totalAmount;
    private String status; // e.g., PENDING, SHIPPED, DELIVERED
    private LocalDateTime orderDate;
}
