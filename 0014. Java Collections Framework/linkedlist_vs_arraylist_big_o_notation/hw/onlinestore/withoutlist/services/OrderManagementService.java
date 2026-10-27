package linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withoutlist.services;

import linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withoutlist.enteties.Order;

public interface OrderManagementService {

    void addOrder(Order order);

    Order[] getOrdersByUserId(int userId);

    Order[] getOrders();

}
