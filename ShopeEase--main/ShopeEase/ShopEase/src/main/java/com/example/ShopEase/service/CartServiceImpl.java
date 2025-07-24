package com.example.ShopEase.service;

import com.example.ShopEase.dto.CartItemRequest;
import com.example.ShopEase.dto.CartItemResponse;
import com.example.ShopEase.model.*;
import com.example.ShopEase.repository.*;
import com.example.ShopEase.model.CartItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public CartItemResponse addToCart(String email, CartItemRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> createCart(user));

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartAndProduct(cart, product);

        CartItem cartItem;
        if (existingItemOpt.isPresent()) {
            cartItem = existingItemOpt.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
        }

        CartItem savedItem = cartItemRepository.save(cartItem);

        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(savedItem.getQuantity()));

        return CartItemResponse.builder()
                .id(savedItem.getId().longValue())
                .productName(product.getName())
                .price(product.getPrice())
                .quantity(savedItem.getQuantity())
                .totalPrice(totalPrice)
                .cartId(cart.getId())
                .build();
    }


    @Override
    public Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }

    @Override
    public List<CartItem> getCartItemsByUserAndCartId(Long userId, Long cartId) {
        return cartItemRepository.findByCart_UserIdAndCartId(userId, cartId);
    }


    @Override
    public void removeItem(Long userId, Long productId) {
        CartItem item = cartItemRepository.findCartItemByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        cartItemRepository.delete(item);
    }
    @Override
    public List<CartItem> getCartItemsByUserId(Long userId) {
        return cartItemRepository.findByCart_UserId(userId);
    }


}
