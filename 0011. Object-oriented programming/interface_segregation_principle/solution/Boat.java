package interface_segregation_principle.solution;

public class Boat implements Sailable {

    @Override
    public void sail() {
        System.out.println("Boat sails");
    }

}
