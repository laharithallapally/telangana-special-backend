package com.telanaganaspecial.service.impl;

import com.telanaganaspecial.dto.CartItemResponseDto;
import com.telanaganaspecial.dto.OrderResponseDto;
import com.telanaganaspecial.dto.PlaceOrderRequestDto;
import com.telanaganaspecial.entity.*;
import com.telanaganaspecial.exception.AddressNotFoundException;
import com.telanaganaspecial.exception.UserNotFoundException;
import com.telanaganaspecial.repository.AddressRepository;
import com.telanaganaspecial.repository.CartItemRepository;
import com.telanaganaspecial.repository.OrderRepository;
import com.telanaganaspecial.repository.UserRepository;
import com.telanaganaspecial.service.NotificationService;
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
    private final AddressRepository addressRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public OrderResponseDto placeOrder(String email, PlaceOrderRequestDto dto) {
        User user = getUser(email);

        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty! Add items before placing order.");
        }

        String resolvedAddress = resolveDeliveryAddress(user, dto);

        Double total = cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        Order order = Order.builder()
                .user(user)
                .totalAmount(total)
                .deliveryAddress(resolvedAddress)
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

        notificationService.notifyUser(
                user,
                NotificationService.orderPlacedCustomerMessage(user.getName())
        );

        notificationService.sendPush(
                user.getId(),
                "Telangana Special",
                NotificationService.orderPlacedCustomerMessage(user.getName())
        );

        notificationService.notifyAllAdmins(
                NotificationService.orderPlacedAdminMessage(user.getName(), total)
        );

        return mapToOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderResponseDto placeOrderWithPayment(String email, PlaceOrderRequestDto dto,
                                                  String razorpayOrderId, String razorpayPaymentId) {
        User user = getUser(email);

        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty! Add items before placing order.");
        }

        String resolvedAddress = resolveDeliveryAddress(user, dto);

        Double total = cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        Order order = Order.builder()
                .user(user)
                .totalAmount(total)
                .deliveryAddress(resolvedAddress)
                .status(OrderStatus.PENDING)
                .razorpayOrderId(razorpayOrderId)
                .razorpayPaymentId(razorpayPaymentId)
                .paymentStatus("PAID")
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

        notificationService.notifyUser(
                user,
                NotificationService.orderPlacedCustomerMessage(user.getName())
        );

        notificationService.sendPush(
                user.getId(),
                "Telangana Special",
                NotificationService.orderPlacedCustomerMessage(user.getName())
        );

        notificationService.notifyAllAdmins(
                NotificationService.orderPlacedAdminMessage(user.getName(), total)
        );

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

        notificationService.notifyUser(
                order.getUser(),
                NotificationService.statusMessage(order.getUser().getName(), status)
        );

        notificationService.sendPush(
                order.getUser().getId(),
                "Telangana Special",
                NotificationService.statusMessage(order.getUser().getName(), status)
        );

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

    private String resolveDeliveryAddress(User user, PlaceOrderRequestDto dto) {
        if (dto.getAddressId() != null) {
            Address address = addressRepository.findByIdAndUserId(dto.getAddressId(), user.getId())
                    .orElseThrow(() -> new AddressNotFoundException(dto.getAddressId()));

            return address.getLabel() + " - " + address.getAddressLine() + ", "
                    + address.getCity() + ", " + address.getState() + " - " + address.getPincode();
        }

        if (dto.getDeliveryAddress() != null && !dto.getDeliveryAddress().isBlank()) {
            return dto.getDeliveryAddress();
        }

        throw new RuntimeException("Delivery address is required. Provide addressId or deliveryAddress.");
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