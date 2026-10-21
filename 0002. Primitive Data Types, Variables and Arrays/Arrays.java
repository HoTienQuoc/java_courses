class Arrays {
    public static void main(String[] args) {
        int [] arr;
        int arr2[];

        arr = new int[10];
        // System.out.println(arr[0]);
        System.out.println(arr.length);

        double[] arr3 = new double[10];
        System.out.println(arr3[0]);

        boolean[] arr4 = new boolean[10];
        System.out.println(arr4[0]);

        int[] arr5 = {1,2,3};
        int[][] matrix = {
            {1,2,3},
            {4,5,6}
        };
        System.out.println(matrix[0][2]);

        System.out.println(java.util.Arrays.toString(arr5));

        int[][] matrix2 = new int[10][10];
        System.out.println(java.util.Arrays.toString(matrix2[0]));
        java.util.Arrays.sort(arr5);
    }
}
