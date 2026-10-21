
import java.math.BigDecimal;
import java.math.RoundingMode;

class OperationsWithIntegersAndDoubles {
    public static void main(String[] args) {
        int i=20;
        System.out.println(i/3);
        System.out.println(i - (6*3));
        System.out.println((double)i / 3);

        BigDecimal rideFee = BigDecimal.valueOf(20).setScale(2);
        BigDecimal amountOfPeople = BigDecimal.valueOf(3);
        BigDecimal chargePerPerson = rideFee.divide(amountOfPeople, RoundingMode.HALF_UP);
        System.out.println(chargePerPerson);

        double d = 3.1;
        double d2 = 1.21;
        System.out.println(d-d2);

        BigDecimal bd3 = BigDecimal.valueOf(3.1).setScale(2);
        BigDecimal bd4 = BigDecimal.valueOf(1.21).setScale(2);
        System.out.println(bd3.subtract(bd4));
    }
}