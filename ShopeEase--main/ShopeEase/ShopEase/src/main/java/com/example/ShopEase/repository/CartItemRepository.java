package com.example.ShopEase.repository;

import com.example.ShopEase.model.Cart;
import com.example.ShopEase.model.CartItem;
import com.example.ShopEase.model.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    List<CartItem> findByCart_User_IdAndCart_Id(Long userId, Long cartId);


    @Query("SELECT c FROM CartItem c WHERE c.cart.user.id = :userId AND c.product.id = :productId")
    Optional<CartItem> findCartItemByUserIdAndProductId(
            @Param("userId") Long userId,
            @Param("productId") Long productId
    );


    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem c WHERE c.cart.id = :cartId")
    void deleteByCartId(@Param("cartId") Long cartId);

    List<CartItem> findByCart_UserId(Long userId);
    List<CartItem> findByCart_Id(Long cartId);


}
