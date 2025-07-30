package com.example.ShopEase.service;

import com.example.ShopEase.dto.OrderInvoiceResponse;
import com.example.ShopEase.dto.UserAllOrdersInvoiceResponse;

import java.util.List;

public interface OrderService {

    // For users to place an order from cart
    OrderInvoiceResponse placeOrder(Long userId, String shippingAddress);

    // For users to cancel their own order
    void cancelOrderByUser(Long orderId, Long userId);

    // For admin to cancel any order
    void cancelOrderByAdmin(Long orderId);
    void cancelOrder(Long orderId, Long userId);
    // For both: Gets full invoice (admin any, user own only)
    OrderInvoiceResponse getInvoice(Long orderId);

    // For user: Get their own order history
    List<OrderInvoiceResponse> getOrderHistoryByUser(Long userId);

    // For admin: Get all user orders
    List<OrderInvoiceResponse> getAllOrders();

    UserAllOrdersInvoiceResponse getAllInvoicesByUser(Long userId);

}
