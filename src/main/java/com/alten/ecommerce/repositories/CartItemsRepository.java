package com.alten.ecommerce.repositories;

import com.alten.ecommerce.domain.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemsRepository extends JpaRepository<CartItems, Long> {

    Optional<CartItems> findCartItemsByProductId(Long productId);
}
