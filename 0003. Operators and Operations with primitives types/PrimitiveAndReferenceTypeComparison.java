class PrimitiveAndReferenceTypeComparison{
    public static void main(String[] args) {
        int int1 = 128;
        int int2 = 128;

        System.out.println("int1 == int2: "+(int1==int2));

        Integer i3 = new Integer(127);
        Integer i4 = new Integer(127);
        System.out.println("i3 == i4: "+(i3==i4));

        Integer i5 = Integer.valueOf(127);
        Integer i6 = Integer.valueOf(127);
        System.out.println("i5 == i6: "+(i5==i6));
    }
}