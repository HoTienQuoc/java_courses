package linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withoutlist.menu.impl;

import linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withoutlist.configs.ApplicationContext;
import linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withoutlist.menu.Menu;

public class SignOutMenu implements Menu {

    private ApplicationContext context;

    {
        context = ApplicationContext.getInstance();
    }

    @Override
    public void start() {
        printMenuHeader();
        context.setLoggedInUser(null);
        context.getMainMenu().start();
    }

    @Override
    public void printMenuHeader() {
        System.out.println("***** Sign Out *****");
        System.out.println("Have a nice day! Look forward to welcoming back!");
    }

}
