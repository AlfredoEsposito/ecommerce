package com.alten.ecommerce.repositories;

import com.alten.ecommerce.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface CartRepository extends JpaRepository<Cart, Long> {

    void deleteCartByUserId(Long userId);
}
