package com.example.luxres;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Represents the User table in the database.
 * The email column is unique to prevent duplicate registrations with the same email.
 */
@Entity(tableName = "users", indices = {@Index(value = {"email"}, unique = true)})
public class UserEntity {

    @PrimaryKey(autoGenerate = true)
    public int uid; // Auto-generated unique ID for each user

    @ColumnInfo(name = "name")
    @NonNull // Ensure name is never null
    public String name;

    @ColumnInfo(name = "email")
    @NonNull // Ensure email is never null
    public String email;

    @ColumnInfo(name = "password_hash")
    @NonNull // Store a HASH of the password, never the plain text
    public String passwordHash;

    // Constructor (Room uses this)
    // Note: It's often good practice to provide getters/setters,
    // but public fields are allowed for Room entities for simplicity.
    public UserEntity(@NonNull String name, @NonNull String email, @NonNull String passwordHash) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
    }
}
