package com.example.commandinterpreter.model;

// Enum for user roles (Polymorphism: Different behavior based on role)
public enum Role {
    USER,  // Basic access
    ADMIN  // Full access (e.g., power commands)
}