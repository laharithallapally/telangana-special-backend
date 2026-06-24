package com.telanaganaspecial.service;

import com.telanaganaspecial.dto.AddToCartRequestDto;
import com.telanaganaspecial.dto.CartResponseDto;

public interface CartService {
    CartResponseDto getCart(String email);
    CartResponseDto addToCart(String email, AddToCartRequestDto dto);
    CartResponseDto updateQuantity(String email, Long cartItemId, Integer quantity);
    void removeFromCart(String email, Long cartItemId);
    void clearCart(String email);
}
