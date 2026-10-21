class Operators {
    public static void main(String[] args) {
        // ==== Arithmetric Operators
        // **** Unary Arithmetric Operators

        // +
        // -
        // ++
        // --

        System.out.println("==== Unary Arithmetric Operators");

        int i1 = +10;
        int i2 = -10;
        int i3 = ++i1;
        int i4 = i1++;
        int i5 = --i1;
        int i6 = i1--;

        System.out.println("i1 = "+i1);
        System.out.println("i2 = "+i2);
        System.out.println("i3 = "+i3);
        System.out.println("i4 = "+i4);
        System.out.println("i5 = "+i5);
        System.out.println("i6 = "+i6);
        System.out.println("i1 = "+i1);


        // *** Binary Arithmetic Operators
        // +, -, /, %, *

        System.out.println("==== Binary Arithmetric Operators");

        int i7 = 10 + 1;
        int i8 = i7 - 1;
        int i9 = i8/2;
        int i10 = i9 % 2;
        int i11 = 5%10;
        int i12 = i10*3;

        System.out.println("i7 = "+i7);
        System.out.println("i8 = "+i8);
        System.out.println("i9 = "+i9);
        System.out.println("i10 = "+i10);
        System.out.println("i11 = "+i11);
        System.out.println("i12 = "+i12);


        // Assignment Operators
        // =, +=, -=, *=, /=, %=
        int i13 = 10;
        i13+=2;
        System.out.println(i13);

        // Relational Operators
        // >, <, >=, <=, ==, !=
        int i14 = 11;
        int i15 = 12;
        System.out.println(i14==i15);
        System.out.println(i14!=i15);
        System.out.println(i14>=i15);
        System.out.println(i14<=i15);
        System.out.println(i14<i15);
        System.out.println(i14>i15);

        // Logical Operators
        // &, &&, |, ||, !, ^
        System.out.println(false & (5/0==0)); // Runtime Exception
        System.out.println(false && (5/0==0));
        System.out.println(true | (5/0==0)); // Runtime Exception
        System.out.println(true || (5/0==0));
        System.out.println("!true = " + !true);
        System.out.println(true ^ true);
        System.out.println(true ^ false);
        System.out.println(false ^ true);

        // Bitwise Operators
        // &, |, ^, ~, >>, >>>, <<
        System.out.println("4 & 5 = "+(4&5)); 
        System.out.println("4 | 5 = "+(4|5)); 
        System.out.println("4 ^ 5 = "+(4^5));

        System.out.println("~1 = "+~1);
        System.out.println(Integer.toBinaryString(1));
        System.out.println(Integer.toBinaryString(-2));
        System.out.println((byte)0b11111110);
        System.out.println((byte)0b11001010);
        System.out.println((byte)0b11000010);

        System.out.println("5 = "+0b101);
        System.out.println("5 >>> 1 = "+(0b101>>>1));

        int negativeByteValue = 0b11111111111111111111111110000000;
        System.out.println("-128 = "+negativeByteValue);
        System.out.println("-128 >>> 1 = "+(negativeByteValue >>> 1));
        System.out.println("-128 >> 1 = "+(negativeByteValue >> 1));

        // Ternary Operator
        // (condition) ? true expression : false expression
        System.out.println(2 > 1 ? "2 is greather than one" : "2 is not less than one");

        // Operators Precedence
        // (), [], ++, --

    }
}
