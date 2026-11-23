
package com.qflow.token;

import java.util.Scanner;

public class TokenMenu {

    public void showMenu(Scanner sc) {

        TokenDAO dao = new TokenDAO();

        int choice;

        do {

            System.out.println("\n");
            System.out.println("================================================");
            System.out.println("                 TOKEN MANAGEMENT               ");
            System.out.println("================================================");

            System.out.println();
            System.out.println("       What would you like to do?");
            System.out.println();

            System.out.println("       [1]  Generate New Token");
            System.out.println("       [2]  View All Tokens");
            System.out.println("       [3]  Search Token By ID");
            System.out.println("       [4]  View Tokens By Customer ID");
            System.out.println("       [5]  Update Token Status");
            System.out.println("       [6]  Cancel Token");
            System.out.println("       [7]  Export Tokens to CSV");
            System.out.println("       [0]  Back to Main Menu");

            System.out.println();
            System.out.println("================================================");

System.out.print("  Enter your choice (0-7): ");

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                // 1. GENERATE TOKEN
                case 1:

                    System.out.println("\n========== GENERATE NEW TOKEN ==========");

                    System.out.print("Enter Customer ID: ");
                    int customerId = sc.nextInt();

                    System.out.print("Enter Service ID: ");
                    int serviceId = sc.nextInt();
                    sc.nextLine();

                    System.out.println("\nSelect Priority:");
                    System.out.println("1. NORMAL");
                    System.out.println("2. SENIOR");
                    System.out.println("3. EMERGENCY");

                    System.out.print("Enter choice: ");
                    int priorityChoice = sc.nextInt();
                    sc.nextLine();

                    String priority;

                    switch (priorityChoice) {
                        case 1:
                            priority = "NORMAL";
                            break;
                        case 2:
                            priority = "SENIOR";
                            break;
                        case 3:
                            priority = "EMERGENCY";
                            break;
                        default:
                            System.out.println("Invalid priority!");
                            break;
                    }

                    if (priorityChoice >= 1 && priorityChoice <= 3) {

                        priority = priorityChoice == 1 ? "NORMAL"
                                 : priorityChoice == 2 ? "SENIOR"
                                 : "EMERGENCY";

                        dao.generateToken(customerId, serviceId, priority);
                    }

                    break;


                // 2. VIEW ALL TOKENS
                case 2:

                    dao.getAllTokens();
                    break;


                // 3. SEARCH TOKEN
                case 3:

                    System.out.print("\nEnter Token ID: ");
                    int searchId = sc.nextInt();
                    sc.nextLine();

                    dao.getTokenById(searchId);
                    break;


                // 4. VIEW CUSTOMER TOKENS
                case 4:

                    System.out.print("\nEnter Customer ID: ");
                    int searchCustomerId = sc.nextInt();
                    sc.nextLine();

                    dao.getTokensByCustomerId(searchCustomerId);
                    break;


                // 5. UPDATE TOKEN STATUS
                case 5:

                    System.out.println("\n========== UPDATE TOKEN STATUS ==========");

                    System.out.print("Enter Token ID: ");
                    int updateId = sc.nextInt();
                    sc.nextLine();

                    boolean tokenExists = dao.getTokenById(updateId);

                    if (tokenExists) {

                        System.out.println("\nSelect New Status:");
                        System.out.println("1. WAITING");
                        System.out.println("2. SERVING");
                        System.out.println("3. COMPLETED");
                        System.out.println("4. CANCELLED");

                        System.out.print("Enter choice: ");
                        int statusChoice = sc.nextInt();
                        sc.nextLine();

                        String status = null;

                        switch (statusChoice) {
                            case 1:
                                status = "WAITING";
                                break;
                            case 2:
                                status = "SERVING";
                                break;
                            case 3:
                                status = "COMPLETED";
                                break;
                            case 4:
                                status = "CANCELLED";
                                break;
                            default:
                                System.out.println("Invalid status choice!");
                        }

                        if (status != null) {
                            dao.updateTokenStatus(updateId, status);
                        }
                    }

                    break;


                // 6. CANCEL TOKEN
                case 6:

                    System.out.println("\n========== CANCEL TOKEN ==========");

                    System.out.print("Enter Token ID: ");
                    int cancelId = sc.nextInt();
                    sc.nextLine();

                    boolean exists = dao.getTokenById(cancelId);

                    if (exists) {

                        System.out.print(
                            "\nAre you sure you want to cancel this token? (Y/N): "
                        );

                        String confirmation = sc.nextLine();

                        if (confirmation.equalsIgnoreCase("Y")) {

                            dao.cancelToken(cancelId);

                        } else {

                            System.out.println("\nToken cancellation cancelled.");
                        }
                    }

                    break;
                    // 7. EXPORT TOKENS TO CSV
case 7:

    System.out.println("\n========== EXPORT TOKENS ==========");

    dao.exportTokensToCSV();

    break;


                // 0. BACK TO MAIN MENU
                case 0:

                    System.out.println("\nReturning to Main Menu...");
                    break;


                default:

                    System.out.println("\nInvalid choice! Please select between 0 and 6.");
            }

        } while (choice != 0);
    }
}
