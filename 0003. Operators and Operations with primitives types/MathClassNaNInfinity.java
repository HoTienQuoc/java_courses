class MathClassNaNInfinity {
    public static void main(String[] args) {
        System.out.println(Math.PI);
        System.out.println(Math.max(3, 5));
        System.out.println(Math.min(3, 5));
        System.out.println(Math.sqrt(3));
        System.out.println(Math.abs(-3));

        System.out.println(Math.sqrt(-1)); // NaN
        System.out.println(0 / 0.0); // NaN
        System.out.println((0/0.0)+5); // NaN

        System.out.println(5/0.0); // Infinity
        System.out.println(-5/0.0); // Infinity

        System.out.println(Math.round(20.0 / 3.0));
        System.out.println(Math.random());
        System.out.println((int)(Math.random()*100));
    }
}