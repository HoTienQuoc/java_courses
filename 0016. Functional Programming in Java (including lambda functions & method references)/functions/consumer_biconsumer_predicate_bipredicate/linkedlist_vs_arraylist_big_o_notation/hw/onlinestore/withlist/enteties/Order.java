package functions.consumer_biconsumer_predicate_bipredicate.linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.enteties;

import java.util.List;

public interface Order {

    boolean isCreditCardNumberValid(String userInput);

    void setCreditCardNumber(String userInput);

    void setProducts(List<Product> products);

    void setCustomerId(int customerId);

    int getCustomerId();

}
