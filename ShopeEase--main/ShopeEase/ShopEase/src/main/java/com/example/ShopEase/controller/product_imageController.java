package com.example.ShopEase.controller;

import com.example.ShopEase.model.Product;
import com.example.ShopEase.model.product_image;
import com.example.ShopEase.repository.ProductRepository;
import com.example.ShopEase.repository.product_imageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/product-images")
public class product_imageController {

    @Autowired
    private product_imageRepository imageRepository;

    @Autowired
    private ProductRepository productRepository;

    // Get image by productId
    @GetMapping("/{productId}")
    public ResponseEntity<byte[]> getImageByProductId(@PathVariable Long productId) throws IOException {
        List<product_image> images = imageRepository.findByProductId(productId);

        if (!images.isEmpty()) {
            product_image image = images.get(0);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(image.getImageData());
        }

        // Fallback to static default image like img1.jpg, img2.jpg, ...
        String filename = "static/images/products/img" + productId + ".jpg";
        ClassPathResource imgFile = new ClassPathResource(filename);

        if (!imgFile.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        byte[] imageBytes = StreamUtils.copyToByteArray(imgFile.getInputStream());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(imageBytes);
    }

    // Admin uploads a new image for a product
    @PostMapping("/upload/{productId}")
    public ResponseEntity<String> uploadImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file) throws IOException {

        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }

        product_image img = new product_image();
        img.setProduct(product);
        img.setImageData(file.getBytes());
        imageRepository.save(img);

        return ResponseEntity.ok("Image uploaded successfully");
    }

    //  Delete image (Optional - for admin panel)
    @DeleteMapping("/{imageId}")
    public ResponseEntity<String> deleteImage(@PathVariable Long imageId) {
        if (!imageRepository.existsById(imageId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found");
        }
        imageRepository.deleteById(imageId);
        return ResponseEntity.ok("Image deleted");
    }
}
