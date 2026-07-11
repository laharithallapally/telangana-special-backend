package com.telanaganaspecial.repository;

import com.telanaganaspecial.entity.Product;
import com.telanaganaspecial.entity.Review;
import com.telanaganaspecial.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProductOrderByCreatedAtDesc(Product product);

    Optional<Review> findByUserAndProduct(User user, Product product);

    void deleteByUserAndProduct(User user, Product product);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double findAverageRating(@Param("productId") Long productId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId")
    Long countByProductId(@Param("productId") Long productId);
}