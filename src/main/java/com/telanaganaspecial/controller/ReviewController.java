package com.telanaganaspecial.controller;

import com.telanaganaspecial.dto.ReviewRequestDto;
import com.telanaganaspecial.dto.ReviewResponseDto;
import com.telanaganaspecial.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews API", description = "Product ratings and reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "Get all reviews for a product")
    @GetMapping
    public ResponseEntity<List<ReviewResponseDto>> getReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsForProduct(productId));
    }

    @Operation(summary = "Add or update my review for a product")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<ReviewResponseDto> addOrUpdateReview(
            @AuthenticationPrincipal String email,
            @PathVariable Long productId,
            @Valid @RequestBody ReviewRequestDto dto) {
        return ResponseEntity.ok(reviewService.addOrUpdateReview(email, productId, dto));
    }

    @Operation(summary = "Delete my review for a product")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping
    public ResponseEntity<Void> deleteReview(
            @AuthenticationPrincipal String email,
            @PathVariable Long productId) {
        reviewService.deleteReview(email, productId);
        return ResponseEntity.noContent().build();
    }
}