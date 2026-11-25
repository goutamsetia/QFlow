
package com.qflow.service;

import java.util.Scanner;

public class ServiceMenu {

    public void showMenu(Scanner sc) {

        ServiceDAO dao = new ServiceDAO();

        int choice;

        do {

            System.out.println("\n");
            System.out.println("================================================");
            System.out.println("                SERVICE MANAGEMENT              ");
            System.out.println("================================================");

            System.out.println();
            System.out.println("       What would you like to do?");
            System.out.println();

            System.out.println("       [1]  Add New Service");
            System.out.println("       [2]  View All Services");
            System.out.println("       [3]  Search Service By ID");
            System.out.println("       [4]  Update Service");
            System.out.println("       [5]  Delete Service");
            System.out.println("       [0]  Back to Main Menu");

            System.out.println();
            System.out.println("================================================");

            System.out.print("  Enter your choice (0-5): ");

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                // 1. ADD SERVICE
                case 1:

                    System.out.println("\n========== ADD NEW SERVICE ==========");

                    System.out.print("Enter service name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter description: ");
                    String description = sc.nextLine();

                    System.out.print("Enter average time (minutes): ");
                    int averageTime = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Is this service active? (Y/N): ");
                    String status = sc.nextLine();

                    boolean isActive = status.equalsIgnoreCase("Y");

                    boolean added = dao.addService(
                            name, description, averageTime, isActive
                    );

                    if (added) {
                        System.out.println("\nSERVICE ADDED SUCCESSFULLY!");
                    } else {
                        System.out.println("\nService addition failed!");
                    }

                    break;


                // 2. VIEW ALL SERVICES
                case 2:

                    dao.getAllServices();
                    break;


                // 3. SEARCH SERVICE
                case 3:

                    System.out.print("\nEnter Service ID: ");
                    int searchId = sc.nextInt();
                    sc.nextLine();

                    dao.getServiceById(searchId);

                    break;


                // 4. UPDATE SERVICE
                case 4:

                    System.out.println("\n========== UPDATE SERVICE ==========");

                    System.out.print("Enter Service ID to update: ");
                    int updateId = sc.nextInt();
                    sc.nextLine();

                    boolean serviceExists = dao.getServiceById(updateId);

                    if (serviceExists) {

                        System.out.println("\nEnter new service details:");

                        System.out.print("Enter new service name: ");
                        String newName = sc.nextLine();

                        System.out.print("Enter new description: ");
                        String newDescription = sc.nextLine();

                        System.out.print("Enter new average time (minutes): ");
                        int newAverageTime = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Should this service be active? (Y/N): ");
                        String newStatus = sc.nextLine();

                        boolean newIsActive = newStatus.equalsIgnoreCase("Y");

                        boolean updated = dao.updateService(
                                updateId,
                                newName,
                                newDescription,
                                newAverageTime,
                                newIsActive
                        );

                        if (updated) {
                            System.out.println("\nSERVICE UPDATED SUCCESSFULLY!");
                        } else {
                            System.out.println("\nService update failed!");
                        }
                    }

                    break;


                // 5. DELETE SERVICE
                case 5:

                    System.out.println("\n========== DELETE SERVICE ==========");

                    System.out.print("Enter Service ID to delete: ");
                    int deleteId = sc.nextInt();
                    sc.nextLine();

                    boolean exists = dao.getServiceById(deleteId);

                    if (exists) {

                        System.out.print(
                                "\nAre you sure you want to delete this service? (Y/N): "
                        );

                        String confirmation = sc.nextLine();

                        if (confirmation.equalsIgnoreCase("Y")) {

                            boolean deleted = dao.deleteService(deleteId);

                            if (deleted) {
                                System.out.println("\nSERVICE DELETED SUCCESSFULLY!");
                            } else {
                                System.out.println("\nService deletion failed.");
                                System.out.println("Please check the message above.");
                            }

                        } else if (confirmation.equalsIgnoreCase("N")) {

                            System.out.println("\nService deletion cancelled.");

                        } else {

                            System.out.println("\nInvalid confirmation! Enter Y or N.");
                        }
                    }

                    break;


                // 0. BACK TO MAIN MENU
                case 0:

                    System.out.println("\nReturning to Main Menu...");
                    break;


                default:

                    System.out.println("\nInvalid choice! Please select between 0 and 5.");
            }

        } while (choice != 0);
    }
}
