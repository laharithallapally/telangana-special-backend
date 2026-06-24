package com.telanaganaspecial.repository;

import com.telanaganaspecial.entity.OrderItem;
import com.telanaganaspecial.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {
    void deleteByProduct(Product product);

    
}
