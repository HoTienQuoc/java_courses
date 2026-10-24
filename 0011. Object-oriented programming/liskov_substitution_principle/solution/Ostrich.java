package liskov_substitution_principle.solution;

public class Ostrich implements Bird {

    @Override
    public void eat() {
        System.out.println("Ostrich eats");
    }
}
