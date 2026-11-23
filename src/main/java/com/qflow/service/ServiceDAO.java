
package com.qflow.service;

import com.qflow.database.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServiceDAO {

    // 1. ADD SERVICE
    public boolean addService(String name, String description,
                              int averageTime, boolean isActive) {

        String sql = "INSERT INTO Service "
                   + "(service_name, description, average_time, is_active) "
                   + "VALUES (?, ?, ?, ?)";

        try (
            Connection con = DriverManager.getConnection(
                DatabaseConfig.URL,
                DatabaseConfig.USERNAME,
                DatabaseConfig.PASSWORD
            );

            PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setString(1, name);
            pst.setString(2, description);
            pst.setInt(3, averageTime);
            pst.setBoolean(4, isActive);

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // 2. VIEW ALL SERVICES
    public void getAllServices() {

        String sql = "SELECT * FROM Service";

        try (
            Connection con = DriverManager.getConnection(
                DatabaseConfig.URL,
                DatabaseConfig.USERNAME,
                DatabaseConfig.PASSWORD
            );

            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery()
        ) {

            System.out.println("\n========== ALL SERVICES ==========");

            while (rs.next()) {

                System.out.println("\nService ID   : " + rs.getInt("service_id"));
                System.out.println("Service Name : " + rs.getString("service_name"));
                System.out.println("Description  : " + rs.getString("description"));
                System.out.println("Average Time : " + rs.getInt("average_time") + " minutes");
                System.out.println("Status       : " +
                    (rs.getBoolean("is_active") ? "Active" : "Inactive"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // 3. SEARCH SERVICE BY ID
    public boolean getServiceById(int id) {

        String sql = "SELECT * FROM Service WHERE service_id = ?";

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

                System.out.println("\n========== SERVICE DETAILS ==========");

                System.out.println("Service ID   : " + rs.getInt("service_id"));
                System.out.println("Service Name : " + rs.getString("service_name"));
                System.out.println("Description  : " + rs.getString("description"));
                System.out.println("Average Time : " + rs.getInt("average_time") + " minutes");
                System.out.println("Status       : " +
                    (rs.getBoolean("is_active") ? "Active" : "Inactive"));

                return true;

            } else {

                System.out.println("\nService Not Found!");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // 4. UPDATE SERVICE
    public boolean updateService(int id, String name,
                                 String description, int averageTime,
                                 boolean isActive) {

        String sql = "UPDATE Service SET service_name = ?, "
                   + "description = ?, average_time = ?, "
                   + "is_active = ? WHERE service_id = ?";

        try (
            Connection con = DriverManager.getConnection(
                DatabaseConfig.URL,
                DatabaseConfig.USERNAME,
                DatabaseConfig.PASSWORD
            );

            PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setString(1, name);
            pst.setString(2, description);
            pst.setInt(3, averageTime);
            pst.setBoolean(4, isActive);
            pst.setInt(5, id);

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // 5. DELETE SERVICE
    public boolean deleteService(int id) {

        String sql = "DELETE FROM Service WHERE service_id = ?";

        try (
            Connection con = DriverManager.getConnection(
                DatabaseConfig.URL,
                DatabaseConfig.USERNAME,
                DatabaseConfig.PASSWORD
            );

            PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setInt(1, id);

            return pst.executeUpdate() > 0;

        } catch (SQLException e) {

            if (e.getErrorCode() == 1451) {
                System.out.println("\nService cannot be deleted!");
                System.out.println("This service is linked to existing records.");
            } else {
                e.printStackTrace();
            }

            return false;
        }
    }
}
