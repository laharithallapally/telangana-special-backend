package com.telanaganaspecial.service;

import com.telanaganaspecial.dto.OrderResponseDto;
import com.telanaganaspecial.dto.PlaceOrderRequestDto;

import java.util.List;

public interface OrderService {

    OrderResponseDto placeOrder(String email, PlaceOrderRequestDto dto);

    OrderResponseDto placeOrderWithPayment(String email, PlaceOrderRequestDto dto,
                                           String razorpayOrderId, String razorpayPaymentId);

    List<OrderResponseDto> getUserOrders(String email);

    OrderResponseDto getOrderById(Long orderId, String email);

    OrderResponseDto updateOrderStatus(Long orderId, String status);

    List<OrderResponseDto> getAllOrders();
}