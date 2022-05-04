package com.alten.ecommerce.service.cartitems;

import com.alten.ecommerce.domain.CartItems;
import com.alten.ecommerce.repositories.CartItemsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@Slf4j
public class CartItemsServiceImpl implements CartItemsService{

    private final CartItemsRepository cartItemsRepository;

    @Autowired
    public CartItemsServiceImpl(CartItemsRepository cartItemsRepository) {
        this.cartItemsRepository = cartItemsRepository;
    }

    @Override
    public CartItems saveCartItems(CartItems items) {
        return cartItemsRepository.save(items);
    }

    @Override
    public CartItems getItemsByProductId(Long productId) {
        Optional<CartItems> optionalCartItems = cartItemsRepository.findCartItemsByProductId(productId);
        CartItems cartItems;
        if(optionalCartItems.isPresent()) {
            cartItems = optionalCartItems.get();
        }else{
            cartItems = new CartItems();
            cartItems.setQuantity(0L);
            this.saveCartItems(cartItems);
        }
        return cartItems;
    }

    @Override
    public void deleteCartItems(CartItems items) {
        cartItemsRepository.delete(items);
    }
}
