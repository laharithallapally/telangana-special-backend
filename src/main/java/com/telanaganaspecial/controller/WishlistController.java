package com.telanaganaspecial.controller;

import com.telanaganaspecial.dto.WishlistItemResponseDto;
import com.telanaganaspecial.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@Tag(name = "Wishlist API", description = "Save products for later")
@SecurityRequirement(name = "bearerAuth")
public class WishlistController {

    private final WishlistService wishlistService;

    @Operation(summary = "Get my wishlist")
    @GetMapping
    public ResponseEntity<List<WishlistItemResponseDto>> getWishlist(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(wishlistService.getWishlist(email));
    }

    @Operation(summary = "Add a product to my wishlist")
    @PostMapping("/{productId}")
    public ResponseEntity<WishlistItemResponseDto> addToWishlist(
            @AuthenticationPrincipal String email,
            @PathVariable Long productId) {
        return ResponseEntity.ok(wishlistService.addToWishlist(email, productId));
    }

    @Operation(summary = "Remove a product from my wishlist")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFromWishlist(
            @AuthenticationPrincipal String email,
            @PathVariable Long productId) {
        wishlistService.removeFromWishlist(email, productId);
        return ResponseEntity.noContent().build();
    }
}