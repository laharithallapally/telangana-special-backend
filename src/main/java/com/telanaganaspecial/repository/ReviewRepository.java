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

    /**
     * Batched version - fetches average rating + review count for MANY products
     * in a single query, instead of looping 2 queries per product.
     * Each row: [0]=productId (Long), [1]=avgRating (Double), [2]=reviewCount (Long)
     */
    @Query("SELECT r.product.id, AVG(r.rating), COUNT(r) " +
            "FROM Review r " +
            "WHERE r.product.id IN :productIds " +
            "GROUP BY r.product.id")
    List<Object[]> findRatingStatsForProducts(@Param("productIds") List<Long> productIds);
}