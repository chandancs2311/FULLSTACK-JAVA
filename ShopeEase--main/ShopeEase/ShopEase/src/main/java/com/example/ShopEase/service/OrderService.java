package com.example.ShopEase.service;

import com.example.ShopEase.dto.OrderHistoryResponse;
import com.example.ShopEase.dto.OrderItemDTO;
import com.example.ShopEase.model.Order;
import com.example.ShopEase.model.OrderItem;
import com.example.ShopEase.model.Product;
import com.example.ShopEase.model.User;
import com.example.ShopEase.repository.OrderItemRepository;
import com.example.ShopEase.repository.OrderRepository;
import com.example.ShopEase.repository.ProductRepository;
import com.example.ShopEase.repository.UserRepository;
import com.example.ShopEase.dto.OrderItemRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;

    //  Place Order
    public Order placeOrder(Order order) {
        return orderRepository.save(order);
    }

    //  Add Item to Order
    public void addOrderItem(OrderItemRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(request.getQuantity());
        item.setPrice(product.getPrice().doubleValue());
        item.setTotalPrice(product.getPrice().doubleValue() * request.getQuantity());

        orderItemRepository.save(item);
    }



    public List<OrderItem> getOrderItems(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getUserId() != userId) {
            throw new AccessDeniedException("You are not allowed to view this order");
        }

        return orderItemRepository.findByOrder(order);
    }


    //  Full History with Items (only for logged-in user)
    public List<OrderHistoryResponse> getOrderHistoryByUser(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);

        return orders.stream().map(order -> {
            OrderHistoryResponse dto = new OrderHistoryResponse();
            dto.setOrderId(order.getOrderId());
            dto.setOrderDate(order.getOrderDate());
            dto.setStatus(order.getStatus());
            dto.setTotalAmount(order.getTotalAmount());
            dto.setShippingAddress(order.getShippingAddress());

            List<OrderItemDTO> itemDTOs = order.getItems().stream().map(item -> {
                OrderItemDTO itemDTO = new OrderItemDTO();
                itemDTO.setProductName(item.getProduct().getName());
                itemDTO.setQuantity(item.getQuantity());
                itemDTO.setPrice(item.getPrice());
                itemDTO.setTotalPrice(item.getTotalPrice());
                return itemDTO;
            }).toList();

            dto.setItems(itemDTOs);
            return dto;
        }).toList();
    }


    // 🟢 Admin-only: View All Orders
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 🟢 Admin/User: Delete an order
    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    public boolean isOrderOwnedByUser(Long orderId, Long userId) {
        return orderRepository.findById(orderId)
                .map(order -> order.getUserId() == userId)
                .orElse(false);
    }

}
