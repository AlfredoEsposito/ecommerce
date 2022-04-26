package com.alten.ecommerce.rest;

import com.alten.ecommerce.domain.Cart;
import com.alten.ecommerce.service.cart.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CartRestController {

    private final CartService cartService;

    @Autowired
    public CartRestController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/cart/addProduct/{productId}/toCart/{cartId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<Cart> addProductToCart(@PathVariable Long productId, @PathVariable Long cartId){
        return ResponseEntity.ok().body(cartService.addProductToCart(productId, cartId));
    }
}
