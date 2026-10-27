package linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withoutlist.services;

import linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withoutlist.enteties.Product;

public interface ProductManagementService {

    Product[] getProducts();

    Product getProductById(int productIdToAddToCart);

}
