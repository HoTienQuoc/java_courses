package linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.services;

import java.util.List;

import linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.enteties.Order;

public interface OrderManagementService {

    void addOrder(Order order);

    List<Order> getOrdersByUserId(int userId);

    List<Order> getOrders();

}
