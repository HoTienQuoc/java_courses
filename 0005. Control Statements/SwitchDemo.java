class SwitchDemo {
    public static void main(String[] args) {
        // expression types available: byte, short, char, int, String, Enum
        String customerStatus = "premium_customer";
        switch (customerStatus) {
            case "guest" -> System.out.println("Thank you for your order!");
            case "regular_customer" -> System.out.println("Thank you for your purchase! Take discount 10% for your order as a gratitude for staying with us.");
            case "premium_customer" -> System.out.println("Wow! You are our PREMIUM customer! Just take all order for 1 USD");
            default -> System.out.println("Customer doesn\'t have status set.");
        }
    }
}
