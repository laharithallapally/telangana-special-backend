package com.telanaganaspecial.service;

import com.telanaganaspecial.dto.ReviewRequestDto;
import com.telanaganaspecial.dto.ReviewResponseDto;

import java.util.List;

public interface ReviewService {
    List<ReviewResponseDto> getReviewsForProduct(Long productId);
    ReviewResponseDto addOrUpdateReview(String email, Long productId, ReviewRequestDto dto);
    void deleteReview(String email, Long productId);
}