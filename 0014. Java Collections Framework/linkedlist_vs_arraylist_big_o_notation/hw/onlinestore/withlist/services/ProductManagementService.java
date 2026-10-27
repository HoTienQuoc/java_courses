package linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.services;

import java.util.List;

import linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.enteties.Product;

public interface ProductManagementService {

    List<Product> getProducts();

    Product getProductById(int productIdToAddToCart);

}
