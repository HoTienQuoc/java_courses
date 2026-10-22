class NestedLoopDemo {
    public static void main(String[] args) {
        // Nested Loop
        for (int i = 0; i < 5; i++) {
            System.out.println("i = "+i);
            for (int j = 0; j < 10; j++) {
                System.out.print(j+"\t");
            }
            System.out.println();
        }
    }
}
