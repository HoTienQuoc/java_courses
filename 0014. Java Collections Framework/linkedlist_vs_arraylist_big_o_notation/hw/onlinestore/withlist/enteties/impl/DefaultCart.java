package linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.enteties.impl;

import java.util.ArrayList;
import java.util.List;

import linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.enteties.Cart;
import linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.enteties.Product;

public class DefaultCart implements Cart {

    private List<Product> products;

    {
        products = new ArrayList<>();
    }

    @Override
    public boolean isEmpty() {
        return products.isEmpty();
    }

    @Override
    public void addProduct(Product product) {
        if (product == null) {
            return;
        }
        products.add(product);
    }

    @Override
    public List<Product> getProducts() {
//		return Collections.unmodifiableList(this.products); // returns unmodifiable List
        return this.products;
    }

    @Override
    public void clear() {
        products.clear();
    }

}
