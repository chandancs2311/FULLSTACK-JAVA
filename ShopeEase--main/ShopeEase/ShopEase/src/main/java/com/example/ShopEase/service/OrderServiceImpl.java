package com.example.ShopEase.service;

import com.example.ShopEase.dto.OrderInvoiceResponse;
import com.example.ShopEase.dto.OrderItemResponse;
import com.example.ShopEase.dto.UserAllOrdersInvoiceResponse;
import com.example.ShopEase.model.*;
import com.example.ShopEase.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private static final String STATUS_PLACED = "PLACED";
    private static final String STATUS_CANCELLED_BY_USER = "CANCELLED_BY_USER";
    private static final String STATUS_CANCELLED_BY_ADMIN = "CANCELLED_BY_ADMIN";
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;


    private final CartRepository cartRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            CartItemRepository cartItemRepository,
                            CartRepository cartRepository,
                            UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }


    @Override
    @Transactional
    public OrderInvoiceResponse placeOrder(Long userId, String shippingAddress) {

        // 1) Get the user's cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Cart not found for user"));

        // 2) Get all cart items for that cart
        List<CartItem> cartItems = cartItemRepository.findByCart_Id(cart.getId());
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalStateException("Your cart is empty. Cannot place an order.");
        }

        // 3) Compute total (null-safe)
        double total = cartItems.stream()
                .map(ci -> ci.getTotalPrice() == null ? BigDecimal.ZERO : ci.getTotalPrice())
                .mapToDouble(BigDecimal::doubleValue)
                .sum();

        // 4) Create Order
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(shippingAddress);
        order.setStatus(STATUS_PLACED);
        order.setTotalAmount(BigDecimal.valueOf(total));

        Order savedOrder = orderRepository.save(order);

        // 5) Create OrderItems
        List<OrderItem> orderItems = cartItems.stream().map(ci -> {
            OrderItem oi = new OrderItem();
            oi.setOrder(savedOrder); // IMPORTANT: @ManyToOne Order order
            oi.setProduct(ci.getProduct());
            oi.setQuantity(ci.getQuantity());
            oi.setPrice(ci.getPrice() != null ? ci.getPrice() : BigDecimal.ZERO);
            oi.setTotalPrice(ci.getTotalPrice() != null ? ci.getTotalPrice() : BigDecimal.ZERO);
            return oi;
        }).toList();

        orderItemRepository.saveAll(orderItems);

        // 6) Clear cart
        cartItemRepository.deleteByCartId(cart.getId());

        // 7) Return invoice DTO
        return toInvoice(savedOrder, orderItems);
    }


    @Override
    public OrderInvoiceResponse getInvoice(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        List<OrderItem> items = orderItemRepository.findByOrder_OrderId(orderId);

        return toInvoice(order, items);
    }
    @Override
    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        cancelOrderByUser(orderId, userId);
    }

    @Override
    public List<OrderInvoiceResponse> getOrderHistoryByUser(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(o -> toInvoice(o, orderItemRepository.findByOrder_OrderId(o.getOrderId())))
                .toList();
    }

    @Override
    public List<OrderInvoiceResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(o -> toInvoice(o, orderItemRepository.findByOrder_OrderId(o.getOrderId())))
                .toList();
    }

    @Override
    @Transactional
    public void cancelOrderByUser(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getUserId().equals(userId)) {
            throw new SecurityException("You can cancel only your own orders");
        }
        if (!STATUS_PLACED.equals(order.getStatus())) {
            throw new IllegalStateException("Order can only be cancelled while it's in PLACED status");
        }
        order.setStatus(STATUS_CANCELLED_BY_USER);
        orderRepository.save(order);
    }
    @Override
    public UserAllOrdersInvoiceResponse getAllInvoicesByUser(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);

        List<OrderInvoiceResponse> responses = orders.stream()
                .map(order -> toInvoice(order, orderItemRepository.findByOrder_OrderId(order.getOrderId())))
                .toList();

        double grandTotal = responses.stream()
                .mapToDouble(OrderInvoiceResponse::getTotalAmount)
                .sum();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserAllOrdersInvoiceResponse.builder()
                .userId(userId)
                .userName(user.getName())
                .orders(responses)
                .grandTotal(grandTotal)
                .build();
    }

    @Override
    @Transactional
    public void cancelOrderByAdmin(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (!STATUS_PLACED.equals(order.getStatus())) {
            throw new IllegalStateException("Order can only be cancelled while it's in PLACED status");
        }
        order.setStatus(STATUS_CANCELLED_BY_ADMIN);
        orderRepository.save(order);
    }

    private OrderInvoiceResponse toInvoice(Order order, List<OrderItem> items) {
        List<OrderItemResponse> itemResponses = items.stream()
                .map(i -> OrderItemResponse.builder()
                        .productId(i.getProduct().getId())
                        .productName(i.getProduct().getName())
                        .quantity(i.getQuantity())
                        .price(i.getPrice() != null ? i.getPrice() : BigDecimal.ZERO)
                        .totalPrice(i.getTotalPrice() != null ? i.getTotalPrice() : BigDecimal.ZERO)

                        .build())
                .toList();

        User user = userRepository.findById(order.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return OrderInvoiceResponse.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .orderDate(order.getOrderDate())
                .userName(user.getName())
                .email(user.getEmail())
                .shippingAddress(order.getShippingAddress())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount().doubleValue())
                .items(itemResponses)
                .build();
    }
}
