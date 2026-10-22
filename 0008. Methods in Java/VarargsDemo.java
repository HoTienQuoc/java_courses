
class VarargsDemo {

    public static void main(String[] args) {
        // Trong Java, dấu 3 chấm ... được gọi là Varargs (Variable Arguments), nghĩa là cho phép truyền vào 0 hoặc nhiều đối số cùng kiểu dữ liệu cho một phương thức.

        System.out.println(sum(1, 2, 3, 4, 5));
        System.out.println(sum(5));
    }

    private static int sum(int i1, int i2) {
        return i1 + i2;
    }

    private static int sum(int i1, int i2, int i3) {
        return i1 + i2 + i3;
    }

    private static int sum(int... ints) {
        int sum = 0;
        for (int i : ints) {
            sum += i;
        }
        return sum;
    }

    // private static void demoVarargsIncorrect(int... i, String s) {
    // varargs parameter must be the last parameter
    // }
    private static void demoVarargs(String s, int... i) {

    }
}
