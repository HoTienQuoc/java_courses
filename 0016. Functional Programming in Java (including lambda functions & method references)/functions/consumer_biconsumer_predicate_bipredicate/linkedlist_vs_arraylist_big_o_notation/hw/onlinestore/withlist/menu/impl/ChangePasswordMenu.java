package functions.consumer_biconsumer_predicate_bipredicate.linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.menu.impl;

import functions.consumer_biconsumer_predicate_bipredicate.linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.configs.ApplicationContext;
import functions.consumer_biconsumer_predicate_bipredicate.linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.menu.Menu;
import java.util.Scanner;

public class ChangePasswordMenu implements Menu {

    private ApplicationContext context;

    {
        context = ApplicationContext.getInstance();
    }

    @Override
    public void start() {
        printMenuHeader();
        Scanner sc = new Scanner(System.in);
        String userInput = sc.next();
        context.getLoggedInUser().setPassword(userInput);
        System.out.println("Your password has been successfully changed");
        new MainMenu().start();
    }

    @Override
    public void printMenuHeader() {
        System.out.println("***** CHANGE PASSWORD *****");
        System.out.print("Enter new password: ");
    }

}
