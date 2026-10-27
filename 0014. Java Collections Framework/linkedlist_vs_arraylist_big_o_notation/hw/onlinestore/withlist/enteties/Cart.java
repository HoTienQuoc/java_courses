package linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.enteties;

import java.util.List;

public interface Cart {

    boolean isEmpty();

    void addProduct(Product productById);

    List<Product> getProducts();

    void clear();

}
