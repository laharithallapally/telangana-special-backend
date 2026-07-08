package com.telanaganaspecial.entity;


import jakarta.persistence.*;
import lombok.*;

    @Entity
    @Table(name="products")

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    public class Product {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column(nullable = false)
        private String name;

        @Column(length = 1000)
        private String description;
        @Column(nullable = false)
        private Double price;
        @Column(length = 1000)
        private String image;
        
        @Builder.Default
        @Column(nullable = false)
        private Boolean available = true;

        @Column(nullable = false)
        private String category;
        @Column(nullable = false)
        private Integer stock;

        @Column(nullable = false)
        private Boolean isVeg;

    }

