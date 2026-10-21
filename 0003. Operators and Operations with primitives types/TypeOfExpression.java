class TypeOfExpression {
    public static void main(String[] args) {
        byte b = 1;
        short c =2;
        int i = 10;
        long l = 100;
        float f = 1.0F;
        double d = 1.0;

        int intExpressionType = b+b;
        int intExpressionType2 = c+c;
        int intExpressionType3 = b+i+c;
        long longExpressionType = i+l;
        double floatExpressiontype = f+d;

        System.out.println(10/3);
        System.out.println(10.0/3);

        System.out.println("Hello " + 1);
    }
}
