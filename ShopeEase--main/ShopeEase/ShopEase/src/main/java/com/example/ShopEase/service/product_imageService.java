package com.example.ShopEase.service;

import com.example.ShopEase.model.Product;
import com.example.ShopEase.model.product_image;
import com.example.ShopEase.repository.ProductRepository;
import com.example.ShopEase.repository.product_imageRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class product_imageService {

    @Autowired
    private product_imageRepository imageRepository;

    @Autowired
    private ProductRepository productRepository;

    @PostConstruct
    public void loadDefaultImages() throws IOException {
        for (int i = 1; i <= 40; i++) {
            Long productId = Long.valueOf(i);
            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) continue;

            // Check if images already exist to avoid duplicate insert
            if (!imageRepository.findByProductId(productId).isEmpty()) continue;

            String filename = "static/images/products/img" + i + ".jpg";
            ClassPathResource imgFile = new ClassPathResource(filename);

            try (InputStream input = imgFile.getInputStream()) {
                byte[] imageData = input.readAllBytes();
                product_image img = new product_image();
                img.setProduct(product);
                img.setImageData(imageData);
                imageRepository.save(img);
            }
        }
    }
}
