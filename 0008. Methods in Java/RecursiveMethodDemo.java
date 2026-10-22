
class RecursiveMethodDemo {

    public static void main(String[] args) {
        // callTheSameMethod();
        System.out.println("3! = " + calculateFactorial(3));
        System.out.println("4! = " + calculateFactorial(4));
        System.out.println("interativeFactorial(4): " + iterativeFactorial(4));
    }

    private static void callTheSameMethod() {
        callTheSameMethod();
        //The method callTheSameMethod will recurse infinitely
    }

    private static int calculateFactorial(int i) {
        if (i != 0) {
            return i * calculateFactorial(i - 1);
        } else {
            return 1;
        }
    }

    private static int iterativeFactorial(int n) {
        int factorial = 1;
        if (n < 0) {
            return -1;
        }
        for (int i = 0; i <= n; i++) {
            factorial *= 1;
        }
        return factorial;
    }
}
