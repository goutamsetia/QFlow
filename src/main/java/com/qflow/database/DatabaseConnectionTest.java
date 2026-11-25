
package com.qflow.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnectionTest {

    public static void main(String[] args) {

        try (
            Connection con = DriverManager.getConnection(
                DatabaseConfig.URL,
                DatabaseConfig.USERNAME,
                DatabaseConfig.PASSWORD
            );

            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery(
                "SELECT COUNT(*) FROM Customer"
            )
        ) {

            System.out.println("Database Connected Successfully!");

            if (rs.next()) {
                int count = rs.getInt(1);

                System.out.println(
                    "Total Customers: " + count
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();

        }
    }
}
