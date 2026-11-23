
package com.qflow.customer;

import java.util.Scanner;

public class CustomerMenu {

    public void showMenu(Scanner sc) {

        CustomerDAO dao = new CustomerDAO();

        int choice;

        do {

            System.out.println("\n");
System.out.println("================================================");
System.out.println("             CUSTOMER MANAGEMENT                ");
System.out.println("================================================");

System.out.println();
System.out.println("       What would you like to do?");
System.out.println();

System.out.println("       [1]  Add New Customer");
System.out.println("       [2]  View All Customers");
System.out.println("       [3]  Search Customer By ID");
System.out.println("       [4]  Update Customer Details");
System.out.println("       [5]  Delete Customer");
System.out.println("       [6]  Import Customers from CSV");
System.out.println("       [0]  Back to Main Menu");

System.out.println();
System.out.println("================================================");

System.out.print("  Enter your choice (0-6): ");
            choice = sc.nextInt();
            sc.nextLine(); // Consume the leftover newline

            switch (choice) {

                case 1:

                    System.out.println("\n--- ADD NEW CUSTOMER ---");

                    System.out.print("Enter customer name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter phone number: ");
                    String phone = sc.nextLine();

                    System.out.print("Enter email: ");
                    String email = sc.nextLine();

                    boolean result = dao.addCustomer(name, phone, email);

                    if (result) {
                        System.out.println("\nCustomer Added Successfully!");
                    } else {
                        System.out.println("\nCustomer Addition Failed!");
                    }

                    break;

                case 2:

                    System.out.println("\nAll Customers:");
                    dao.getAllCustomers();
                    break;

                case 3:

                    System.out.print("Enter Customer ID: ");
                    int id = sc.nextInt();
                    sc.nextLine();

                    dao.getCustomerById(id);
                    break;

                case 4:
                    System.out.println("\n================================================");
    System.out.println("                 UPDATE CUSTOMER                ");
    System.out.println("================================================");

    System.out.print("\nEnter Customer ID to update: ");
    int updateId = sc.nextInt();
    sc.nextLine(); // Consume leftover newline

    System.out.println("\nEnter the new customer details:");

    System.out.print("Enter new name: ");
    String newName = sc.nextLine();

    System.out.print("Enter new phone number: ");
    String newPhone = sc.nextLine();

    System.out.print("Enter new email: ");
    String newEmail = sc.nextLine();

    boolean updated = dao.updateCustomer(
            updateId, newName, newPhone, newEmail
    );

    if (updated) {
        System.out.println("\n================================================");
        System.out.println("          CUSTOMER UPDATED SUCCESSFULLY!         ");
        System.out.println("================================================");
    } else {
        System.out.println("\nCustomer update failed!");
        System.out.println("Please check the Customer ID and try again.");
    }

                    break;



case 5:

    System.out.println("\n================================================");
    System.out.println("                 DELETE CUSTOMER                ");
    System.out.println("================================================");

    System.out.print("\nEnter Customer ID to delete: ");
    int deleteId = sc.nextInt();
    sc.nextLine();

    // First, display customer details
    boolean exists = dao.getCustomerById(deleteId);

    // Continue only if customer exists
    if (exists) {

        System.out.print("\nAre you sure you want to delete this customer? (Y/N): ");
        String confirmation = sc.nextLine();

        if (confirmation.equalsIgnoreCase("Y")) {

            boolean deleted = dao.deleteCustomer(deleteId);

            if (deleted) {
                System.out.println("\nCUSTOMER DELETED SUCCESSFULLY!");
            } else {
                System.out.println("\nDeletion could not be completed.");
                System.out.println("Please check the message above.");
            }

        } else if (confirmation.equalsIgnoreCase("N")) {

            System.out.println("\nCustomer deletion cancelled.");

        } else {

            System.out.println("\nInvalid confirmation! Please enter Y or N.");
        }
    }

    break;
                // 6. IMPORT CUSTOMERS FROM CSV
                case 6:

                    System.out.println("\n========== IMPORT CUSTOMERS FROM CSV ==========");

                    System.out.print("Enter CSV file path: ");
                    String filePath = sc.nextLine();

                    dao.importCustomersFromCSV(filePath);

                    break;

                case 0:
                    System.out.println("Returning to Main Menu...");
                    break;

                default:
                    System.out.println("Invalid Choice!");
            }

        } while (choice != 0);
    }
}
