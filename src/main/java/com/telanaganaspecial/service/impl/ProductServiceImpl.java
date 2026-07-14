package com.telanaganaspecial.service.impl;

import com.telanaganaspecial.dto.ProductRequestDto;
import com.telanaganaspecial.dto.ProductResponseDto;
import com.telanaganaspecial.entity.Product;
import com.telanaganaspecial.exception.ProductNotFoundException;
import com.telanaganaspecial.mapper.ProductMapper;
import com.telanaganaspecial.repository.CartItemRepository;
import com.telanaganaspecial.repository.OrderItemRepository;
import com.telanaganaspecial.repository.ProductRepository;
import com.telanaganaspecial.repository.ReviewRepository;
import com.telanaganaspecial.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public ProductResponseDto addProduct(ProductRequestDto dto) {
        Product product = ProductMapper.toEntity(dto);
        Product saved = productRepository.save(product);
        return ProductMapper.toDto(saved);
    }

    @Override
    public List<ProductResponseDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return mapWithBatchedRatings(products);
    }

    @Override
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return toDtoWithRating(product);
    }

    @Override
    public List<ProductResponseDto> getProductsByCategory(String category) {
        List<Product> products = productRepository.findByCategory(category);
        return mapWithBatchedRatings(products);
    }

    /**
     * Fetches rating stats for a whole list of products in ONE query
     * (instead of 2 queries per product), then maps each product using
     * an in-memory lookup. This is what fixes the N+1 slowdown.
     */
    private List<ProductResponseDto> mapWithBatchedRatings(List<Product> products) {
        if (products.isEmpty()) {
            return List.of();
        }

        List<Long> productIds = products.stream().map(Product::getId).toList();
        List<Object[]> statsRows = reviewRepository.findRatingStatsForProducts(productIds);

        Map<Long, Double> avgByProductId = new HashMap<>();
        Map<Long, Long> countByProductId = new HashMap<>();
        for (Object[] row : statsRows) {
            Long productId = (Long) row[0];
            Double avg = (Double) row[1];
            Long count = (Long) row[2];
            avgByProductId.put(productId, avg);
            countByProductId.put(productId, count);
        }

        return products.stream()
                .map(product -> {
                    Double avg = avgByProductId.getOrDefault(product.getId(), null);
                    Long count = countByProductId.getOrDefault(product.getId(), 0L);
                    return ProductMapper.toDto(product, avg, count);
                })
                .toList();
    }

    private ProductResponseDto toDtoWithRating(Product product) {
        Double avg = reviewRepository.findAverageRating(product.getId());
        Long count = reviewRepository.countByProductId(product.getId());
        return ProductMapper.toDto(product, avg, count);
    }

    @Override
    public ProductResponseDto updateProduct(Long id, ProductRequestDto dto) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setImage(dto.getImage());
        existing.setAvailable(dto.getAvailable() == null ? existing.getAvailable() : dto.getAvailable());
        existing.setCategory(dto.getCategory());
        existing.setStock(dto.getStock());
        existing.setIsVeg(dto.getIsVeg() == null ? existing.getIsVeg() : dto.getIsVeg());

        Product updated = productRepository.save(existing);
        return ProductMapper.toDto(updated, reviewRepository.findAverageRating(updated.getId()), reviewRepository.countByProductId(updated.getId()));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // delete from cart_items first to avoid foreign key error
        cartItemRepository.deleteByProduct(product);

        orderItemRepository.deleteByProduct(product);
        // then delete product
        productRepository.delete(product);
    }
}