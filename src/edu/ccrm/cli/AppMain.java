package edu.ccrm.cli;

import edu.ccrm.config.AppConfig;
import java.util.Scanner;

public class AppMain {
    public static void main(String[] args) {
        AppConfig.getInstance().load();

        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to CCRM!");

        boolean running = true;
        while (running) {
            System.out.println("\nMain Menu:");
            System.out.println("1. Manage Students");
            System.out.println("2. Manage Courses");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            switch (choice) {
                case 1 -> System.out.println("Student management coming soon...");
                case 2 -> System.out.println("Course management coming soon...");
                case 0 -> { running = false; System.out.println("Goodbye!"); }
                default -> System.out.println("Invalid choice, try again.");
            }
        }
        sc.close();
    }
}
