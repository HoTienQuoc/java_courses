
import java.util.Scanner;

class ReadFromConsole{
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("Please, enter any words: ");
            String word = sc.next();
            System.out.println("You entered this word: "+word);
            System.out.println("Please, enter any integer number: ");
            int i = sc.nextInt();
            System.out.println("You entered this integer number: "+i);
        }

    }
}