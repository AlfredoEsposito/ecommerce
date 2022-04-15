package com.alten.ecommerce.repositories;

import com.alten.ecommerce.domain.Product;
import com.alten.ecommerce.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface ProductRepository extends JpaRepository<Product,Long> {

    Optional<Product> findProductByProductName(String productName);
    Optional<Set<Product>> findProductsByUsers(User user);
}
