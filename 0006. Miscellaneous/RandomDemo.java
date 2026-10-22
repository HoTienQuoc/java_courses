
import java.util.Random;

class RandomDemo {
    public static void main(String[] args) {
        Random r = new Random();
        System.out.println(r.nextInt());
        System.out.println(r.nextInt(100));
        System.out.println(r.nextInt(100)+1);
    }
}
