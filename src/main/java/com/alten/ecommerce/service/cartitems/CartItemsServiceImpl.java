package com.alten.ecommerce.service.cartitems;

import com.alten.ecommerce.domain.Cart;
import com.alten.ecommerce.domain.CartItems;
import com.alten.ecommerce.exceptions.CustomException;
import com.alten.ecommerce.repositories.CartItemsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    public void deleteCartItems(CartItems item) {
        cartItemsRepository.delete(item);
    }

    @Override
    public void deleteCartItemsByCartId(Long cartId) {
        List<CartItems> items = cartItemsRepository.findCartItemsByCartId(cartId);
        if(items.isEmpty()){
            log.error("Error! Cart items not found for cart '{}'", cartId);
            throw new CustomException("Error! Cart items not found for cart '"+cartId+"'");
        }else{
            for(CartItems item : items){
                item.setCart(null);
                cartItemsRepository.delete(item);
            }
        }
    }
}
