package com.telanaganaspecial.service.impl;

import com.telanaganaspecial.dto.ReviewRequestDto;
import com.telanaganaspecial.dto.ReviewResponseDto;
import com.telanaganaspecial.entity.Product;
import com.telanaganaspecial.entity.Review;
import com.telanaganaspecial.entity.User;
import com.telanaganaspecial.exception.ProductNotFoundException;
import com.telanaganaspecial.exception.UserNotFoundException;
import com.telanaganaspecial.repository.ProductRepository;
import com.telanaganaspecial.repository.ReviewRepository;
import com.telanaganaspecial.repository.UserRepository;
import com.telanaganaspecial.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public List<ReviewResponseDto> getReviewsForProduct(Long productId) {
        Product product = getProduct(productId);
        return reviewRepository.findByProductOrderByCreatedAtDesc(product)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @Transactional
    public ReviewResponseDto addOrUpdateReview(String email, Long productId, ReviewRequestDto dto) {
        User user = getUser(email);
        Product product = getProduct(productId);

        Review review = reviewRepository.findByUserAndProduct(user, product)
                .orElseGet(() -> Review.builder()
                        .user(user)
                        .product(product)
                        .createdAt(LocalDateTime.now())
                        .build());

        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        Review saved = reviewRepository.save(review);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public void deleteReview(String email, Long productId) {
        User user = getUser(email);
        Product product = getProduct(productId);
        reviewRepository.deleteByUserAndProduct(user, product);
    }

    private Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    private ReviewResponseDto mapToDto(Review review) {
        return ReviewResponseDto.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .userName(review.getUser().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}