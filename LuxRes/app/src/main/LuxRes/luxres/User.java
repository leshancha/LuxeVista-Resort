package com.example.luxres;

// Simple User model (Consider making Parcelable if passed via Intent)
public class User {
    String userId;
    String name;
    String email;
    // Add other relevant fields like preferences, booking history list (IDs maybe)

    public User(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    // --- Getters (and potentially Setters) ---
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}