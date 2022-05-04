package com.alten.ecommerce.rest;

import com.alten.ecommerce.domain.Cart;
import com.alten.ecommerce.service.cart.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CartRestController {

    private final CartService cartService;

    @Autowired
    public CartRestController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/carts/addProduct/{productId}/toCart/{cartId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<Cart> addProductToCart(@PathVariable Long productId, @PathVariable Long cartId){
        return ResponseEntity.ok().body(cartService.addProductToCart(productId, cartId));
    }

    @DeleteMapping("/carts/removeProduct/{productId}/fromCart/{cartId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<Cart> deleteProductFromCart(@PathVariable Long productId, @PathVariable Long cartId){
        return ResponseEntity.ok().body(cartService.deleteProductFromCart(productId, cartId));
    }

    @DeleteMapping("/carts/id/{cartId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<String> deleteCartById(@PathVariable Long cartId){
        cartService.deleteCartById(cartId);
        return ResponseEntity.ok().body("Cart '"+cartId+"' deleted");
    }
}
