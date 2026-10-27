package linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.services;

import java.util.List;

import linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.enteties.User;

public interface UserManagementService {

    String registerUser(User user);

    List<User> getUsers();

    User getUserByEmail(String userEmail);

}
