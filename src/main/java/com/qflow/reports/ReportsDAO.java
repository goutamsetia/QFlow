package com.qflow.reports;

//Imports  database connection constants (URL, USERNAME, PASSWORD).
import com.qflow.database.DatabaseConfig;

//Loads JDBC interfaces and classes for executing SQL queries and handling SQL errors
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//Loads collection types and sorting utilities for processing query rows in Java.

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class ReportsDAO {

    // 1. TOKEN SUMMARY REPORT
    public void getTokenSummary() {

        String sql =
                "SELECT COUNT(*) AS total_tokens, "
              + "SUM(CASE WHEN status = 'WAITING' THEN 1 ELSE 0 END) AS waiting, "
              + "SUM(CASE WHEN status = 'SERVING' THEN 1 ELSE 0 END) AS serving, "
              + "SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed, "
              + "SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelled "
              + "FROM Token";

        try (Connection con = DriverManager.getConnection(
                    DatabaseConfig.URL,
                    DatabaseConfig.USERNAME,
                    DatabaseConfig.PASSWORD);

             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {

                System.out.println("\n========== TOKEN SUMMARY REPORT ==========");
                System.out.println("Total Tokens     : " + rs.getInt("total_tokens"));
                System.out.println("Waiting Tokens   : " + rs.getInt("waiting"));
                System.out.println("Serving Tokens   : " + rs.getInt("serving"));
                System.out.println("Completed Tokens : " + rs.getInt("completed"));
                System.out.println("Cancelled Tokens : " + rs.getInt("cancelled"));

                System.out.println("==========================================");
            }

        } catch (SQLException e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }


    // 2. SERVICE-WISE TOKEN REPORT
    public void getServiceWiseReport() {

        String sql =
                "SELECT s.service_name, "
              + "COUNT(t.token_id) AS total_tokens, "
              + "SUM(CASE WHEN t.status = 'WAITING' THEN 1 ELSE 0 END) AS waiting, "
              + "SUM(CASE WHEN t.status = 'SERVING' THEN 1 ELSE 0 END) AS serving, "
              + "SUM(CASE WHEN t.status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed, "
              + "SUM(CASE WHEN t.status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelled "
              + "FROM Service s "
              + "LEFT JOIN Token t ON s.service_id = t.service_id "
              + "GROUP BY s.service_id, s.service_name "
              + "ORDER BY s.service_id";

        try (Connection con = DriverManager.getConnection(
                    DatabaseConfig.URL,
                    DatabaseConfig.USERNAME,
                    DatabaseConfig.PASSWORD);

             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            System.out.println("\n========== SERVICE-WISE TOKEN REPORT ==========");

            List<String> serviceRecords = new ArrayList<>();
            boolean hasRecords = false;
            while (rs.next()) {
                if (rs.getInt("total_tokens") > 0) {
                    hasRecords = true;
                }
                String record =
                        rs.getString("service_name") + " | "
                      + rs.getInt("total_tokens") + " | "
                      + rs.getInt("waiting") + " | "
                      + rs.getInt("serving") + " | "
                      + rs.getInt("completed") + " | "
                      + rs.getInt("cancelled");

                serviceRecords.add(record);
            }
            if (!hasRecords) {
                System.out.println("No service records with tokens found.");
                System.out.println("================================================");
                return;
            }
            serviceRecords.stream()

                    // filter(): Keep services having at least one token
                    .filter(record ->
                            Integer.parseInt(record.split(" \\| ")[1]) > 0)

                    // distinct(): Remove duplicate records
                    .distinct()

                    // sorted(): Highest token count first
                    .sorted(Comparator.comparingInt(
                            (String record) ->
                            Integer.parseInt(record.split(" \\| ")[1])
                    ).reversed())

                    // skip(): Skip zero records
                    .skip(0)

                    // limit(): Display at most 5 services
                    .limit(5)

                    // forEach(): Display records
                    .forEach(System.out::println);

            System.out.println("\n================================================");

        } catch (SQLException e) {
            System.out.println("Error generating service-wise report: " + e.getMessage());
        }
    }


    // 3. DAILY TOKEN REPORT
    public void getDailyTokenReport() {

        String sql =
                "SELECT COUNT(*) AS total_tokens, "
              + "SUM(CASE WHEN status = 'WAITING' THEN 1 ELSE 0 END) AS waiting, "
              + "SUM(CASE WHEN status = 'SERVING' THEN 1 ELSE 0 END) AS serving, "
              + "SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed, "
              + "SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelled "
              + "FROM Token "
              + "WHERE issue_date = CURDATE()";

        try (Connection con = DriverManager.getConnection(
                    DatabaseConfig.URL,
                    DatabaseConfig.USERNAME,
                    DatabaseConfig.PASSWORD);

             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {

                System.out.println("\n========== DAILY TOKEN REPORT ==========");

                System.out.println("Date             : " +
                        java.time.LocalDate.now());

                System.out.println("Total Tokens     : " +
                        rs.getInt("total_tokens"));

                System.out.println("Waiting Tokens   : " +
                        rs.getInt("waiting"));

                System.out.println("Serving Tokens   : " +
                        rs.getInt("serving"));

                System.out.println("Completed Tokens : " +
                        rs.getInt("completed"));

                System.out.println("Cancelled Tokens : " +
                        rs.getInt("cancelled"));

                System.out.println("=========================================");
            }

        } catch (SQLException e) {
            System.out.println("Error generating daily report: " + e.getMessage());
        }
    }


    // 4. AVERAGE SERVICE TIME REPORT
    public void getAverageServiceTime() {

        String sql =
                "SELECT AVG(TIMESTAMPDIFF(MINUTE, "
              + "created_at, completed_at)) AS average_time "
              + "FROM Token "
              + "WHERE status = 'COMPLETED' "
              + "AND completed_at IS NOT NULL";

        try (Connection con = DriverManager.getConnection(
                    DatabaseConfig.URL,
                    DatabaseConfig.USERNAME,
                    DatabaseConfig.PASSWORD);

             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            if (rs.next()) {

                double averageTime = rs.getDouble("average_time");

                System.out.println("\n========== AVERAGE SERVICE TIME REPORT ==========");

                if (rs.wasNull()) {
                    System.out.println("No completed tokens available.");
                } else {
                    System.out.printf(
                        "Average Service Time : %.2f minutes%n",
                        averageTime
                    );
                }

                System.out.println("=================================================");
            }

        } catch (SQLException e) {
            System.out.println("Error generating average service time report: "
                    + e.getMessage());
        }
    }


    // 5. PRIORITY-WISE TOKEN REPORT
    public void getPriorityWiseReport() {

        String sql =
                "SELECT priority, "
              + "COUNT(*) AS total_tokens, "
              + "SUM(CASE WHEN status = 'WAITING' THEN 1 ELSE 0 END) AS waiting, "
              + "SUM(CASE WHEN status = 'SERVING' THEN 1 ELSE 0 END) AS serving, "
              + "SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed, "
              + "SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelled "
              + "FROM Token "
              + "GROUP BY priority "
              + "ORDER BY CASE priority "
              + "WHEN 'EMERGENCY' THEN 1 "
              + "WHEN 'SENIOR' THEN 2 "
              + "WHEN 'NORMAL' THEN 3 "
              + "END";

        try (Connection con = DriverManager.getConnection(
                    DatabaseConfig.URL,
                    DatabaseConfig.USERNAME,
                    DatabaseConfig.PASSWORD);

             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            System.out.println("\n========== PRIORITY-WISE TOKEN REPORT ==========");

            while (rs.next()) {

                System.out.println("\nPriority         : " + rs.getString("priority"));
                System.out.println("Total Tokens     : " + rs.getInt("total_tokens"));
                System.out.println("Waiting Tokens   : " + rs.getInt("waiting"));
                System.out.println("Serving Tokens   : " + rs.getInt("serving"));
                System.out.println("Completed Tokens : " + rs.getInt("completed"));
                System.out.println("Cancelled Tokens : " + rs.getInt("cancelled"));
            }

            System.out.println("\n================================================");

        } catch (SQLException e) {
            System.out.println("Error generating priority-wise report: "
                    + e.getMessage());
        }
    }


    // 6. COUNTER PERFORMANCE REPORT
    public void getCounterPerformanceReport() {

        String sql =
                "SELECT c.counter_name, "
              + "COUNT(t.token_id) AS total_tokens, "
              + "SUM(CASE WHEN t.status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed, "
              + "SUM(CASE WHEN t.status = 'SERVING' THEN 1 ELSE 0 END) AS serving "
              + "FROM Counter c "
              + "LEFT JOIN Token t ON c.counter_id = t.counter_id "
              + "GROUP BY c.counter_id, c.counter_name "
              + "ORDER BY c.counter_id";

        try (Connection con = DriverManager.getConnection(
                    DatabaseConfig.URL,
                    DatabaseConfig.USERNAME,
                    DatabaseConfig.PASSWORD);

             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            System.out.println("\n========== COUNTER PERFORMANCE REPORT ==========");

            while (rs.next()) {

                System.out.println("\nCounter Name     : " + rs.getString("counter_name"));
                System.out.println("Total Tokens     : " + rs.getInt("total_tokens"));
                System.out.println("Completed Tokens : " + rs.getInt("completed"));
                System.out.println("Serving Tokens   : " + rs.getInt("serving"));
            }

            System.out.println("\n=================================================");

        } catch (SQLException e) {
            System.out.println("Error generating counter performance report: "
                    + e.getMessage());
        }
    }

    // 7. GENERATE REPORTS USING MULTITHREADING
   public void generateAllReportsParallel() {

    System.out.println("\n========== GENERATING ALL REPORTS ==========");

    Thread report1 = new Thread(new Runnable() {

        @Override
        public void run() {
            System.out.println("\n[Thread 1] Generating Token Summary...");
            getTokenSummary();
        }
    });

    Thread report2 = new Thread(new Runnable() {

        @Override
        public void run() {
            System.out.println("\n[Thread 2] Generating Service-wise Report...");
            getServiceWiseReport();
        }
    });

    Thread report3 = new Thread(new Runnable() {

        @Override
        public void run() {
            System.out.println("\n[Thread 3] Generating Counter Performance...");
            getCounterPerformanceReport();
        }
    });

    // Start all reports at the same time
    report1.start();
    report2.start();
    report3.start();

    try {
        // Wait for all report threads to finish
        report1.join();
        report2.join();
        report3.join();

        System.out.println("\nAll reports generated successfully!");

    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        System.out.println("Report generation was interrupted.");
    }
}
}
