package com.alten.ecommerce.rest;

import com.alten.ecommerce.domain.Product;
import com.alten.ecommerce.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class ProductRestController {

    private final ProductService productService;

    @Autowired
    public ProductRestController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/products")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<Product> saveProduct(@RequestBody Product product){
        return ResponseEntity.ok().body(productService.saveProduct(product));
    }

    @GetMapping("/products/id/{productId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CUSTOMER','ROLE_SELLER')")
    public ResponseEntity<Product> getProductById(@PathVariable Long productId){
        return ResponseEntity.ok().body(productService.getProductById(productId));
    }

    @GetMapping("/products/productName/{productName}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CUSTOMER','ROLE_SELLER')")
    public ResponseEntity<Product> getProductByProductName(@PathVariable String productName){
        return ResponseEntity.ok().body(productService.getProductByProductName(productName));
    }

    @GetMapping("/products/user/{username}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CUSTOMER','ROLE_SELLER')")
    public ResponseEntity<List<Product>> getProductsByUser(@PathVariable String username){
        return ResponseEntity.ok().body(productService.getProductsByUser(username));
    }

    @GetMapping("/products/category/{category}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CUSTOMER','ROLE_SELLER')")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category){
        return ResponseEntity.ok().body(productService.getProductsByCategory(category));
    }

    @GetMapping("/products")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CUSTOMER','ROLE_SELLER')")
    public ResponseEntity<List<Product>> getAllProducts(){
        return ResponseEntity.ok().body(productService.getProducts());
    }

    @PutMapping("/products")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<Product> updateProduct(@RequestBody Product product){
        return ResponseEntity.ok().body(productService.updateProduct(product));
    }

    @DeleteMapping("/products/id/{productId}")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<String> deleteProductById(@PathVariable Long productId){
        productService.deleteProductById(productId);
        return ResponseEntity.ok().body("Product '"+productId+"' deleted");
    }
}
