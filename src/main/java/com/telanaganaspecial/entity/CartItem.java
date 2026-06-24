package com.telanaganaspecial.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // which user added this item
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // which product
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // how many quantity
    private Integer quantity;
}


