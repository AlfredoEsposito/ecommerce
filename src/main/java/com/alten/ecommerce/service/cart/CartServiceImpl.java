package com.alten.ecommerce.service.cart;

import com.alten.ecommerce.domain.Cart;
import com.alten.ecommerce.domain.CartItems;
import com.alten.ecommerce.domain.Product;
import com.alten.ecommerce.domain.User;
import com.alten.ecommerce.exceptions.CustomException;
import com.alten.ecommerce.repositories.CartItemsRepository;
import com.alten.ecommerce.repositories.CartRepository;
import com.alten.ecommerce.service.cartitems.CartItemsService;
import com.alten.ecommerce.service.product.ProductService;
import com.alten.ecommerce.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemsService cartItemsService;
    private final ProductService productService;
    private final UserService userService;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository, CartItemsService cartItemsService, ProductService productService, UserService userService) {
        this.cartRepository = cartRepository;
        this.cartItemsService = cartItemsService;
        this.productService = productService;
        this.userService = userService;
    }

    @Override
    public Cart addProductToCart(Long productId, Long cartId) {
        log.info("Adding product to cart...");
        Optional<Cart> optionalCart = cartRepository.findById(cartId);
        Cart cart = new Cart();
        CartItems items = cartItemsService.getItemsByProductId(productId);
        User user = userService.getCurrentUser();
        List<Cart> carts = cartRepository.findAll();

        if(optionalCart.isPresent()){
            cart = optionalCart.get();
        }else{
            cart.setId(0L);
            cart.setTotalItems(0L);
            if(carts.stream().noneMatch(c -> c.getUser().getId().equals(user.getId()))){ //verifico che l'utente non abbia già un carrello
                cart.setUser(user); //setta l'utente al carrello per la relazione 1:1
            }else{
                //nel caso in cui un utente abbia già un carrello lancio un'eccezione
                log.error("Error! A user can have ony one cart");
                throw new CustomException("Error! A user can have only one cart");
            }
        }

        Product product = productService.getProductById(productId);
        if(product.getQuantityInStock()<=0){ //verifico la disponibilità del prodotto nel magazzino
            log.error("Error! Product '{}' currently not available", product.getProductName());
            throw new CustomException("Error! Product '"+product.getProductName()+"' currently not available");
        }else{
            cart.addProduct(product); //aggiungo il prodotto
            cart.setTotalItems(cart.getTotalItems()+1); //aggiorno il numero totale degli articoli del carrello
            items.setCart(cart); //setto il carrello il prodotto all'oggetto di tipo CartItems
            items.setProduct(product); //setto il prodotto il prodotto all'oggetto di tipo CartItems
            items.setQuantity(items.getQuantity()+1); //aggiorno il numero degli articoli divisi per prodotto nel carrello
            items.setTotalPerProduct((product.getPrice()-product.getDiscount())* items.getQuantity()); //calcolo la cifra totale per ogni prodotto presente nel carrello
            product.addCartItem(items);
            cart.addCartItem(items);
            cart.setTotalAmount(cart.getCartItems().stream().mapToDouble(CartItems::getTotalPerProduct).sum()); //calcolo la cifra totale degli articoli presenti nel carrello
            cartItemsService.saveCartItems(items); //salvo il carrello
            log.info("Product '{}' added to cart", product.getProductName());
        }
        return cart;
    }

    @Override
    public Cart deleteProductFromCart(Long productId, Long cartId) {
        log.info("Deleting product '{}' from cart '{}'...", productId, cartId);
        Optional<Cart> optionalCart = cartRepository.findById(cartId); //ottengo il carrello in base all'id
        Cart cart;
        CartItems items = cartItemsService.getItemsByProductId(productId); //ottengo l'articolo nel carrello in base all'id del prodotto

        if(optionalCart.isPresent()){
            cart = optionalCart.get();
        }else{
            log.error("Error! Cart doesn't exist: id '{}' not found", cartId);
            throw new CustomException("Error! Cart doesn't exist: id '"+cartId+"' not found");
        }

        //verifico la presenza del prodotto in base all'id nel carrello ottenuto in precedenza ed elimino il prodotto se presente
        Optional<Product> optionalProduct = cart.getProducts().stream().filter(p -> p.getId().equals(productId)).findFirst();
        Product product;
        if(optionalProduct.isPresent()){
            product = optionalProduct.get();
            if(items.getQuantity()>0){ //se la quantità di articoli per prodotto è >0 elimino il prodotto decrementando la quantità del prodotto e quella totale
                items.setQuantity(items.getQuantity()-1);
                items.setTotalPerProduct(items.getTotalPerProduct()-(product.getPrice()-product.getDiscount()));
                cart.setTotalItems(cart.getTotalItems()-1);
                cart.setTotalAmount(cart.getTotalAmount()-(product.getPrice()-product.getDiscount()));
                log.info("Product '{}' deleted from cart '{}'", productId, cartId);
                if(items.getQuantity()<=0){ //una volta che la quantità del prodotto è pari a 0, elimino effettivamente il prodotto dal carrello
                    items.setCart(null);
                    items.setProduct(null);
                    product.getCartItems().remove(items);
                    cart.getProducts().remove(product);
                    cartItemsService.deleteCartItems(items);
                    cartRepository.saveAndFlush(cart);
                    log.info("Product '{}' deleted from cart '{}'", productId, cartId);
                }
            }
            if(cart.getTotalItems()==0){
                deleteCartById(cartId); //se non è presente più nessun articolo nel carrello, questo viene automaticamente eliminato
            }
        }else{
            log.error("Error! Product doesn't exist: id '{}' not found", productId);
            throw new CustomException("Error! Product doesn't exist: id '"+productId+"' not found");
        }
        return cart;
    }

    @Override
    public void deleteCartById(Long cartId) {
        log.info("Deleting cart '{}'...", cartId);
        if(cartRepository.findById(cartId).isEmpty()){
            log.error("Error! Cart doesn't exist: id '{}' not found", cartId);
            throw new CustomException("Error! Cart doesn't exist: id '"+cartId+"' not found");
        }else{
            cartRepository.deleteAllByIdInBatch(Collections.singleton(cartId));
            log.info("Cart '{}' deleted", cartId);
        }
    }
}
