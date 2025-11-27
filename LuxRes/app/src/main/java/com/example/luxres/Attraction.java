package com.example.luxres; // Use your correct package name

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull; // Use androidx annotation

public class Attraction implements Parcelable { // Implement Parcelable
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private String type;
    private String externalLink;

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

    // --- Parcelable Implementation ---
    protected Attraction(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        imageUrl = in.readString();
        type = in.readString();
        externalLink = in.readString();
    }

    public static final Creator<Attraction> CREATOR = new Creator<Attraction>() {
        @Override
        public Attraction createFromParcel(Parcel in) {
            return new Attraction(in);
        }

        @Override
        public Attraction[] newArray(int size) {
            return new Attraction[size];
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
        dest.writeString(imageUrl);
        dest.writeString(type);
        dest.writeString(externalLink);
    }
    // --- End Parcelable ---
}
