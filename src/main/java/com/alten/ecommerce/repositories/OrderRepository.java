package com.alten.ecommerce.repositories;

import com.alten.ecommerce.domain.Order;
import com.alten.ecommerce.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<List<Order>> findOrdersByUser(User user);
}
