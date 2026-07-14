package com.telanaganaspecial.repository;

import com.telanaganaspecial.entity.CartItem;
import com.telanaganaspecial.entity.Product;
import com.telanaganaspecial.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // get all cart items of a user
    List<CartItem> findByUser(User user);

    /**
     * Same as findByUser, but pulls the related Product in the SAME query
     * via JOIN FETCH — guarantees ONE database round-trip total, instead of
     * one extra query per cart item when buildCartResponse() reads product fields.
     */
    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.product WHERE ci.user = :user")
    List<CartItem> findByUserWithProduct(@Param("user") User user);

    // check if product already in cart
    Optional<CartItem> findByUserAndProduct(User user, Product product);

    // delete all cart items of a user (after placing order)
    void deleteByUser(User user);

    void deleteByProduct(Product product);
}