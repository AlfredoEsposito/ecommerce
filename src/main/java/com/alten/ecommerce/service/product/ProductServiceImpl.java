package com.alten.ecommerce.service.product;

import com.alten.ecommerce.domain.Product;
import com.alten.ecommerce.domain.User;
import com.alten.ecommerce.enums.Categories;
import com.alten.ecommerce.exceptions.CustomException;
import com.alten.ecommerce.repositories.ProductRepository;
import com.alten.ecommerce.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final UserService userService;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, UserService userService) {
        this.productRepository = productRepository;
        this.userService = userService;
    }

    //Utility method: check if product name already exists
    public boolean checkProductName(Product product){
        List<Product> products = productRepository.findAll();
        if(products.stream().anyMatch(p -> p.getProductName().equalsIgnoreCase(product.getProductName().trim()))){
            return true;
        }else{
            return false;
        }
    }

    //Utility method: check if product already exists
    public boolean checkProduct(Product product){
        List<Product> products = productRepository.findAll();
        if(products.stream().anyMatch(p -> p.getProductName().equalsIgnoreCase(product.getProductName()) &&
                                           p.getCategory().equals(product.getCategory()) &&
                                           p.getPrice().equals(product.getPrice()) &&
                                           p.getDiscount().equals(product.getDiscount()) &&
                                           p.getQuantityInStock().equals(product.getQuantityInStock()) &&
                                           p.getPublishedAt().equals(product.getPublishedAt())
        )){
            return true;
        }else{
            return false;
        }
    }


    //Utility method: check quantity in stock
    public boolean checkQuantity(Product product){
        if(product.getQuantityInStock()>0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Product saveProduct(Product product) {
        log.info("Saving new product to database...");
        if(checkProductName(product)){
            log.error("Error! Product '{}' already exists", product.getProductName());
            throw new CustomException("Error! Product '"+product.getProductName()+"' already exists");
        }
        product.setId(0L);
        product.setPublishedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        productRepository.save(product);
        log.info("Product '{}' saved to database", product.getProductName());
        return product;
    }

    @Override
    public Product getProductById(Long id) {
        log.info("Getting product with id '{}' from database...", id);
        Optional<Product> optionalProduct = productRepository.findById(id);
        Product product;
        if(optionalProduct.isPresent()){
            product = optionalProduct.get();
        }else{
            log.error("Error! Product doesn't exist: id '{}' not found", id);
            throw new CustomException("Error! Product doesn't exist : id '"+id+"' not found");
        }
        return product;
    }

    @Override
    public Product getProductByProductName(String productName) {
        log.info("Getting product by name...");
        Optional<Product> optionalProduct = productRepository.findProductByProductName(productName.trim());
        Product product;
        if(optionalProduct.isPresent()){
            product = optionalProduct.get();
        }else{
            log.error("Error! Product doesn't exist: product name '{}' not found", productName);
            throw new CustomException("Error! Product doesn't exist: product name '"+productName+"' not found");
        }
        return product;
    }

    @Override
    public Set<Product> getProductsByUser(String username) {
        log.info("Getting all {}'s products...",username);
        User user = userService.getUserByUsername(username.trim());
        return productRepository.findProductsByUsers(user).get();
    }

    @Override
    public Set<Product> getProducts() {
        log.info("Getting all products...");
        return productRepository.findAll().stream().collect(Collectors.toSet());
    }

    @Override
    public Set<Product> getProductsByCategory(String category) {
        log.info("Getting all '{}' products", category);
        List<Product> products = productRepository.findAll();
        return products.stream().filter(product -> (
                product.getCategory().name().equalsIgnoreCase(String.valueOf(Categories.valueOf(category.trim())))
                )).collect(Collectors.toSet());
    }

    @Override
    public Product updateProduct(Product product) {
        log.info("Updating product '{}'...",product.getProductName());
        if(checkProduct(product)){
            log.error("Error! Product already exists: no changes have been made");
            throw new CustomException("Error! Product already exists: no changes have been made");
        }else {
            log.info("Product '{}' updated", product.getProductName());
            return productRepository.saveAndFlush(product);
        }
    }

    @Override
    public void deleteProductById(Long id) {
        log.info("Deleting product with id '{}'...", id);
        if(productRepository.findById(id).isEmpty()){
            log.error("Error! Product doesn't exist: product id '{}' not found", id);
            throw new CustomException("Error! Product doesn't exist: product id '"+id+"' not found");
        }else{
            productRepository.deleteById(id);
            log.info("Product '{}' deleted", id);
        }
    }
}
