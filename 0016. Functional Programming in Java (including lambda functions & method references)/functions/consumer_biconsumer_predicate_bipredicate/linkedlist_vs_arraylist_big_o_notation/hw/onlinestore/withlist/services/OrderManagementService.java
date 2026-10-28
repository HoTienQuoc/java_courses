package functions.consumer_biconsumer_predicate_bipredicate.linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.services;

import functions.consumer_biconsumer_predicate_bipredicate.linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.enteties.Order;
import java.util.List;

public interface OrderManagementService {

    void addOrder(Order order);

    List<Order> getOrdersByUserId(int userId);

    List<Order> getOrders();

}
