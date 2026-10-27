package linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withoutlist.menu.impl;

import java.util.Scanner;

import linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withoutlist.configs.ApplicationContext;
import linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withoutlist.enteties.User;
import linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withoutlist.menu.Menu;
import linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withoutlist.services.UserManagementService;
import linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withoutlist.services.impl.DefaultUserManagementService;

public class SignInMenu implements Menu {

    private ApplicationContext context;
    private UserManagementService userManagementService;

    {
        context = ApplicationContext.getInstance();
        userManagementService = DefaultUserManagementService.getInstance();
    }

    @Override
    public void start() {
        printMenuHeader();
        Scanner sc = new Scanner(System.in);

        System.out.print("Please, enter your email: ");
        String userEmail = sc.next();

        System.out.print("Please, enter your password: ");
        String userPassword = sc.next();

        User user = userManagementService.getUserByEmail(userEmail);
        if (user != null && user.getPassword().equals(userPassword)) {
            System.out.printf("Glad to see you back %s %s", user.getFirstName(),
                    user.getLastName() + System.lineSeparator());
            context.setLoggedInUser(user);
        } else {
            System.out.println("Unfortunately, such login and password doesn't exist");
        }
        context.getMainMenu().start();
    }

    @Override
    public void printMenuHeader() {
        System.out.println("***** Sign In *****");
    }

}
