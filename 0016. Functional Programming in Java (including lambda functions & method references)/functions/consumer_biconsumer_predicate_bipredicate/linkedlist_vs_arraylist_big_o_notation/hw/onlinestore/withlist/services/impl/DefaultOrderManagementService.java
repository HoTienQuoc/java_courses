package functions.consumer_biconsumer_predicate_bipredicate.linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.services.impl;

import functions.consumer_biconsumer_predicate_bipredicate.linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.enteties.Order;
import functions.consumer_biconsumer_predicate_bipredicate.linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.services.OrderManagementService;
import java.util.ArrayList;
import java.util.List;

public class DefaultOrderManagementService implements OrderManagementService {

    private static DefaultOrderManagementService instance;
    private List<Order> orders;

    {
        orders = new ArrayList<>();
    }

    public static OrderManagementService getInstance() {
        if (instance == null) {
            instance = new DefaultOrderManagementService();
        }
        return instance;
    }

    @Override
    public void addOrder(Order order) {
        if (order == null) {
            return;
        }
        orders.add(order);
    }

    @Override
    public List<Order> getOrdersByUserId(int userId) {
        List<Order> userOrders = new ArrayList<>();
        for (Order order : orders) {
            if (order != null && order.getCustomerId() == userId) {
                userOrders.add(order);
            }
        }

        return userOrders;
    }

    @Override
    public List<Order> getOrders() {
        return this.orders;
    }

    void clearServiceState() {
        orders.clear();
    }

}
