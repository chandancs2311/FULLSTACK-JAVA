package com.example.ShopEase.controller;

import com.example.ShopEase.dto.OrderItemRequest;
import com.example.ShopEase.service.JwtService;
import com.example.ShopEase.model.Order;

import com.example.ShopEase.model.OrderItem;
import com.example.ShopEase.service.OrderService;
import lombok.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Data
@RestController
@RequestMapping("/api/user/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final JwtService jwtService;

    @PostMapping("/place")
    public ResponseEntity<String> placeOrder(@RequestBody Order order, HttpServletRequest request) {
        Long tokenUserId = (long) jwtService.extractUserIdFromToken(request); // Convert to Long
        order.setUserId(tokenUserId); // set Long
        orderService.placeOrder(order);
        return ResponseEntity.ok("Order placed successfully");
    }


    @PostMapping("/add-item")
    public ResponseEntity<String> addItem(@RequestBody OrderItemRequest request, HttpServletRequest httpReq) {
        Long tokenUserId = (long) jwtService.extractUserIdFromToken(httpReq); // Convert to Long

        if (!orderService.isOrderOwnedByUser(request.getOrderId(), tokenUserId)) {
            return ResponseEntity.status(403).body("You can't add items to another user's order.");
        }

        orderService.addOrderItem(request);
        return ResponseEntity.ok("Item added successfully");
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getUserOrderHistory(@PathVariable Long userId, HttpServletRequest request) {
        String role = jwtService.extractRoleFromToken(request);
        Long tokenUserId = (long) jwtService.extractUserIdFromToken(request);

        if (!tokenUserId.equals(userId) && !"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).body("Access Denied: You can only view your own order history.");
        }

        return ResponseEntity.ok(orderService.getOrderHistoryByUser(userId));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId, HttpServletRequest request) {
        Long tokenUserId = (long) jwtService.extractUserIdFromToken(request); // Convert to Long

        if (!orderService.isOrderOwnedByUser(orderId, tokenUserId)) {
            return ResponseEntity.status(403).body("You can only delete your own order.");
        }

        orderService.deleteOrder(orderId);
        return ResponseEntity.ok("Order deleted successfully");
    }
}


