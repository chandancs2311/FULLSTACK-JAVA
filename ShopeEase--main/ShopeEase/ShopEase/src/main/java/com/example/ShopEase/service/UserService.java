package com.example.ShopEase.service;

import com.example.ShopEase.config.JwtUtil;
import com.example.ShopEase.dto.AdminUserCartOrderDTO;
import com.example.ShopEase.dto.CartDTO;
import com.example.ShopEase.dto.CartItemDTO;
import com.example.ShopEase.dto.OrderItemDTO;
import com.example.ShopEase.model.Cart;
import com.example.ShopEase.model.OrderItem;
import com.example.ShopEase.model.User;
import com.example.ShopEase.repository.CartRepository;
import com.example.ShopEase.repository.OrderItemRepository;
import com.example.ShopEase.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;
    private final OrderItemRepository orderItemRepository;
    CartDTO cartDTO = new CartDTO();
    // Register new user
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("ROLE_USER");
        } else if (!user.getRole().startsWith("ROLE_")) {
            user.setRole("ROLE_" + user.getRole().toUpperCase());
        }

        User savedUser = userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(savedUser);
        cart.setCreatedAt(new Date());
        cartRepository.save(cart);

        return savedUser;
    }

    public Map<String, Object> login(String email, String rawPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                String token = jwtUtil.generateToken(user.getEmail(), user.getRole(), user.getId());

                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("role", user.getRole());
                response.put("email", user.getEmail());
                return response;
            }
        }

        throw new RuntimeException("Invalid email or password");
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean isValidUser(String email, String rawPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        return userOpt.isPresent() &&
                passwordEncoder.matches(rawPassword, userOpt.get().getPassword());
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    //  ADMIN: Get all users' cart and order data
    public List<AdminUserCartOrderDTO> getAllUsersWithCartAndOrders() {
        List<User> users = userRepository.findAll();
        List<AdminUserCartOrderDTO> result = new ArrayList<>();

        for (User user : users) {
            AdminUserCartOrderDTO dto = new AdminUserCartOrderDTO();
            dto.setUserName(user.getName());
            dto.setUserId(user.getId());
            dto.setEmail(user.getEmail());

            //  Cart items
            List<CartItemDTO> cartItems = new ArrayList<>();
            if (user.getCart() != null && user.getCart().getCartItems() != null) {
                user.getCart().getCartItems().forEach(item -> {
                    CartItemDTO cartItem = new CartItemDTO();
                    cartItem.setUserId(user.getId());
                    cartItem.setProductId(item.getProduct().getId());
                    cartItem.setProductName(item.getProduct().getName());
                    cartItem.setQuantity(item.getQuantity());
                    cartItem.setPricePerUnit(item.getPrice());
                    cartItem.setTotalItemPrice(item.getTotalPrice());
                    cartItems.add(cartItem);
                });
            }

            //  Order items
            List<OrderItemDTO> orderItems = new ArrayList<>();
            List<OrderItem> items = orderItemRepository.findByOrder_UserId(user.getId());

            items.forEach(item -> {
                OrderItemDTO orderItem = new OrderItemDTO();
                orderItem.setProductName(item.getProduct().getName());
                orderItem.setQuantity(item.getQuantity());
                orderItem.setPrice(item.getPrice());
                orderItem.setTotalPrice(item.getTotalPrice());
                orderItems.add(orderItem);
            });

            dto.setCartItems(cartItems);
            dto.setOrderItems(orderItems);
            result.add(dto);
        }

        return result;
    }

}

