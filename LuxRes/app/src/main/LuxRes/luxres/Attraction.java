package com.example.luxres;

public class Attraction {
    String id;
    String name;
    String description;
    String imageUrl;
    String type; // "Offer", "Nearby Attraction", "Hotel Event"
    String externalLink; // Optional link for more info or map

    public Attraction(String id, String name, String description, String imageUrl, String type, String externalLink) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.type = type;
        this.externalLink = externalLink;
    }

    // --- Getters ---
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getType() { return type; }
    public String getExternalLink() { return externalLink; }
}