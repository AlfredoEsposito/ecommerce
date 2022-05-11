package com.alten.ecommerce.service.order;

import com.alten.ecommerce.domain.*;
import com.alten.ecommerce.enums.Status;
import com.alten.ecommerce.exceptions.CustomException;
import com.alten.ecommerce.repositories.OrderRepository;
import com.alten.ecommerce.service.cart.CartService;
import com.alten.ecommerce.service.cartitems.CartItemsService;
import com.alten.ecommerce.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final CartService cartService;
    private final CartItemsService cartItemsService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, UserService userService, CartService cartService, CartItemsService cartItemsService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.cartService = cartService;
        this.cartItemsService = cartItemsService;
    }

    @Override
    public Order saveOrder() {
        User user = userService.getCurrentUser();
        Cart cart = user.getCart();
        Order order = new Order();
        if(cart == null){
            log.error("Error! User's cart is empty");
            throw new CustomException("Error! User's cart is empty");
        }else{
            log.info("Adding product from cart to order...");
            for(Product product : cart.getProducts()){
                CartItems item = cartItemsService.getItemsByProductId(product.getId());
                product.addCartItem(item);
                order.addProduct(product);
                product.setQuantityInStock(product.getQuantityInStock()-item.getQuantity()); //per ogni prodotto aggiunto decremento la quantità presente in stock
            }

            order.setId(0L);
            order.setOrderedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
            if(cart.getTotalAmount()>29){
                order.setShippingCosts(0.0);
            }else{
                order.setShippingCosts(5.0);
            }
            order.setProductsQuantity(cart.getTotalItems());
            order.setStatus(Status.SUCCESS);
            order.setShippingDate(LocalDateTime.now().plusDays(5).truncatedTo(ChronoUnit.MINUTES));
            order.setTotal(cart.getTotalAmount()+order.getShippingCosts());
            order.setUser(user);
            log.info("Saving order...");
            orderRepository.save(order);
            log.info("Deleting cart...");
            cartService.deleteCartById(cart.getId());//una volta che tutti gli elementi del carrello sono stati trasferiti nell'ordine, elimino il carrello
            return order;
        }
    }

    @Override
    public Order getOrderById(Long orderId) {
        log.info("Getting order with id '{}' from database...", orderId);
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        Order order;
        if(optionalOrder.isPresent()){
            order = optionalOrder.get();
        }else{
            log.error("Error! Order doesn't exist: id '{}' not found", orderId);
            throw new CustomException("Error! Order doesn't exist: id '"+orderId+"' not found");
        }
        return order;
    }

    @Override
    public List<Order> getOrdersByUser(String username) {
        log.info("Getting all {}'s products", username);
        User user = userService.getUserByUsername(username);
        return orderRepository.findOrdersByUser(user).get();
    }

    @Override
    public Order getYourOrderById(Long orderId) {
        log.info("Getting your order with id '{}'...", orderId);
        User user = userService.getCurrentUser();
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        Order order;
        if(optionalOrder.isPresent()){
            order = optionalOrder.get();
            if(user.getOrders().contains(order)){
                return order;
            }else{
                log.error("Error! Order '{}' is not your order", orderId);
                throw new CustomException("Error! Order '"+orderId+"' is not your order");
            }
        }else{
            log.error("Error! Order doesn't exist: id '{}' not found", orderId);
            throw new CustomException("Error! Order doesn't exist: id '"+orderId+"' not found");
        }
    }

    @Override
    public List<Order> getYourOrders() {
        log.info("Getting all your orders...");
        User user = userService.getCurrentUser();
        return user.getOrders().stream().toList();
    }

    @Override
    public void deleteOrderById(Long orderId) {
        log.info("Deleting order with id '{}'...",orderId);
        if(orderRepository.existsById(orderId)){
            orderRepository.deleteById(orderId);
        }else{
            log.error("Error! Order doesn't exist: id '{}' not found", orderId);
            throw new CustomException("Error! Order doesn't exist: id '"+orderId+"' not found");
        }
    }
}
