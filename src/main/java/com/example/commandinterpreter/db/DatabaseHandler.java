package com.example.commandinterpreter.db;

import com.example.commandinterpreter.model.Role;
import com.example.commandinterpreter.model.User;
import org.mindrot.jbcrypt.BCrypt; // For secure hashing (Encapsulation of security)

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/command_interpreter_db?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String USER = "root";
    private static final String PASS = "12345";

    public DatabaseHandler() {
        createTableIfNotExists();
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    private void createTableIfNotExists() {

        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    role ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
                """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public boolean registerUser(User user) {
        if (user == null || user.getUsername() == null || user.getPassword() == null) {
            return false;
        }

        String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12));
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername().trim());
            pstmt.setString(2, hashed);
            pstmt.setString(3, user.getRole().name());

            pstmt.executeUpdate();
            System.out.println("User registered successfully: " + user.getUsername());
            return true;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("Duplicate username: " + user.getUsername());
            } else {
                System.err.println("Database error during registration:");
                e.printStackTrace();
            }
            return false;
        }
    }

    public User loginUser(String username, String password) {
        if (username == null || password == null || username.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT password, role FROM users WHERE username = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username.trim());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                String roleStr = rs.getString("role");
                Role role = Role.valueOf(roleStr);


                if (storedPassword.startsWith("$2")) {
                    // Hashed with BCrypt → normal check
                    if (BCrypt.checkpw(password, storedPassword)) {
                        return new User(username, password, role);
                    }
                } else {

                    if (storedPassword.equals(password)) {
                        System.out.println("Upgrading old plain password to BCrypt for: " + username);


                        String newHashed = BCrypt.hashpw(password, BCrypt.gensalt(12));
                        updatePassword(username, newHashed);

                        return new User(username, password, role);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Login error:");
            e.printStackTrace();
        }
        return null;
    }

    // Helper method to update password
    private void updatePassword(String username, String newHashed) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newHashed);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
            System.out.println("Password upgraded to BCrypt for: " + username);

        } catch (SQLException e) {
            System.err.println("Failed to upgrade password:");
            e.printStackTrace();
        }
    }

    // Other methods...
    public void testConnection() {
        try (Connection conn = connect()) {
            System.out.println("DB Connected");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Save a command to history
    public void saveCommandHistory(int userId, String command) {
        String sql = "INSERT INTO command_history (user_id, command_text) VALUES (?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, command);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error saving command history:");
            e.printStackTrace();
        }
    }

    // Load all commands for a user, newest first
    public List<String> loadCommandHistory(int userId) {
        List<String> history = new ArrayList<>();
        String sql = "SELECT command_text FROM command_history WHERE user_id = ? ORDER BY executed_at DESC";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                history.add(rs.getString("command_text"));
            }

        } catch (SQLException e) {
            System.err.println("Error loading command history:");
            e.printStackTrace();
        }
        return history;
    }

    // Get user ID by username (used during login)
    public int getUserId(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }

        } catch (SQLException e) {
            System.err.println("Error getting user ID:");
            e.printStackTrace();
        }
        return -1; // Not found
    }
}