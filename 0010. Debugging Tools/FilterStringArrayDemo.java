
import java.util.Arrays;
import java.util.Scanner;

class FilterStringArrayDemo {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter any words: ");
        String userInput = sc.nextLine();
        System.out.println("Enter minium word length to filter words: ");
        int minLength = sc.nextInt();

        String[] words = userInput.split("\\s+");
        String[] filteredWords = filterWordsByLength(minLength, words);
        System.out.println(Arrays.toString(filteredWords));
        sc.close();
    }

    public static String[] filterWordsByLength(int minLength, String[] words) {
        String[] filteredArray = new String[words.length];
        for (int i = 0; i < words.length; i++) {
            if (words[i].length() >= minLength) {
                filteredArray[i] = words[i];
            }
        }
        filteredArray = filterNulls(filteredArray);
        return filteredArray;
    }

    private static String[] filterNulls(String[] arr) {
        int newArraySize = 0;
        for (String word : arr) {
            if (word != null) {
                newArraySize++;
            }
        }
        String[] result = new String[newArraySize];
        int index = 0;
        for (String word : arr) {
            if (word != null) {
                result[index++] = word;
            }
        }
        return result;
    }
}
