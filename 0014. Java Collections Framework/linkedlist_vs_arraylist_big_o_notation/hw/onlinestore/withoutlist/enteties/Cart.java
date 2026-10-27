package linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withoutlist.enteties;

public interface Cart {

    boolean isEmpty();

    void addProduct(Product productById);

    Product[] getProducts();

    void clear();

}
