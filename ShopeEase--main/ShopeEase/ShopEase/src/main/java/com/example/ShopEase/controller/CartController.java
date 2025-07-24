package com.example.ShopEase.controller;

import com.example.ShopEase.dto.CartItemRequest;
import com.example.ShopEase.dto.CartItemResponse;
import com.example.ShopEase.model.CartItem;
import com.example.ShopEase.service.CartService;
import com.example.ShopEase.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final JwtService jwtService;

    // ------------------- ADD ITEM TO CART -------------------
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody CartItemRequest request,
                                       HttpServletRequest httpRequest) {

        Long tokenUserId = jwtService.extractUserIdFromToken(httpRequest);
        String email = jwtService.extractUsernameFromToken(httpRequest);

        if (!tokenUserId.equals(request.getUserId())) {
            return ResponseEntity.status(403)
                    .body("Access Denied: Token userId does not match request userId");
        }

        CartItemResponse response = cartService.addToCart(email, request);
        return ResponseEntity.ok(Map.of("message", "Item added successfully", "data", response));
    }

    // ------------------- GET CART ITEMS -------------------
    @GetMapping("/items")
    public ResponseEntity<?> getUserCart(HttpServletRequest httpRequest) {
        Long tokenUserId = jwtService.extractUserIdFromToken(httpRequest);
        return ResponseEntity.ok(cartService.getCartItemsByUserId(tokenUserId));
    }


    // ------------------- REMOVE ITEM FROM CART -------------------
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeFromCart(@RequestBody Map<String, Long> payload,
                                                 HttpServletRequest httpRequest) {

        Long userId = payload.get("userId");
        Long productId = payload.get("productId");

        Long tokenUserId = jwtService.extractUserIdFromToken(httpRequest);
        String role = jwtService.extractRoleFromToken(httpRequest);

        if (!tokenUserId.equals(userId) && !"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403)
                    .body("Access Denied: You can't remove from another user's cart.");
        }

        cartService.removeItem(userId, productId);
        return ResponseEntity.ok("Item removed from cart");
    }

    // ------------------- CLEAR CART -------------------
    @DeleteMapping("/clear/{userId}/{cartId}")
    public ResponseEntity<String> clearCart(@PathVariable Long userId,
                                            @PathVariable Long cartId,
                                            HttpServletRequest httpRequest) {

        Long tokenUserId = jwtService.extractUserIdFromToken(httpRequest);
        String role = jwtService.extractRoleFromToken(httpRequest);

        if (!tokenUserId.equals(userId) && !"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403)
                    .body("Access Denied: You can't clear another user's cart.");
        }

        List<CartItem> items = cartService.getCartItemsByUserAndCartId(userId, cartId);
        for (CartItem item : items) {
            cartService.removeItem(userId, item.getProduct().getId());
        }

        return ResponseEntity.ok("Cart cleared successfully");
    }
}
