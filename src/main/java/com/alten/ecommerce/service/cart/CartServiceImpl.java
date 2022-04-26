package com.alten.ecommerce.service.cart;

import com.alten.ecommerce.domain.Cart;
import com.alten.ecommerce.domain.Product;
import com.alten.ecommerce.domain.User;
import com.alten.ecommerce.exceptions.CustomException;
import com.alten.ecommerce.repositories.CartRepository;
import com.alten.ecommerce.service.product.ProductService;
import com.alten.ecommerce.service.user.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductService productService;
    private final UserServiceImpl userService;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository, ProductService productService, UserServiceImpl userService) {
        this.cartRepository = cartRepository;
        this.productService = productService;
        this.userService = userService;
    }

    @Override
    public Cart addProductToCart(Long productId, Long cartId) {
        Optional<Cart> optionalCart = cartRepository.findById(cartId);
        Cart cart = new Cart();
        if(optionalCart.isPresent()){
            cart = optionalCart.get();
        }else{
            cart.setId(0L);
            cart.setTotalItems(0L);
            User user = userService.getCurrentUser();
            cart.setUser(user); //setta l'utente al carrello per la relazione 1:1
            if(cart.getUser().equals(user)){ //se è già presente un carrello associato all'utente corrente, lancio un'eccezione
                log.error("Error! A user can have only one cart");
                throw new CustomException("Error! A user can have only one cart");
            }
        }
        Product product = productService.getProductById(productId);
        if(product.getQuantityInStock()<=0){ //verifico la disponibilità del prodotto nel magazzino
            log.error("Error! Product '{}' currently not available", product.getProductName());
            throw new CustomException("Error! Product '"+product.getProductName()+"' currently not available");
        }else{
            //aggiungo il prodotto e aggiorno gli attributi relativi alla quantità e all'importo totale degli elementi nel carrello
            cart.addProduct(product);
            cart.setTotalItems(cart.getTotalItems()+1);
            cart.setTotalAmount(cart.getTotalAmount()*cart.getTotalItems());
            cartRepository.save(cart); //salvo il carrello
            log.info("Product '{}' added to cart", product.getProductName());
        }
        return cart;
    }

    @Override
    public Cart deleteProductFromCart(Long productId) {
        return null;
    }

    @Override
    public void deleteCart(Long cartId) {

    }
}
