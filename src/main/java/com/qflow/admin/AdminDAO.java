
package com.qflow.admin;

import com.qflow.database.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class AdminDAO {

    // Generate a secure password hash
    private String hashPassword(String password, String salt)
            throws Exception {

        KeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                Base64.getDecoder().decode(salt),
                65536,
                256
        );

        SecretKeyFactory factory =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] hash = factory.generateSecret(spec).getEncoded();

        return Base64.getEncoder().encodeToString(hash);
    }

    // Register a new admin
    public boolean registerAdmin(String username,
                                 String password,
                                 String fullName) {

        try {
            byte[] saltBytes = new byte[16];
            new SecureRandom().nextBytes(saltBytes);

            String salt =
                    Base64.getEncoder().encodeToString(saltBytes);

            String passwordHash = hashPassword(password, salt);

            // Store salt and hash together
            String storedPassword = salt + ":" + passwordHash;

            String sql =
                    "INSERT INTO Admin "
                  + "(username, password_hash, full_name, role) "
                  + "VALUES (?, ?, ?, 'ADMIN')";

            try (Connection con = DriverManager.getConnection(
                        DatabaseConfig.URL,
                        DatabaseConfig.USERNAME,
                        DatabaseConfig.PASSWORD);

                 PreparedStatement pst = con.prepareStatement(sql)) {

                pst.setString(1, username);
                pst.setString(2, storedPassword);
                pst.setString(3, fullName);

                return pst.executeUpdate() > 0;
            }

        } catch (Exception e) {
            System.out.println("Registration error: " + e.getMessage());
            return false;
        }
    }

    // Login an admin
    public boolean loginAdmin(String username, String password) {

        String sql =
                "SELECT password_hash FROM Admin "
              + "WHERE username = ? AND is_active = 1";

        try (Connection con = DriverManager.getConnection(
                    DatabaseConfig.URL,
                    DatabaseConfig.USERNAME,
                    DatabaseConfig.PASSWORD);

             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, username);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {

                    String storedPassword =
                            rs.getString("password_hash");

                    String[] parts = storedPassword.split(":");

                    if (parts.length != 2) {
                        return false;
                    }

                    String salt = parts[0];
                    String originalHash = parts[1];

                    String enteredHash =
                            hashPassword(password, salt);

                    return originalHash.equals(enteredHash);
                }
            }

        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
        }

        return false;
    }

public boolean adminExists() {

    String sql = "SELECT COUNT(*) FROM Admin";

    try (Connection con = DriverManager.getConnection(
                DatabaseConfig.URL,
                DatabaseConfig.USERNAME,
                DatabaseConfig.PASSWORD);

         PreparedStatement pst = con.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {

        if (rs.next()) {
            return rs.getInt(1) > 0;
        }

    } catch (SQLException e) {
        System.out.println("Error checking admin: " + e.getMessage());
    }

    // If database checking fails, do not allow registration.
    return true;
}

}
