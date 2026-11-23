
package com.qflow.app;

import com.qflow.customer.CustomerDAO;

public class CustomerTest {

    public static void main(String[] args) {

        CustomerDAO dao = new CustomerDAO();

        // Delete customer with ID 9
        boolean result = dao.deleteCustomer(9);

        if (result) {
            System.out.println("Customer Deleted Successfully!");
        } else {
            System.out.println("Customer Not Found!");
        }

        // Display remaining customers
        System.out.println("\nRemaining Customers:");

        dao.getAllCustomers();
    }
}
