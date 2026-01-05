package com.example.commandinterpreter.db;

import com.example.commandinterpreter.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * MySQL Database Handler - Updated for latest Connector/J 9.5.0
 */
public class DatabaseHandler {
    // UPDATE THESE WITH YOUR MySQL CREDENTIALS
    private static final String DB_URL = "jdbc:mysql://localhost:3306/command_interpreter_db?useSSL=false&serverTimezone=UTC";
    private static final String javauser = "root";  // Or your custom user
    private static final String javapass = "12345";  // Set this!

    public DatabaseHandler() {
        createTableIfNotExists();
    }

    public void testConnection() {
        try (Connection conn = connect()) {
            System.out.println("MySQL Connection Successful!");
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
    }


    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, javauser, javapass);
    }

    private void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
            """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("MySQL 'users' table ready.");
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean registerUser(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            return false;
        }

        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username.trim());
            pstmt.setString(2, password); // Recommend hashing in production!
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry
                System.out.println("Username already exists: " + username);
            } else {
                System.err.println("Registration error: " + e.getMessage());
            }
            return false;
        }
    }

    public boolean registerUser(User user) {
        if (user == null) return false;
        return registerUser(user.getUsername(), user.getPassword());
    }

    public User loginUser(String username, String password) {
        if (username == null || password == null) return null;

        String sql = "SELECT username, password FROM users WHERE username = ? AND password = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username.trim());
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("username"), rs.getString("password"));
            }

        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
        }
        return null;
    }

    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username.trim());
            return pstmt.executeQuery().next();

        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
            return false;
        }
    }
}