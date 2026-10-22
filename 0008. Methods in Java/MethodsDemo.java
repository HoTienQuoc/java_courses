class MethodsDemo {
    public static void main(String[] args) {
        int sum = sum(1,3);
        printToConsole(sum);
        printToConsole(sum(2.1, 3.5));
        printToConsole("Some Text");
    }
    
    private static int sum(int i1, int i2){
        return i1+i2;
    }

    private static double sum(double d1, double d2){
        return d1+d2;
    }

    private static void printToConsole(int i){
        System.out.println(i);
    }

    private static void printToConsole(double  i){
        System.out.println(i);
    }

    private static void printToConsole(String i){
        System.out.println(i);
    }
}
