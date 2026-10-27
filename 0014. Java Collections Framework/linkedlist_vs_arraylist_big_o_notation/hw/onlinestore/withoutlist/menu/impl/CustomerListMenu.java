package linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withoutlist.menu.impl;

import linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withoutlist.configs.ApplicationContext;
import linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withoutlist.enteties.User;
import linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withoutlist.menu.Menu;
import linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withoutlist.services.UserManagementService;
import linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withoutlist.services.impl.DefaultUserManagementService;

public class CustomerListMenu implements Menu {

    private ApplicationContext context;
    private UserManagementService userManagementService;

    {
        userManagementService = DefaultUserManagementService.getInstance();
        context = ApplicationContext.getInstance();
    }

    @Override
    public void start() {
        printMenuHeader();
        User[] users = userManagementService.getUsers();

        if (users.length == 0) {
            System.out.println("Unfortunately, there are no customers.");
        } else {
            for (User user : users) {
                System.out.println(user);
            }
        }
        context.getMainMenu().start();
    }

    @Override
    public void printMenuHeader() {
        System.out.println("***** USERS *****");
    }

}
