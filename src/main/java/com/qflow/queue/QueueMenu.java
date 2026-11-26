
package com.qflow.queue;

import java.util.Scanner;

public class QueueMenu {

    public void showMenu(Scanner sc) {

        QueueDAO dao = new QueueDAO();
        int choice;

        do {
            System.out.println("\n========== QUEUE MANAGEMENT ==========");
            System.out.println("1. View Current Queue");
            System.out.println("2. Call Next Token");
            System.out.println("3. View Serving Tokens");
            System.out.println("4. Complete Service");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");

            choice = readInt(sc);
           

            switch (choice) {

                case 1:
                    dao.viewCurrentQueue();
                    break;

                case 0:
                    System.out.println("Returning to Main Menu...");
                    break;
                case 2:
                    System.out.print("Enter Counter ID: ");
                    int counterId = readInt(sc);

                    dao.callNextToken(counterId);
                    break;
                case 3:
                    dao.viewServingTokens();
                    break;
                case 4:
                    System.out.print("Enter Token ID to complete service: ");
                    int tokenId = readInt(sc);

                    dao.completeService(tokenId);
                    break;

                default:
                    System.out.println("Invalid choice! Try again.");
            }

        } while (choice != 0);
    }
        private int readInt(Scanner sc) {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a number: ");
            }
        }
    }
}
