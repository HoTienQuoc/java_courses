package interface_segregation_principle.solution;

public class Motorcycle implements Drivable {

    @Override
    public void drive() {
        System.out.println("Motorcycle drives");
    }

}
