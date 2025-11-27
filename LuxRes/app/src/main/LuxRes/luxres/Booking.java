package com.example.luxres;

// Represents both Room Bookings and Service Reservations
public class Booking {
    String bookingId;
    String userId;
    String itemId; // Could be Room ID or Service ID
    String itemName; // e.g., "Ocean View Suite" or "Spa Treatment"
    String bookingType; // "Room" or "Service"
    String startDate; // Or check-in date
    String endDate;   // Or check-out date / service time
    double totalPrice;
    String status; // e.g., "Confirmed", "Pending", "Cancelled"

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

    // --- Getters ---
    public String getBookingId() { return bookingId; }
    public String getUserId() { return userId; }
    public String getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public String getBookingType() { return bookingType; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
}