package com.alten.ecommerce.service.cart;

import com.alten.ecommerce.domain.Cart;
import com.alten.ecommerce.domain.Product;

public interface CartService {

    Cart addProductToCart(Long productId, Long cartId);
    Cart deleteProductFromCart(Long productId);
    void deleteCart(Long cartId);
}
