
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class StringDemo {
    public static void main(String[] args) {
        String s = "Hello ";
        System.out.println("length = "+s.length());   // 6
        System.out.println("contains = "+s.contains("he")); // true   
        System.out.println("isEmpty = "+s.isEmpty());   // false
        System.out.println("toUpperCase = "+s.toUpperCase());   // HELLO
        System.out.println("startsWith = "+s.startsWith("h"));   // false
        System.out.println("endsWith = "+s.endsWith(" ")); // true   
        System.out.println("replace = "+s.replace("ll", "LL"));   // heLLo
        System.out.println("trim = "+s.trim());   // hello 
        System.out.println("strip = "+s.strip());  // hello
        System.out.println("substring = "+s.substring(0,3)); //Hel
        System.out.println("Arrays.toString(s.getBytes()) = " + Arrays.toString(s.getBytes())); // [72, 101, 108, 108, 111, 32]
        System.out.println("Arrays.toString(s.toCharArray()) = "+Arrays.toString(s.toCharArray())); // [H, e, l, l, o,  ]
        System.out.println("s.charAt(1) = "+s.charAt(1)); // e
        System.out.println("Arrays.toString(s.split(\"e\")) = "+Arrays.toString(s.split("e"))); // [H, llo ]
        System.out.println(s);


        String s2 = " hello ";
        System.out.println("s == s2 "+ (s==s2)); // false

        String s3 = new String(" hello ");
        System.out.println("s == s3 "+(s==s3)); //false
        System.out.println("s.equals(s3) "+(s==s3.intern())); // true

        String firstName = "Andrii";
        String firstName2 = "andrii";

        System.out.println("firstname.equals(firstName2) "+ firstName.equals(firstName2)); // false
        System.out.println("firstname.equalsIgnoreCase(firstName2) "+ firstName.equalsIgnoreCase(firstName2)); // true

        /**
         * \t - tab
         * \b - backspace (a step backward in the text or deletion of a single character)
         * \n - new line
         * \r - carriage return ()
         * \f - form feed
         * \' - single quote
         * \" - double quote
         * \\ - backslash
         */

        String greetingsTemplate = "Hello, dear %s! Good %s!";
        String morning = "morning";
        String afternoon = "afternoon";
        String evening = "evening";
        String formattedString = String.format(greetingsTemplate, morning, afternoon);
        System.out.println(formattedString); // Hello, dear morning! Good afternoon!

        /**
         * Regular expressions
         */
        String gmailPattern = "[a-zA-Z-\\d]+@gmail\\.com";
        Pattern p = Pattern.compile(gmailPattern);
        String sampleText = "Some random text that contains gmail addresses "+ "like this one some-email@gmail.com. And some other random text";
        Matcher m = p.matcher(sampleText);
        m.find();

        String gmailAddress = m.group();
        System.out.println(gmailAddress);

        String sampleText2 = "There are three sentences in this string. Are you sure? Yes!";
        String[] sentences = sampleText2.split("[\\.!?]");
        System.out.println(Arrays.toString(sentences));

        /**
         * \p{Lower} A lower-case alphabetic character: [a-z]
         * \p{Upper} An upper-case alphabetic character: [A-Z]
         * \p{Ascii} All ASCII: [\x00-\x7F]
         * \p{Alpha} An alphabetic character: [\p{Lower}\p{Upper}]
         * \p{Digit} A decimal digit: [0-9]
         * \p{Alnum} An alphanumeric character: [\p{Alpha}\p{Digit}]
         * \p{Punct} Punctuation: One of !"#$%&' ()*+,-./:;<=>?@[\]^_`{|}~
         * \p{Graph} A Visible character: [\p{Alnum}\p{Punct}]
         * \p{Print} A Printable character: [\p{Graph}\x20]
         * \p{Blank} A space or a tab: [\t]
         * \p{Cntrl} A control character: [\x00-\x1F\x7F]
         * \p{XDigit} A hexadecimal digit: [0-9a-fA-F]
         * \p{Space} A whitespace character: [\t\n\x0B\f\r]
         */

        


    }
}
