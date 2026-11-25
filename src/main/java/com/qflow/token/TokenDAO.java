
package com.qflow.token;

import com.qflow.database.DatabaseConfig;
import com.qflow.enums.TokenStatus;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.FileWriter;
import java.io.IOException;

public class TokenDAO {

    // 1. GENERATE TOKEN
    public boolean generateToken(int customerId, int serviceId,
                                 String priority) {

      String numberSql =
        "SELECT COALESCE(MAX(token_number), 0) + 1 "
      + "FROM Token WHERE issue_date = CURDATE()";

      String waitSql =
        "SELECT s.average_time, COUNT(t.token_id) "
      + "FROM Service s "
      + "LEFT JOIN Token t "
      + "ON s.service_id = t.service_id "
      + "AND t.status IN ('WAITING', 'SERVING') "
      + "WHERE s.service_id = ? "
      + "AND s.is_active = 1 "
      + "GROUP BY s.service_id, s.average_time";

       String insertSql =
        "INSERT INTO Token "
      + "(token_number, customer_id, service_id, priority, estimated_wait_time) "
      + "VALUES (?, ?, ?, ?, ?)";

        try (
            Connection con = DriverManager.getConnection(
                DatabaseConfig.URL,
                DatabaseConfig.USERNAME,
                DatabaseConfig.PASSWORD
            )
        ) {

            int tokenNumber = 1;

int estimatedWaitTime = 0;
int averageTime = 0;
int waitingCustomers = 0;

// Calculate estimated waiting time
try (PreparedStatement pst = con.prepareStatement(waitSql)) {

    pst.setInt(1, serviceId);

    try (ResultSet rs = pst.executeQuery()) {

        if (rs.next()) {

            averageTime = rs.getInt(1);
            waitingCustomers = rs.getInt(2);

            estimatedWaitTime = averageTime * waitingCustomers;

        } else {

            System.out.println("Service not found or inactive!");
            return false;
        }
    }
}


            // Find the next token number for today
            try (PreparedStatement pst =
                    con.prepareStatement(numberSql);
                 ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {
                    tokenNumber = rs.getInt(1);
                }
            }

            // Insert the new token
            try (PreparedStatement pst =
                    con.prepareStatement(insertSql)) {

                pst.setInt(1, tokenNumber);
                pst.setInt(2, customerId);
                pst.setInt(3, serviceId);
                pst.setString(4, priority);
                pst.setInt(5, estimatedWaitTime);

                int rows = pst.executeUpdate();

                if (rows > 0) {
                    System.out.println("\nTOKEN GENERATED SUCCESSFULLY!");
                    System.out.println("Token Number: " + tokenNumber);
                    System.out.println("Estimated Wait: " + estimatedWaitTime + " minutes");
                    System.out.println("Status      : WAITING");
                    return true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // 2. VIEW ALL TOKENS
    public void getAllTokens() {

        String sql =
                "SELECT t.token_id, t.token_number, "
              + "c.name AS customer_name, "
              + "s.service_name, "
              + "t.priority, t.status, "
              + "t.estimated_wait_time, t.issue_date "
              + "FROM Token t "
              + "JOIN Customer c ON t.customer_id = c.customer_id "
              + "JOIN Service s ON t.service_id = s.service_id "
              + "ORDER BY t.token_id DESC";

        try (
            Connection con = DriverManager.getConnection(
                DatabaseConfig.URL,
                DatabaseConfig.USERNAME,
                DatabaseConfig.PASSWORD
            );

            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery()
        ) {

            System.out.println("\n========== ALL TOKENS ==========");

            boolean found = false;

            while (rs.next()) {

                found = true;

                System.out.println("\nToken ID       : "
                        + rs.getInt("token_id"));

                System.out.println("Token Number   : "
                        + rs.getInt("token_number"));

                System.out.println("Customer Name  : "
                        + rs.getString("customer_name"));

                System.out.println("Service        : "
                        + rs.getString("service_name"));

                System.out.println("Priority       : "
                        + rs.getString("priority"));

                System.out.println("Status         : "
                        + rs.getString("status"));

                System.out.println("Estimated Wait : "
                        + rs.getInt("estimated_wait_time") + " minutes");

                System.out.println("Issue Date     : "
                        + rs.getDate("issue_date"));

                System.out.println("--------------------------------");
            }

            if (!found) {
                System.out.println("No tokens found!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 3. SEARCH TOKEN BY ID
    public boolean getTokenById(int id) {

        String sql =
                "SELECT t.token_id, t.token_number, "
              + "c.name AS customer_name, "
              + "s.service_name, "
              + "t.priority, t.status, "
              + "t.estimated_wait_time, t.issue_date "
              + "FROM Token t "
              + "JOIN Customer c ON t.customer_id = c.customer_id "
              + "JOIN Service s ON t.service_id = s.service_id "
              + "WHERE t.token_id = ?";

        try (
            Connection con = DriverManager.getConnection(
                DatabaseConfig.URL,
                DatabaseConfig.USERNAME,
                DatabaseConfig.PASSWORD
            );

            PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {

                    System.out.println("\n========== TOKEN DETAILS ==========");

                    System.out.println("Token ID       : "
                            + rs.getInt("token_id"));

                    System.out.println("Token Number   : "
                            + rs.getInt("token_number"));

                    System.out.println("Customer Name  : "
                            + rs.getString("customer_name"));

                    System.out.println("Service        : "
                            + rs.getString("service_name"));

                    System.out.println("Priority       : "
                            + rs.getString("priority"));

                    System.out.println("Status         : "
                            + rs.getString("status"));

                    System.out.println("Estimated Wait : "
                            + rs.getInt("estimated_wait_time") + " minutes");

                    System.out.println("Issue Date     : "
                            + rs.getDate("issue_date"));

                    return true;

                } else {

                    System.out.println("\nToken Not Found!");
                    return false;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. VIEW TOKENS BY CUSTOMER ID
    public void getTokensByCustomerId(int customerId) {

        String sql =
                "SELECT t.token_id, t.token_number, "
              + "c.name AS customer_name, "
              + "s.service_name, "
              + "t.priority, t.status, "
              + "t.estimated_wait_time, t.issue_date "
              + "FROM Token t "
              + "JOIN Customer c ON t.customer_id = c.customer_id "
              + "JOIN Service s ON t.service_id = s.service_id "
              + "WHERE t.customer_id = ? "
              + "ORDER BY t.token_id DESC";

        try (
            Connection con = DriverManager.getConnection(
                DatabaseConfig.URL,
                DatabaseConfig.USERNAME,
                DatabaseConfig.PASSWORD
            );

            PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setInt(1, customerId);

            try (ResultSet rs = pst.executeQuery()) {

                System.out.println("\n========== CUSTOMER TOKENS ==========");

                boolean found = false;

                while (rs.next()) {

                    found = true;

                    System.out.println("\nToken ID       : "
                            + rs.getInt("token_id"));

                    System.out.println("Token Number   : "
                            + rs.getInt("token_number"));

                    System.out.println("Customer Name  : "
                            + rs.getString("customer_name"));

                    System.out.println("Service        : "
                            + rs.getString("service_name"));

                    System.out.println("Priority       : "
                            + rs.getString("priority"));

                    System.out.println("Status         : "
                            + rs.getString("status"));

                    System.out.println("Estimated Wait : "
                            + rs.getInt("estimated_wait_time") + " minutes");

                    System.out.println("Issue Date     : "
                            + rs.getDate("issue_date"));

                    System.out.println("--------------------------------");
                }

                if (!found) {
                    System.out.println("No tokens found for this customer!");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 5. UPDATE TOKEN STATUS
    public boolean updateTokenStatus(int tokenId, String status) {
                // Validate token status before updating the database
        if (status == null) {
            System.out.println("\nInvalid token status!");
            System.out.println("Allowed statuses: WAITING, SERVING, COMPLETED, CANCELLED");
            return false;
        }

        try {
            TokenStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("\nInvalid token status!");
            System.out.println("Allowed statuses: WAITING, SERVING, COMPLETED, CANCELLED");
            return false;
        }

        String sql =
        "UPDATE Token SET status = ?, "
      + "completed_at = CASE "
      + "WHEN ? = 'COMPLETED' THEN CURRENT_TIMESTAMP "
      + "ELSE completed_at END "
      + "WHERE token_id = ?";
        try (
            Connection con = DriverManager.getConnection(
                DatabaseConfig.URL,
                DatabaseConfig.USERNAME,
                DatabaseConfig.PASSWORD
            );

            PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setString(1, status);
            pst.setString(2, status);
            pst.setInt(3, tokenId);

            int rows = pst.executeUpdate();

            if (rows > 0) {

                System.out.println("\nTOKEN STATUS UPDATED SUCCESSFULLY!");
                System.out.println("New Status: " + status);

                return true;

            } else {

                System.out.println("\nToken Not Found!");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 6. CANCEL TOKEN
    public boolean cancelToken(int tokenId) {

        String sql =
                "UPDATE Token SET status = 'CANCELLED' "
              + "WHERE token_id = ? AND status = 'WAITING'";

        try (
            Connection con = DriverManager.getConnection(
                DatabaseConfig.URL,
                DatabaseConfig.USERNAME,
                DatabaseConfig.PASSWORD
            );

            PreparedStatement pst = con.prepareStatement(sql)
        ) {

            pst.setInt(1, tokenId);

            int rows = pst.executeUpdate();

            if (rows > 0) {

                System.out.println("\nTOKEN CANCELLED SUCCESSFULLY!");
                return true;

            } else {

                System.out.println(
                    "\nCancellation failed! Token not found or is not WAITING."
                );

                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 7. EXPORT TOKENS TO CSV
    public void exportTokensToCSV() {

        String sql =
                "SELECT t.token_id, t.token_number, "
              + "c.name AS customer_name, "
              + "s.service_name, "
              + "t.priority, t.status, "
              + "t.estimated_wait_time, t.issue_date "
              + "FROM Token t "
              + "JOIN Customer c ON t.customer_id = c.customer_id "
              + "JOIN Service s ON t.service_id = s.service_id "
              + "ORDER BY t.token_id";

        try (
            Connection con = DriverManager.getConnection(
                DatabaseConfig.URL,
                DatabaseConfig.USERNAME,
                DatabaseConfig.PASSWORD
            );

            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            FileWriter writer = new FileWriter("qflow_tokens.csv")
        ) {

            // Write CSV headings
            writer.write(
                "Token ID,Token Number,Customer Name,Service,"
              + "Priority,Status,Estimated Wait,Issue Date\n"
            );

            // Write token records
            while (rs.next()) {

                writer.write(
                    rs.getInt("token_id") + ","
                  + rs.getInt("token_number") + ","
                  + "\"" + rs.getString("customer_name").replace("\"", "\"\"") + "\","
                  + "\"" + rs.getString("service_name").replace("\"", "\"\"") + "\","
                  + rs.getString("priority") + ","
                  + rs.getString("status") + ","
                  + rs.getInt("estimated_wait_time") + ","
                  + rs.getDate("issue_date")
                  + "\n"
                );
            }

            System.out.println("\nTOKENS EXPORTED SUCCESSFULLY!");
            System.out.println("File Name: qflow_tokens.csv");

        } catch (SQLException | IOException e) {

            System.out.println("Error exporting tokens: " + e.getMessage());
        }
    }



}
