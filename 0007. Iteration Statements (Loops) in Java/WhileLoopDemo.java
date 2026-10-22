class WhileLoopDemo {
    public static void main(String[] args) {
        // While Loop
        int counter = 0;
        while (counter < 5) { 
            System.out.println("counter: "+counter++);
        }

        do { 
            System.out.println("counter is five already: "+counter);
        } while (counter<5);
    }
}
