package com.telanaganaspecial.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // list of items in this order
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    private String deliveryAddress;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

}