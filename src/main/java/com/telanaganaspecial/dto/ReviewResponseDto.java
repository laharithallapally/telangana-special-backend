package com.telanaganaspecial.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponseDto {
    private Long id;
    private Long productId;
    private String userName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}