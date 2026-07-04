package com.telanaganaspecial.controller;

import com.telanaganaspecial.dto.OrderResponseDto;
import com.telanaganaspecial.dto.PlaceOrderRequestDto;
import com.telanaganaspecial.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
/*
@CrossOrigin(origins = "http://localhost:5173")
*/
@Tag(name = "Order API", description = "Order management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Place an order from cart")
    @PostMapping
    public ResponseEntity<OrderResponseDto> placeOrder(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody PlaceOrderRequestDto dto) {
        return ResponseEntity.ok(orderService.placeOrder(email, dto));
    }

    @Operation(summary = "Get my orders")
    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getMyOrders(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(orderService.getUserOrders(email));
    }

    @Operation(summary = "Get order by ID")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderById(
            @AuthenticationPrincipal String email,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId, email));
    }

    // Only ADMIN can update order status
    @Operation(summary = "Update order status (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }
    @Operation(summary = "Get all orders (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}

