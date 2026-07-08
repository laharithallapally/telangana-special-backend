package com.telanaganaspecial.service;

import com.telanaganaspecial.dto.WishlistItemResponseDto;

import java.util.List;

public interface WishlistService {
    List<WishlistItemResponseDto> getWishlist(String email);
    WishlistItemResponseDto addToWishlist(String email, Long productId);
    void removeFromWishlist(String email, Long productId);
}