import java.util.Scanner;

public class Test {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        char[] alphabet = new char[26];
        for (int i = 0; i < 26; i++) {
            alphabet[i] = input.next().charAt(0);
        }
        String str = input.next();
        int n = str.length();
        char last = transition(str.charAt(n - 1), alphabet);
        boolean ifMatch = false;
        for (int i = n / 2 - 1; i >= 0; i--) {
            if (str.charAt(i) == last) {
                String front = str.substring(0, i + 1);
                String end = convert(str.substring(n - i - 1), alphabet);
                if (front.equals(end)) {
                    System.out.print(n - i - 1);
                    ifMatch = true;
                    break;
                }
            }
        }
        if (!ifMatch) {
            System.out.print(n);
        }
    }

    public static String convert(String str, char[] alphabet) {
        char[] origin = str.toCharArray();
        for (int i = 0; i < origin.length; i++) {
            origin[i] = transition(origin[i], alphabet);
        }
        return String.valueOf(origin);
    }

    public static char transition(char c, char[] alphabet) {
        switch (c) {
            case 'a':
                return alphabet[0];
            case 'b':
                return alphabet[1];
            case 'c':
                return alphabet[2];
            case 'd':
                return alphabet[3];
            case 'e':
                return alphabet[4];
            case 'f':
                return alphabet[5];
            case 'g':
                return alphabet[6];
            case 'h':
                return alphabet[7];
            case 'i':
                return alphabet[8];
            case 'j':
                return alphabet[9];
            case 'k':
                return alphabet[10];
            case 'l':
                return alphabet[11];
            case 'm':
                return alphabet[12];
            case 'n':
                return alphabet[13];
            case 'o':
                return alphabet[14];
            case 'p':
                return alphabet[15];
            case 'q':
                return alphabet[16];
            case 'r':
                return alphabet[17];
            case 's':
                return alphabet[18];
            case 't':
                return alphabet[19];
            case 'u':
                return alphabet[20];
            case 'v':
                return alphabet[21];
            case 'w':
                return alphabet[22];
            case 'x':
                return alphabet[23];
            case 'y':
                return alphabet[24];
            case 'z':
                return alphabet[25];
        }
        return '0';
    }

}
