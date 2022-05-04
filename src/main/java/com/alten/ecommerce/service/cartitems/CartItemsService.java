package com.alten.ecommerce.service.cartitems;

import com.alten.ecommerce.domain.CartItems;

public interface CartItemsService {

    CartItems saveCartItems(CartItems items);
    CartItems getItemsByProductId(Long productId);
    void deleteCartItems(CartItems items);
}
