package com.alten.ecommerce.service.order;

import com.alten.ecommerce.domain.Order;
import com.alten.ecommerce.domain.User;

import java.util.List;

public interface OrderService {

    Order saveOrder();
    Order getOrderById(Long orderId); //for role admin
    List<Order> getOrdersByUser(String username); //for role admin
    Order getYourOrderById(Long orderId);
    List<Order> getYourOrders();
    void deleteOrderById(Long orderId);

}
