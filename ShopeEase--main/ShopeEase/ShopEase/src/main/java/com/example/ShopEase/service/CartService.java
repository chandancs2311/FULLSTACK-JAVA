package com.example.ShopEase.service;

import com.example.ShopEase.dto.CartItemRequest;
import com.example.ShopEase.dto.CartItemResponse;
import com.example.ShopEase.model.Cart;
import com.example.ShopEase.model.CartItem;
import com.example.ShopEase.model.User;

import java.util.List;

public interface CartService {

    CartItemResponse addToCart(String email, CartItemRequest request);

    Cart createCart(User user);

    List<CartItem> getCartItemsByUserAndCartId(Long userId, Long cartId);

    void removeItem(Long userId, Long productId);

    List<CartItem> getCartItemsByUserId(Long userId);
    ;
}
