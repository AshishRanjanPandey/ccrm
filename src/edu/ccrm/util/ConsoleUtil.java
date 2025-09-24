package edu.ccrm.util;

import java.util.Scanner;

public class ConsoleUtil {
    private static final Scanner SC = new Scanner(System.in);

    public static String readLine(String prompt) {
        System.out.print(prompt);
        return SC.nextLine().trim();
    }

    public static int readInt(String prompt, int defaultVal) {
        try {
            String s = readLine(prompt);
            if (s.isBlank()) return defaultVal;
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number. Using default: " + defaultVal);
            return defaultVal;
        }
    }
}
