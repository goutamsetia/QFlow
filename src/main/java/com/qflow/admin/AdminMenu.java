
package com.qflow.admin;

import java.util.Scanner;

public class AdminMenu {

    public boolean showMenu(Scanner sc) {

        AdminDAO dao = new AdminDAO();

        while (true) {

            boolean exists = dao.adminExists();

            System.out.println("\n");
            System.out.println("************************************************");
            System.out.println("*                                              *");
            System.out.println("*                  Q F L O W                   *");
            System.out.println("*                                              *");
            System.out.println("*        SMART QUEUE MANAGEMENT SYSTEM         *");
            System.out.println("*                                              *");
            System.out.println("************************************************");

            System.out.println("\n================================================");
            System.out.println("                  ADMIN PORTAL                 ");
            System.out.println("================================================");

            System.out.println("\n       Welcome to QFlow Administration");

            if (!exists) {
                System.out.println("       Initial setup: Create your admin account.");
                System.out.println("\n       [1] Register Admin");
            } else {
                System.out.println("       Please login to continue.");
            }

            System.out.println("       [2] Admin Login");
            System.out.println("       [0] Exit Application");
            System.out.println("------------------------------------------------");

            System.out.print("       Enter your choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    if (exists) {
                        System.out.println("\nRegistration is disabled!");
                        break;
                    }

                    System.out.println("\n========== ADMIN REGISTRATION ==========");

                    System.out.print("Enter Full Name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter Username: ");
                    String username = sc.nextLine();

                    System.out.print("Enter Password: ");
                    String password = sc.nextLine();

                    if (dao.registerAdmin(username, password, name)) {
                        System.out.println("\nAdmin registered successfully!");
                    } else {
                        System.out.println("\nAdmin registration failed!");
                    }
                    break;

                case 2:
                    System.out.println("\n============== ADMIN LOGIN ==============");

                    System.out.print("Enter Username: ");
                    String loginUsername = sc.nextLine();

                    System.out.print("Enter Password: ");
                    String loginPassword = sc.nextLine();

                    System.out.println("-----------------------------------------");

                    if (dao.loginAdmin(loginUsername, loginPassword)) {
                        System.out.println("\n       Login Successful!");
                        System.out.println("       Welcome to QFlow Administration.");
                        return true;
                    } else {
                        System.out.println("\nInvalid username or password!");
                    }
                    break;

                case 0:
                    System.out.println("\nThank you for using QFlow!");
                    return false;

                default:
                    System.out.println("\nInvalid choice! Please try again.");
            }
        }
    }
}
