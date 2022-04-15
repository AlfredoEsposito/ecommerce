package com.alten.ecommerce.service.product;

import com.alten.ecommerce.domain.Product;

import java.util.Set;

public interface ProductService {

    Product saveProduct(Product product);
    Product getProductById(Long id);
    Product getProductByProductName(String productName);
    Set<Product> getProductsByUser(String username);
    Set<Product> getProducts();
    Set<Product> getProductsByCategory(String category);
    Product updateProduct(Product product);
    void deleteProductById(Long id);
}
