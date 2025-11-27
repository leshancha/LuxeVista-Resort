package com.example.luxres; // Use your correct package name

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Represents the Bookings table in the database.
 * Uses ForeignKey to link to the UserEntity table.
 */
@Entity(tableName = "bookings",
        foreignKeys = @ForeignKey(entity = UserEntity.class,
                parentColumns = "uid", // Column in UserEntity
                childColumns = "user_id", // Column in BookingEntity
                onDelete = ForeignKey.CASCADE), // Delete bookings if user is deleted
        indices = {@Index(value = "user_id")}) // Index on user_id for faster lookups
public class BookingEntity {

    @PrimaryKey
    @NonNull // Booking ID should not be null
    @ColumnInfo(name = "booking_id")
    public String bookingId; // Use a unique ID generated before insertion (e.g., from API or UUID)

    @ColumnInfo(name = "user_id")
    public int userId; // Foreign key linking to UserEntity.uid

    @ColumnInfo(name = "item_id")
    @NonNull
    public String itemId; // Room ID or Service ID

    @ColumnInfo(name = "item_name")
    public String itemName;

    @ColumnInfo(name = "booking_type") // "Room" or "Service"
    public String bookingType;

    @ColumnInfo(name = "start_date") // Check-in date or Service date (YYYY-MM-DD)
    public String startDate;

    @ColumnInfo(name = "end_date") // Check-out date (YYYY-MM-DD) or Service time (HH:mm)
    public String endDate;

    @ColumnInfo(name = "total_price")
    public double totalPrice;

    @ColumnInfo(name = "status") // e.g., "Confirmed", "Pending", "Cancelled"
    public String status;

    // Constructor - Room uses this
    public BookingEntity(@NonNull String bookingId, int userId, @NonNull String itemId, String itemName,
                         String bookingType, String startDate, String endDate, double totalPrice, String status) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.bookingType = bookingType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.status = status;
    }
}
