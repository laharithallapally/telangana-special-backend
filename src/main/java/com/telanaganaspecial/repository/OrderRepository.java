package com.telanaganaspecial.repository;

import com.telanaganaspecial.entity.Order;
import com.telanaganaspecial.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByCreatedAtDesc(User user);
}
