package com.telanaganaspecial.controller;

import com.telanaganaspecial.dto.AddToCartRequestDto;
import com.telanaganaspecial.dto.CartResponseDto;
import com.telanaganaspecial.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
/*
@CrossOrigin(origins = "http://localhost:5173")
*/
@Tag(name = "Cart API", description = "Cart management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class CartController {
    private final CartService cartService;

    @Operation(summary = "Get my cart")
    @GetMapping
    public ResponseEntity<CartResponseDto> getCart(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(cartService.getCart(email));
    }

    @Operation(summary = "Add item to cart")
    @PostMapping
    public ResponseEntity<CartResponseDto> addToCart(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody AddToCartRequestDto dto) {
        return ResponseEntity.ok(cartService.addToCart(email, dto));
    }

    @Operation(summary = "Update item quantity")
    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartResponseDto> updateQuantity(
            @AuthenticationPrincipal String email,
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.updateQuantity(email, cartItemId, quantity));
    }

    @Operation(summary = "Remove item from cart")
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeFromCart(
            @AuthenticationPrincipal String email,
            @PathVariable Long cartItemId) {
        cartService.removeFromCart(email, cartItemId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Clear entire cart")
    @DeleteMapping
    public ResponseEntity<Void> clearCart(
            @AuthenticationPrincipal String email) {
        cartService.clearCart(email);
        return ResponseEntity.noContent().build();
    }
}

