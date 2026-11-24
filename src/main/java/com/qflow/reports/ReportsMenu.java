package com.qflow.reports;

import java.util.Scanner;

public class ReportsMenu {

    public void showMenu(Scanner sc) {

        ReportsDAO dao = new ReportsDAO();

        int choice;

        do {
            System.out.println("\n========== REPORTS & ANALYTICS ==========");
            System.out.println("1. Token Summary Report");
            System.out.println("2. Service-wise Token Report");
            System.out.println("3. Daily Token Report");
            System.out.println("4. Average Service Time Report");
            System.out.println("5. Priority-wise Token Report");
            System.out.println("6. Counter Performance Report");
            System.out.println("7. Generate  All Reports");
            System.out.println("0. Back to Main Menu");

            System.out.print("Enter your choice: ");

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    dao.getTokenSummary();
                    break;
                case 2:
                    dao.getServiceWiseReport();
                    break;
                case 3:
                    dao.getDailyTokenReport();
                    break;
                case 4:
                    dao.getAverageServiceTime();
                    break;
                case 5:
                    dao.getPriorityWiseReport();
                    break;
                    case 6:
                        dao.getCounterPerformanceReport();
                        break;
                    
                    case 7:
                        dao.generateAllReportsParallel();
                        break;

                case 0:
                    System.out.println("Returning to Main Menu...");
                    break;

                default:
                    System.out.println("Invalid choice! Try again.");
            }

        } while (choice != 0);
    }
}
