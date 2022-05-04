package com.alten.ecommerce.repositories;

import com.alten.ecommerce.domain.Product;
import com.alten.ecommerce.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {

    Optional<Product> findProductByProductName(String productName);
    Optional<List<Product>> findProductsByUsers(User user);
}
