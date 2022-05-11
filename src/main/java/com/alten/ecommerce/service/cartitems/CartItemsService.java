package com.alten.ecommerce.service.cartitems;

import com.alten.ecommerce.domain.CartItems;

import java.util.Set;

public interface CartItemsService {

    CartItems saveCartItems(CartItems items);
    CartItems getItemsByProductId(Long productId);
    void deleteCartItems(CartItems item);
    void deleteCartItemsByCartId(Long cartId);
}
