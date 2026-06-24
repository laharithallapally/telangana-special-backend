package com.telanaganaspecial.dto;




import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class AddToCartRequestDto {
    @NotNull(message = "Product ID is required") // Changed from @NotNull to @NotNull(message = "Product ID is required")
    private Long productId;

    @Min(value = 1, message = "Quantity must be at least 1") // Changed from @Min to @Min(message = "Quantity must be at least 1")
    private Integer quantity;
}
