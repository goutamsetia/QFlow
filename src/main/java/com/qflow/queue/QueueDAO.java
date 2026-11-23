package com.qflow.queue;
import com.qflow.database.DatabaseConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.qflow.token.TokenDAO;
import java.util.ArrayList;
import java.util.List;

public class QueueDAO {

public void viewCurrentQueue() {

    String sql =
            "SELECT t.token_id, t.token_number, "
          + "c.name, s.service_name, t.priority, "
          + "t.status, t.estimated_wait_time, "
          + "co.counter_name "
          + "FROM Token t "
          + "JOIN Customer c ON t.customer_id = c.customer_id "
          + "JOIN Service s ON t.service_id = s.service_id "
          + "LEFT JOIN Counter co ON t.counter_id = co.counter_id "
          + "WHERE t.status = 'WAITING' "
          + "ORDER BY "
          + "CASE t.priority "
          + "WHEN 'EMERGENCY' THEN 1 "
          + "WHEN 'SENIOR' THEN 2 "
          + "WHEN 'NORMAL' THEN 3 "
          + "END, t.created_at ASC";

    try (Connection con = DriverManager.getConnection(
                DatabaseConfig.URL,
                DatabaseConfig.USERNAME,
                DatabaseConfig.PASSWORD);

         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        System.out.println("\n========== CURRENT QUEUE ==========");

            List<String> queueRecords = new ArrayList<>();

        while (rs.next()) {

    String record =
            "----------------------------------\n"
          + "Token ID       : " + rs.getInt("token_id") + "\n"
          + "Token Number   : " + rs.getInt("token_number") + "\n"
          + "Customer Name  : " + rs.getString("name") + "\n"
          + "Service        : " + rs.getString("service_name") + "\n"
          + "Priority       : " + rs.getString("priority") + "\n"
          + "Status         : " + rs.getString("status") + "\n"
          + "Estimated Wait : " + rs.getInt("estimated_wait_time") + " minutes\n"
          + "Counter        : " + rs.getString("counter_name");

    queueRecords.add(record);
}

        if (queueRecords.isEmpty()) {
    System.out.println("No customers are currently waiting!");
} else {
   queueRecords.stream()
            .forEach(System.out::println);
}

        System.out.println("===================================");

    } catch (SQLException e) {
        System.out.println("Error viewing queue: " + e.getMessage());
    }
}


public void callNextToken(int counterId) {

    // STEP 1: Check whether the counter is already busy
    String checkSql =
            "SELECT COUNT(*) FROM Token "
          + "WHERE counter_id = ? AND status = 'SERVING'";

    // STEP 2: Find the next waiting token
    String sql =
            "SELECT t.token_id, t.token_number, "
          + "c.name, s.service_name, t.priority "
          + "FROM Token t "
          + "JOIN Customer c ON t.customer_id = c.customer_id "
          + "JOIN Service s ON t.service_id = s.service_id "
          + "JOIN Counter co ON t.service_id = co.service_id "
          + "WHERE t.status = 'WAITING' "
          + "AND co.counter_id = ? "
          + "AND co.is_active = 1 "
          + "ORDER BY "
          + "CASE t.priority "
          + "WHEN 'EMERGENCY' THEN 1 "
          + "WHEN 'SENIOR' THEN 2 "
          + "WHEN 'NORMAL' THEN 3 "
          + "END, t.created_at ASC "
          + "LIMIT 1";

    // STEP 3: Update the selected token
    String updateSql =
            "UPDATE Token "
          + "SET status = 'SERVING', counter_id = ? "
          + "WHERE token_id = ? AND status = 'WAITING'";

    try (Connection con = DriverManager.getConnection(
                DatabaseConfig.URL,
                DatabaseConfig.USERNAME,
                DatabaseConfig.PASSWORD)) {

        // STEP 4: Check if counter is busy
        try (PreparedStatement checkPst = con.prepareStatement(checkSql)) {

            checkPst.setInt(1, counterId);

            try (ResultSet checkRs = checkPst.executeQuery()) {

                if (checkRs.next() && checkRs.getInt(1) > 0) {
                    System.out.println("This counter is already serving a customer!");
                    return;
                }
            }
        }

        // STEP 5: Find the next token
        try (PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, counterId);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {

                    int tokenId = rs.getInt("token_id");

                    // STEP 6: Assign token to counter
                    try (PreparedStatement updatePst =
                            con.prepareStatement(updateSql)) {

                        updatePst.setInt(1, counterId);
                        updatePst.setInt(2, tokenId);

                        int rows = updatePst.executeUpdate();

                        if (rows > 0) {

                            System.out.println("\n===== TOKEN CALLED =====");
                            System.out.println("Token Number : " + rs.getInt("token_number"));
                            System.out.println("Customer     : " + rs.getString("name"));
                            System.out.println("Service      : " + rs.getString("service_name"));
                            System.out.println("Priority     : " + rs.getString("priority"));
                            System.out.println("Counter ID   : " + counterId);
                            System.out.println("Status       : SERVING");
                        }
                    }

                } else {
                    System.out.println("No waiting tokens for this counter!");
                }
            }
        }

    } catch (SQLException e) {
        System.out.println("Error calling next token: " + e.getMessage());
    }
}

public void viewServingTokens() {

    String sql =
            "SELECT t.token_number, c.name, "
          + "s.service_name, t.priority, "
          + "co.counter_name, t.status "
          + "FROM Token t "
          + "JOIN Customer c ON t.customer_id = c.customer_id "
          + "JOIN Service s ON t.service_id = s.service_id "
          + "LEFT JOIN Counter co ON t.counter_id = co.counter_id "
          + "WHERE t.status = 'SERVING' "
          + "ORDER BY co.counter_id";

    try (Connection con = DriverManager.getConnection(
                DatabaseConfig.URL,
                DatabaseConfig.USERNAME,
                DatabaseConfig.PASSWORD);

         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        System.out.println("\n========== SERVING TOKENS ==========");

        boolean found = false;

        while (rs.next()) {

            found = true;

            System.out.println("----------------------------------");
            System.out.println("Token Number : " + rs.getInt("token_number"));
            System.out.println("Customer     : " + rs.getString("name"));
            System.out.println("Service      : " + rs.getString("service_name"));
            System.out.println("Priority     : " + rs.getString("priority"));
            System.out.println("Counter      : " + rs.getString("counter_name"));
            System.out.println("Status       : " + rs.getString("status"));
        }

        if (!found) {
            System.out.println("No customers are currently being served!");
        }

        System.out.println("====================================");

    } catch (SQLException e) {
        System.out.println("Error viewing serving tokens: " + e.getMessage());
    }
}



public void completeService(int tokenId) {

    String sql =
            "UPDATE Token "
          + "SET status = 'COMPLETED', "
          + "completed_at = CURRENT_TIMESTAMP "
          + "WHERE token_id = ? AND status = 'SERVING'";

    try (Connection con = DriverManager.getConnection(
                DatabaseConfig.URL,
                DatabaseConfig.USERNAME,
                DatabaseConfig.PASSWORD);

         PreparedStatement pst = con.prepareStatement(sql)) {

        pst.setInt(1, tokenId);

        int rows = pst.executeUpdate();

        if (rows > 0) {
            System.out.println("Service completed successfully!");
            System.out.println("Counter is now available.");
        } else {
            System.out.println(
                "Token not found or token is not currently SERVING!"
            );
        }

    } catch (SQLException e) {
        System.out.println("Error completing service: " + e.getMessage());
    }
}


}
