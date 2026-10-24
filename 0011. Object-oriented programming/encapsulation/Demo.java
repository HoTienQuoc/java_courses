package encapsulation;

import encapsulation.package1.ClassA;

public class Demo {

    public static void main(String[] args) {
        ClassA classA = new ClassA();
        classA.doSomething(); // public modifier demo

//		classA.doSomething2(); // protected modifier demo
    }

}
