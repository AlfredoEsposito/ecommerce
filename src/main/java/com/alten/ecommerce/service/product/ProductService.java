package com.alten.ecommerce.service.product;

import com.alten.ecommerce.domain.Product;

import java.util.List;

public interface ProductService {

    Product saveProduct(Product product);
    Product getProductById(Long id);
    Product getProductByProductName(String productName);
    List<Product> getProductsByUser(String username);
    List<Product> getProducts();
    List<Product> getProductsByCategory(String category);
    Product updateProduct(Product product);
    void deleteProductById(Long id);
}
