-- Step 1: Create the Database
CREATE DATABASE IF NOT EXISTS command_interpreter_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci;

-- Step 2: Create a Dedicated User (replace 'javapass' with a strong password)
CREATE USER IF NOT EXISTS 'javauser'@'localhost' IDENTIFIED BY 'javapass';

-- Step 3: Grant Permissions to the User
GRANT ALL PRIVILEGES ON command_interpreter_db.* TO 'javauser'@'localhost';
FLUSH PRIVILEGES;

-- Step 4: Switch to the Database
USE command_interpreter_db;

-- Step 5: Create the Users Table (with role for scalability/role-based access)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER', -- Added for polymorphism in command access
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Optional: Insert Test Users (demonstrating roles)
INSERT INTO users (username, password, role) VALUES ('testuser', 'testpass', 'USER');
INSERT INTO users (username, password, role) VALUES ('admin', 'adminpass', 'ADMIN');

-- Step 6: Verify Setup
SELECT * FROM users;
SHOW GRANTS FOR 'javauser'@'localhost';