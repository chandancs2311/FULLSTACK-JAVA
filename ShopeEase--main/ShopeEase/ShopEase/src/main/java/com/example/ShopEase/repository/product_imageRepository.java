package com.example.ShopEase.repository;

import com.example.ShopEase.model.product_image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface product_imageRepository extends JpaRepository<product_image, Long> {
    List<product_image> findByProductId(Long productId);
}
