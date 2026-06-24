package com.telanaganaspecial.service.impl;

import com.telanaganaspecial.dto.CartItemResponseDto;
import com.telanaganaspecial.dto.OrderResponseDto;
import com.telanaganaspecial.dto.PlaceOrderRequestDto;
import com.telanaganaspecial.entity.*;
import com.telanaganaspecial.exception.UserNotFoundException;
import com.telanaganaspecial.repository.CartItemRepository;
import com.telanaganaspecial.repository.OrderRepository;
import com.telanaganaspecial.repository.UserRepository;
import com.telanaganaspecial.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OrderResponseDto placeOrder(String email, PlaceOrderRequestDto dto) {
        User user = getUser(email);

        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty! Add items before placing order.");
        }

        Double total = cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        Order order = Order.builder()
                .user(user)
                .totalAmount(total)
                .deliveryAddress(dto.getDeliveryAddress())
                .status(OrderStatus.PENDING)
                .build();

        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> OrderItem.builder()
                        .order(order)
                        .product(cartItem.getProduct())
                        .quantity(cartItem.getQuantity())
                        .price(cartItem.getProduct().getPrice())
                        .build())
                .toList();

        order.setItems(orderItems);
        orderRepository.save(order);
        cartItemRepository.deleteByUser(user);

        return mapToOrderResponse(order);
    }

    @Override
    public List<OrderResponseDto> getUserOrders(String email) {
        User user = getUser(email);
        return orderRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    @Override
    public OrderResponseDto getOrderById(Long orderId, String email) {
        return null;
    }

    // ✅ Fix — match your interface exactly
    public OrderResponseDto getOrderById(String email, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return mapToOrderResponse(order);
    }

    @Override
    public OrderResponseDto updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        orderRepository.save(order);
        return mapToOrderResponse(order);
    }

    @Override
    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    private OrderResponseDto mapToOrderResponse(Order order) {
        List<CartItemResponseDto> items = order.getItems().stream()
                .map(item -> CartItemResponseDto.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .productImage(item.getProduct().getImage())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .totalPrice(item.getPrice() * item.getQuantity())
                        .build())
                .toList();

        return OrderResponseDto.builder()
                .id(order.getId())
                .items(items)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .deliveryAddress(order.getDeliveryAddress())
                .createdAt(order.getCreatedAt())
                .build();
    }
}