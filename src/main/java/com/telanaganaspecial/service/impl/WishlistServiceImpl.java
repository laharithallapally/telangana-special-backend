package com.telanaganaspecial.service.impl;

import com.telanaganaspecial.dto.WishlistItemResponseDto;
import com.telanaganaspecial.entity.Product;
import com.telanaganaspecial.entity.User;
import com.telanaganaspecial.entity.WishlistItem;
import com.telanaganaspecial.exception.ProductNotFoundException;
import com.telanaganaspecial.exception.UserNotFoundException;
import com.telanaganaspecial.repository.ProductRepository;
import com.telanaganaspecial.repository.UserRepository;
import com.telanaganaspecial.repository.WishlistItemRepository;
import com.telanaganaspecial.service.WishlistService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistItemRepository wishlistItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public List<WishlistItemResponseDto> getWishlist(String email) {
        User user = getUser(email);
        return wishlistItemRepository.findByUserOrderByAddedAtDesc(user)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public WishlistItemResponseDto addToWishlist(String email, Long productId) {
        User user = getUser(email);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        // avoid duplicates — if already wishlisted, just return the existing entry
        WishlistItem item = wishlistItemRepository.findByUserAndProduct(user, product)
                .orElseGet(() -> {
                    WishlistItem newItem = WishlistItem.builder()
                            .user(user)
                            .product(product)
                            .build();
                    return wishlistItemRepository.save(newItem);
                });

        return mapToDto(item);
    }

    @Transactional
    @Override
    public void removeFromWishlist(String email, Long productId) {
        User user = getUser(email);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        wishlistItemRepository.deleteByUserAndProduct(user, product);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    private WishlistItemResponseDto mapToDto(WishlistItem item) {
        Product product = item.getProduct();
        return WishlistItemResponseDto.builder()
                .id(item.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productImage(product.getImage())
                .price(product.getPrice())
                .isVeg(product.getIsVeg())
                .available(product.getAvailable())
                .build();
    }
}