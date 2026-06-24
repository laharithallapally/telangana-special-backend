package com.telanaganaspecial.repository;

import com.telanaganaspecial.entity.CartItem;
import com.telanaganaspecial.entity.Product;
import com.telanaganaspecial.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // get all cart items of a user
    List<CartItem> findByUser(User user);

    // check if product already in cart
    Optional<CartItem> findByUserAndProduct(User user, Product product);

    // delete all cart items of a user (after placing order)
    void deleteByUser(User user);

    void deleteByProduct(Product product);
}
