class ForeachLoopDemo {
    public static void main(String[] args) {
        // foreach loop
        int[] arr = {1,2,3};
        int sum = 0;
        for (int number : arr) {
            sum += number;
            System.out.println("Number: "+number);
        }
        System.out.println("Sum: "+sum);
    }
}
