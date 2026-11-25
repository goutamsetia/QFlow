
package com.qflow.app;

import java.util.Scanner;
import com.qflow.customer.CustomerMenu;
import com.qflow.service.ServiceMenu;
import com.qflow.token.TokenMenu;
import com.qflow.queue.QueueMenu;
import com.qflow.reports.ReportsMenu;
import com.qflow.admin.AdminMenu;
public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        AdminMenu adminMenu = new AdminMenu();

        boolean loggedIn = adminMenu.showMenu(sc);

        if (!loggedIn) {
          sc.close();
         return;
        }

        CustomerMenu customerMenu = new CustomerMenu();
        ServiceMenu serviceMenu = new ServiceMenu();
        TokenMenu tokenMenu = new TokenMenu();
        QueueMenu queueMenu = new QueueMenu();
        ReportsMenu reportsMenu = new ReportsMenu();

        int choice;

        // Welcome Screen
        System.out.println("\n");
        System.out.println("************************************************");
        System.out.println("*                                              *");
        System.out.println("*                  Q F L O W                   *");
        System.out.println("*                                              *");
        System.out.println("*        SMART QUEUE MANAGEMENT SYSTEM         *");
        System.out.println("*                                              *");
        System.out.println("************************************************");

        System.out.println("\n       Welcome! We are glad to have you.");
        System.out.println("       Manage your queues smarter with QFlow.");
        System.out.println("\n       Starting QFlow System...");
        
        System.out.println("\n************************************************");

        do {

            System.out.println("\n");
            System.out.println("================================================");
            System.out.println("                 QFLOW MAIN MENU                ");
            System.out.println("================================================");

            System.out.println();
            System.out.println("       Please select an option below:");
            System.out.println();

            System.out.println("       [1]  Customer Management");
            System.out.println("       [2]  Service Management");
            System.out.println("       [3]  Token Management");
            System.out.println("       [4]  Queue Management");
            System.out.println("       [5]  Reports & Analytics");
            System.out.println("       [0]  Exit Application");

            System.out.println();
            System.out.println("================================================");

            System.out.print("  Enter your choice (0-5): ");

            choice = sc.nextInt();

            switch (choice) {

                case 1:
                    System.out.println("\nWelcome to Customer Management!");
                    customerMenu.showMenu(sc);
                    break;

                case 2:
                    System.out.println("\nWelcome to Service Management!");
                    serviceMenu.showMenu(sc);
                    break;

                case 3:
                    System.out.println("\nWelcome to Token Management!");
                    tokenMenu.showMenu(sc);
                    break;

                case 4:
                    queueMenu.showMenu(sc);
                    break;

                case 5:
                    reportsMenu.showMenu(sc);
                    break;

                case 0:
                    System.out.println("\n================================================");
                    System.out.println("              THANK YOU FOR USING QFLOW         ");
                    System.out.println("================================================");
                    System.out.println("       Have a wonderful day!");
                    System.out.println("       Goodbye! See you again.");
                    System.out.println("================================================");
                    break;

                default:
                    System.out.println("\nInvalid choice! Please select between 0 and 5.");
            }

        } while (choice != 0);

        sc.close();
    }
}
