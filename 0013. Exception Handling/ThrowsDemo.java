
import java.io.FileNotFoundException;

public class ThrowsDemo {

    public static void main(String[] args) {
        ThrowsDemo demo = new ThrowsDemo();
//		demo.someMethod();

    }

    public void someMethod() throws FileNotFoundException {

    }

}

class ThrowsDemoOverride extends ThrowsDemo {

    @Override
    public void someMethod() {

    }
}
