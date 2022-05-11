package com.alten.ecommerce.rest;

import com.alten.ecommerce.domain.Order;
import com.alten.ecommerce.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderRestController {

    private final OrderService orderService;

    @Autowired
    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders/confirmOrder")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<Order> confirmOrder(){
        return ResponseEntity.ok().body(orderService.saveOrder());
    }

    @GetMapping("/orders/id/{orderId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId){
        return ResponseEntity.ok().body(orderService.getOrderById(orderId));
    }

    @GetMapping("/orders/username/{username}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Order>> getOrderByUser(@PathVariable String username){
        return ResponseEntity.ok().body(orderService.getOrdersByUser(username));
    }

    @GetMapping("/orders/yourOrder/{orderId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<Order> getYourOrderById(@PathVariable Long orderId){
        return ResponseEntity.ok().body(orderService.getYourOrderById(orderId));
    }

    @GetMapping("/orders/yourOrders")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<List<Order>> getYourOrders(){
        return ResponseEntity.ok().body(orderService.getYourOrders());
    }

    @DeleteMapping("/orders/id/{orderId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteOrderById(@PathVariable Long orderId){
        orderService.deleteOrderById(orderId);
        return ResponseEntity.ok().body("Order '"+orderId+"' deleted");
    }
}
