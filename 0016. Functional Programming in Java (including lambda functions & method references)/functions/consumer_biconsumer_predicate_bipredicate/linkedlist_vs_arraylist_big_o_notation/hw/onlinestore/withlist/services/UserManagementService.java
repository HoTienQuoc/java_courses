package functions.consumer_biconsumer_predicate_bipredicate.linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.services;

import functions.consumer_biconsumer_predicate_bipredicate.linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.enteties.User;
import java.util.List;

public interface UserManagementService {

    String registerUser(User user);

    List<User> getUsers();

    User getUserByEmail(String userEmail);

}
