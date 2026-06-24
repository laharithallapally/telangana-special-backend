package com.telanaganaspecial.service.impl;

import com.telanaganaspecial.dto.AddToCartRequestDto;
import com.telanaganaspecial.dto.CartItemResponseDto;
import com.telanaganaspecial.dto.CartResponseDto;
import com.telanaganaspecial.entity.CartItem;
import com.telanaganaspecial.entity.Product;
import com.telanaganaspecial.entity.User;
import com.telanaganaspecial.exception.ProductNotFoundException;
import com.telanaganaspecial.exception.UserNotFoundException;
import com.telanaganaspecial.repository.CartItemRepository;
import com.telanaganaspecial.repository.ProductRepository;
import com.telanaganaspecial.repository.UserRepository;
import com.telanaganaspecial.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public CartResponseDto getCart(String email) {
        User user = getUser(email);
        List<CartItem> items = cartItemRepository.findByUser(user);
        return buildCartResponse(items);
    }



    @Override
    public CartResponseDto addToCart(String email, AddToCartRequestDto dto) {
        User user = getUser(email);
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(dto.getProductId()));

        // if product already in cart → just increase quantity
        CartItem cartItem = cartItemRepository
                .findByUserAndProduct(user, product)
                .orElse(CartItem.builder()
                        .user(user)
                        .product(product)
                        .quantity(0)
                        .build());

        cartItem.setQuantity(cartItem.getQuantity() + dto.getQuantity());
        cartItemRepository.save(cartItem);

        return buildCartResponse(cartItemRepository.findByUser(user));
    }

    @Override
    public CartResponseDto updateQuantity(String email, Long cartItemId, Integer quantity) {
        User user = getUser(email);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }

        return buildCartResponse(cartItemRepository.findByUser(user));
    }

    @Override
    public void removeFromCart(String email, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        cartItemRepository.delete(cartItem);
    }

    @Override
    public void clearCart(String email) {
        User user = getUser(email);
        cartItemRepository.deleteByUser(user);
    }

    // ---- helper methods ----

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    private CartResponseDto buildCartResponse(List<CartItem> items) {
        List<CartItemResponseDto> itemDtos = items.stream()
                .map(item -> CartItemResponseDto.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .productImage(item.getProduct().getImage())
                        .price(item.getProduct().getPrice())
                        .quantity(item.getQuantity())
                        .totalPrice(item.getProduct().getPrice() * item.getQuantity())
                        .build())
                .toList();

        Double grandTotal = itemDtos.stream()
                .mapToDouble(CartItemResponseDto::getTotalPrice)
                .sum();

        return CartResponseDto.builder()
                .items(itemDtos)
                .grandTotal(grandTotal)
                .totalItems(itemDtos.size())
                .build();
    }
}

