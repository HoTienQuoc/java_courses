package linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withoutlist.services;

import linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withoutlist.enteties.User;

public interface UserManagementService {

    String registerUser(User user);

    User[] getUsers();

    User getUserByEmail(String userEmail);

}
