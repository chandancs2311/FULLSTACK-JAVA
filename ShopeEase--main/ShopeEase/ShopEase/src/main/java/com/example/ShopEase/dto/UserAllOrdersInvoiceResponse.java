package com.example.ShopEase.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAllOrdersInvoiceResponse {
    private Long userId;
    private String userName;
    private List<OrderInvoiceResponse> orders;
    private double grandTotal;
}
