
package com.qflow.customer;

import com.qflow.database.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CustomerDAO {

    // 1. Add a new customer
    public boolean addCustomer(String name, String phone, String email) {

        String sql = "INSERT INTO Customer (name, phone, email) VALUES (?, ?, ?)";

        try (
            Connection con = DriverManager.getConnection(
                DatabaseConfig.URL,
                DatabaseConfig.USERNAME,
                DatabaseConfig.PASSWORD
            );

            PreparedStatement pstmt = con.prepareStatement(sql)
        ) {

            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, email);

            int rows = pstmt.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 2. Get all customers
    public void getAllCustomers() {

        String sql = "SELECT * FROM Customer";

        try (
            Connection con = DriverManager.getConnection(
                DatabaseConfig.URL,
                DatabaseConfig.USERNAME,
                DatabaseConfig.PASSWORD
            );

            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery(sql)
        ) {

            while (rs.next()) {

                int id = rs.getInt("customer_id");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String email = rs.getString("email");

                System.out.println("ID: " + id);
                System.out.println("Name: " + name);
                System.out.println("Phone: " + phone);
                System.out.println("Email: " + email);

                System.out.println("----------------------");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 3. Get customer by ID
   
public boolean getCustomerById(int id) {

    String sql = "SELECT * FROM Customer WHERE customer_id = ?";

    try (
        Connection con = DriverManager.getConnection(
            DatabaseConfig.URL,
            DatabaseConfig.USERNAME,
            DatabaseConfig.PASSWORD
        );

        PreparedStatement pst = con.prepareStatement(sql)
    ) {

        pst.setInt(1, id);

        ResultSet rs = pst.executeQuery();

        if (rs.next()) {

            System.out.println("\n----------------------------------------");
            System.out.println("          CUSTOMER DETAILS");
            System.out.println("----------------------------------------");

            System.out.println("Customer ID : " + rs.getInt("customer_id"));
            System.out.println("Name        : " + rs.getString("name"));
            System.out.println("Phone       : " + rs.getString("phone"));
            System.out.println("Email       : " + rs.getString("email"));

            System.out.println("----------------------------------------");

            return true;

        } else {

            System.out.println("\nCustomer Not Found!");
            return false;
        }

    } catch (SQLException e) {

        e.printStackTrace();
        return false;
    }
}


// 4. Update customer details
public boolean updateCustomer(int id, String name, String phone, String email) {

    String sql = "UPDATE Customer SET name = ?, phone = ?, email = ? WHERE customer_id = ?";

    try (
        Connection con = DriverManager.getConnection(
            DatabaseConfig.URL,
            DatabaseConfig.USERNAME,
            DatabaseConfig.PASSWORD
        );

        PreparedStatement pstmt = con.prepareStatement(sql)
    ) {

        pstmt.setString(1, name);
        pstmt.setString(2, phone);
        pstmt.setString(3, email);
        pstmt.setInt(4, id);

        int rows = pstmt.executeUpdate();

        return rows > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    // 5. Delete customer by ID
   public boolean deleteCustomer(int id) {

    String sql = "DELETE FROM Customer WHERE customer_id = ?";

    try (
        Connection con = DriverManager.getConnection(
            DatabaseConfig.URL,
            DatabaseConfig.USERNAME,
            DatabaseConfig.PASSWORD
        );

        PreparedStatement pst = con.prepareStatement(sql)
    ) {

        pst.setInt(1, id);

        int rows = pst.executeUpdate();

        return rows > 0;

    } catch (SQLException e) {

        // Customer has related token records
        if (e.getErrorCode() == 1451) {

            System.out.println("\nCustomer cannot be deleted!");
            System.out.println("This customer has existing token records.");
            System.out.println("Please resolve the related tokens first.");

        } else {

            e.printStackTrace();
        }

        return false;
    }
}

    // 6. IMPORT CUSTOMERS FROM CSV
public void importCustomersFromCSV(String filePath) {

        String sql =
                "INSERT INTO Customer (name, phone, email) "
              + "VALUES (?, ?, ?)";

        int imported = 0;
        int skipped = 0;

        try (
            Connection con = DriverManager.getConnection(
                DatabaseConfig.URL,
                DatabaseConfig.USERNAME,
                DatabaseConfig.PASSWORD
            );

            PreparedStatement pst = con.prepareStatement(sql);

            BufferedReader reader = new BufferedReader(
new FileReader(filePath)
            )
        ) {

            // Skip the first row containing column headings
            String line = reader.readLine();

            // Read customer records
            while ((line = reader.readLine()) != null) {

                String[] data = line.split(",", -1);

                if (data.length != 3) {
                    skipped++;
                    continue;
                }

                String name = data[0].trim();
                String phone = data[1].trim();
                String email = data[2].trim();

                // Validate customer details before database insertion
                if (name.isEmpty() || name.length() > 100
                        || phone.isEmpty()
                        || !phone.matches("\\d{10,15}")
                        || (!email.isEmpty() &&
                            (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
                            || email.length() > 100))) {

                    skipped++;
                    continue;
                }

                try {

                    pst.setString(1, name);
                    pst.setString(2, phone);

                    if (email.isEmpty()) {
                        pst.setNull(3, java.sql.Types.VARCHAR);
                    } else {
                        pst.setString(3, email);
                    }

                    pst.executeUpdate();
                    imported++;

                } catch (SQLException e) {

                    // Duplicate phone number
                    if (e.getErrorCode() == 1062) {//1062->means any duplicate entry
                        skipped++;
                    } else {
                        throw e;
                    }
                }
            }

            System.out.println("\nCUSTOMER IMPORT COMPLETED!");
            System.out.println("Imported Customers: " + imported);
            System.out.println("Skipped Records: " + skipped);

        } catch (SQLException | IOException e) {

            System.out.println("Error importing customers: " + e.getMessage());
        }
    }
}
