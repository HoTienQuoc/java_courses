package interface_segregation_principle.solution;

public class Car implements Drivable {

    @Override
    public void drive() {
        System.out.println("Car drives");
    }

}
