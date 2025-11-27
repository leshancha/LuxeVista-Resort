package com.example.luxres;

// Simple Service model (Consider Parcelable)
public class Service {
    String id;
    String name;
    String description;
    double price; // Optional, might be per hour or fixed
    String category; // e.g., "Spa", "Dining", "Activity"
    String imageUrl; // Optional

    public Service(String id, String name, String description, double price, String category, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    // --- Getters ---
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public String getImageUrl() { return imageUrl; }
}