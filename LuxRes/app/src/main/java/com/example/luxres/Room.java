package com.example.luxres; // Use your correct package name

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull; // Use androidx annotation

public class Room implements Parcelable { // Ensure it implements Parcelable
    String id;
    String name;
    String description;
    double price; // per night
    String imageUrl; // <<< Field for the image URL
    boolean available;
    // Add more fields: amenities list, size, bed type, etc.

    public Room(String id, String name, String description, double price, String imageUrl, boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl; // <<< Assigned in constructor
        this.available = available;
    }

    // --- Getters ---
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; } // <<< Getter exists
    public boolean isAvailable() { return available; }

    // --- Parcelable Implementation ---
    protected Room(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        price = in.readDouble();
        imageUrl = in.readString(); // <<< Read from Parcel
        available = in.readByte() != 0;
    }

    public static final Creator<Room> CREATOR = new Creator<Room>() {
        @Override
        public Room createFromParcel(Parcel in) {
            return new Room(in);
        }

        @Override
        public Room[] newArray(int size) {
            return new Room[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeDouble(price);
        dest.writeString(imageUrl); // <<< Write to Parcel
        dest.writeByte((byte) (available ? 1 : 0));
    }
    // --- End Parcelable ---
}
