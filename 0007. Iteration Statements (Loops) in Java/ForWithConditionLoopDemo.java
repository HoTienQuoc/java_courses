class ForWithConditionLoopDemo {
    public static void main(String[] args) {
        // For Loop
        for (int i = 0; i < 5; i++) {
            System.out.println("Counter: "+i++);
        }

        // Infinite Loop
        // for(;;){}

        // for loop | comma usage example
        for (int i = 0, j = 10; i < j; i++, j--) {
            System.out.println("counters: i = "+i+", j = "+j);
        }
    }
}
