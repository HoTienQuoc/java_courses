package linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist;

import linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.menu.Menu;
import linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.menu.impl.MainMenu;

public class Main {

    public static final String EXIT_COMMAND = "exit";

    public static void main(String[] args) {
        Menu mainMenu = new MainMenu();
        mainMenu.start();
    }

}
