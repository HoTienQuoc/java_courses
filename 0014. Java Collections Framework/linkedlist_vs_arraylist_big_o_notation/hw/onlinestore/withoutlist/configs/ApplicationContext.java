package linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withoutlist.configs;

import linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withoutlist.enteties.Cart;
import linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withoutlist.enteties.User;
import linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withoutlist.enteties.impl.DefaultCart;
import linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withoutlist.menu.Menu;

public class ApplicationContext {

    private static ApplicationContext instance;

    private User loggedInUser;
    private Menu mainMenu;
    private Cart sessionCart;

    private ApplicationContext() {
    }

    public void setLoggedInUser(User user) {
        if (this.sessionCart != null) {
            this.sessionCart.clear(); // we have to clear session cart when new user is logged in
        }
        this.loggedInUser = user;
    }

    public User getLoggedInUser() {
        return this.loggedInUser;
    }

    public void setMainMenu(Menu menu) {
        this.mainMenu = menu;
    }

    public Menu getMainMenu() {
        return this.mainMenu;
    }

    public static ApplicationContext getInstance() {
        if (instance == null) {
            instance = new ApplicationContext();
        }
        return instance;
    }

    public Cart getSessionCart() {
        if (this.sessionCart == null) {
            this.sessionCart = new DefaultCart();
        }
        return this.sessionCart;
    }

}
