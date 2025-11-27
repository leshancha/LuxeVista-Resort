package com.example.luxres;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull; // Use androidx annotation

public class Booking implements Parcelable {
    String bookingId;
    String userId;
    String itemId;
    String itemName;
    String bookingType;
    String startDate;
    String endDate;
    double totalPrice;
    String status;

    // Constructor
    public Booking(String bookingId, String userId, String itemId, String itemName, String bookingType, String startDate, String endDate, double totalPrice, String status) {
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

    // Getters... (keep all getters)
    public String getBookingId() { return bookingId; }
    public String getUserId() { return userId; }
    public String getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public String getBookingType() { return bookingType; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }

    // Setters... (keep setters)
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public void setStatus(String status) { this.status = status; }


    // --- Parcelable Implementation ---
    protected Booking(Parcel in) {
        bookingId = in.readString();
        userId = in.readString();
        itemId = in.readString();
        itemName = in.readString();
        bookingType = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        totalPrice = in.readDouble();
        status = in.readString();
    }

    public static final Creator<Booking> CREATOR = new Creator<Booking>() {
        @Override
        public Booking createFromParcel(Parcel in) {
            return new Booking(in);
        }

        @Override
        public Booking[] newArray(int size) {
            return new Booking[size];
        }
    };

    @Override
    public int describeContents() {
        return 0; // Usually return 0
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(bookingId);
        dest.writeString(userId);
        dest.writeString(itemId);
        dest.writeString(itemName);
        dest.writeString(bookingType);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeDouble(totalPrice);
        dest.writeString(status);
    }
    // --- End Parcelable ---
}
