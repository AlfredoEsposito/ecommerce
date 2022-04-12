package com.alten.ecommerce.service.product;

import com.alten.ecommerce.domain.Product;
import com.alten.ecommerce.exceptions.CustomException;
import com.alten.ecommerce.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@Slf4j
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    //Utility method: check if product already exists
    public boolean checkProduct(Product product){
        List<Product> products = productRepository.findAll();
        if(products.stream().anyMatch(p -> p.getProductName().equalsIgnoreCase(product.getProductName().trim()))){
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
        if(checkProduct(product)){
            log.error("Error! Product '{}' already exists", product.getProductName());
            throw new CustomException("Error! Product '"+product.getProductName()+"' already exists");
        }
        product.setId(0L);
        productRepository.save(product);
        log.info("Product '{}' saved to database", product.getProductName());
        return product;
    }

    @Override
    public Product getProductById(Long id) {
        log.info("Getting product with id '{}' from database...", id);
        Optional<Product> optionalProduct = productRepository.findById(id);
        Product product = null;
        if(optionalProduct.isPresent()){
            product = optionalProduct.get();
        }else{
            log.error("Error! Product does not exists: id '{}' not found", id);
            throw new CustomException("Error! Product does not exists: id '"+id+"' not found");
        }
        return product;
    }

    @Override
    public Product getProductByProductName(String productName) {
        return null;
    }

    @Override
    public Set<Product> getProducts() {
        return null;
    }

    @Override
    public Set<Product> getProductsByCategory(String category) {
        return null;
    }

    @Override
    public Product updateProduct(Product product) {
        return null;
    }

    @Override
    public void deleteProductById(Long id) {

    }
}
