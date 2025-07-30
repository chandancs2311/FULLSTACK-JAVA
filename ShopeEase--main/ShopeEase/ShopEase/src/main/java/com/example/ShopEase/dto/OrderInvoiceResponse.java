package com.example.ShopEase.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderInvoiceResponse {
    private Long orderId;
    private Long userId;
    private String userName;
    private String email;
    private LocalDateTime orderDate;
    private String shippingAddress;
    private double totalAmount;
    private String status;
    private List<OrderItemResponse> items;

}
