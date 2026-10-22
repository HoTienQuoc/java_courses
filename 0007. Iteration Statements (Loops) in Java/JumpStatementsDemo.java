class JumpStatementsDemo {
    public static void main(String[] args) {
        // continue
        for (int i = 0; i < 5; i++) {
            if (i % 2 == 0) {
                continue;
            }
            System.out.println("counter: "+i);
        }
        // break
        for (int i = 0; i < 5; i++) {
            if (i==3) {
                break;
            }
            System.out.println("counter: "+i);
        }
    }
}
