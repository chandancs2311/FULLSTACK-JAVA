package com.example.ShopEase.controller;

import com.example.ShopEase.dto.OrderInvoiceResponse;
import com.example.ShopEase.dto.PlaceOrderRequest;
import com.example.ShopEase.dto.UserAllOrdersInvoiceResponse;
import com.example.ShopEase.model.Order;
import com.example.ShopEase.model.User;
import com.example.ShopEase.repository.OrderRepository;
import com.example.ShopEase.repository.UserRepository;
import com.example.ShopEase.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/user/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    /**
     *   Place an order (User only)
     */
    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(@RequestBody PlaceOrderRequest request, Authentication authentication) {
        String email = authentication.getName();

        // Fetch the authenticated user using the email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Override the userId from token instead of trusting request body
        OrderInvoiceResponse response = orderService.placeOrder(user.getId(), request.getShippingAddress());

        return ResponseEntity.ok(response);
    }


    /**
     *   Get invoice for a single order
     *  - User: can access only their own order
     *  - Admin: can access any order
     */
    @GetMapping("/invoice/{orderId}")
    public ResponseEntity<?> getInvoice(@PathVariable Long orderId, Authentication authentication) {
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (currentUser.getRole().equals("ROLE_USER") &&
                !order.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not allowed to access this order.");
        }

        OrderInvoiceResponse invoice = orderService.getInvoice(orderId);
        return ResponseEntity.ok(invoice);
    }

    /**
     *  Admin: View all user orders
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllInvoices(Authentication authentication) {
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!currentUser.getRole().equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only admins can view all orders.");
        }

        List<Order> orders = orderRepository.findAll();
        List<OrderInvoiceResponse> responses = new ArrayList<>();

        for (Order order : orders) {
            responses.add(orderService.getInvoice(order.getOrderId())); // FIXED getOrderId()
        }

        return ResponseEntity.ok(responses);
    }
//all invoice total by userid
@GetMapping("/invoice/user/{userId}")
public ResponseEntity<?> getAllInvoicesByUser(
        @PathVariable Long userId,
        Authentication authentication) {

    String email = authentication.getName();
    User currentUser = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    if (currentUser.getRole().equals("ROLE_USER") && !userId.equals(currentUser.getId())) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
    }


    UserAllOrdersInvoiceResponse response = orderService.getAllInvoicesByUser(userId);
    return ResponseEntity.ok(response);
}

    /**
     *  User: View own order history
     */
    @GetMapping("/my-orders")
    public ResponseEntity<?> getMyOrders(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders = orderRepository.findByUserId(user.getId());
        List<OrderInvoiceResponse> responses = new ArrayList<>();

        for (Order order : orders) {
            responses.add(orderService.getInvoice(order.getOrderId())); // FIXED getOrderId()
        }

        return ResponseEntity.ok(responses);
    }
}
