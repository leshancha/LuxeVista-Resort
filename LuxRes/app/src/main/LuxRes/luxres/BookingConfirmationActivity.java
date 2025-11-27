package com.example.luxres;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class BookingConfirmationActivity extends AppCompatActivity {

    TextView confirmationTitle, itemNameTextView, itemDetailsTextView, priceTextView;
    Button confirmBookingButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);

        confirmationTitle = findViewById(R.id.textViewConfirmationTitle);
        itemNameTextView = findViewById(R.id.textViewConfirmItemName);
        itemDetailsTextView = findViewById(R.id.textViewConfirmDetails); // For dates/times
        priceTextView = findViewById(R.id.textViewConfirmPrice);
        confirmBookingButton = findViewById(R.id.buttonConfirmBooking);

        // Get details from Intent
        String bookingType = getIntent().getStringExtra("BOOKING_TYPE"); // "Room" or "Service"
        String itemId = getIntent().getStringExtra("ITEM_ID");
        String itemName = getIntent().getStringExtra("ITEM_NAME");
        double itemPrice = getIntent().getDoubleExtra("ITEM_PRICE", 0.0);
        // Additional details (dates/times)
        String selectedDate = getIntent().getStringExtra("SELECTED_DATE"); // For services
        String selectedTime = getIntent().getStringExtra("SELECTED_TIME"); // For services
        // TODO: Add check-in/check-out dates for rooms if needed

        if (bookingType == null || itemId == null || itemName == null) {
            Toast.makeText(this, "Error: Booking details missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        confirmationTitle.setText(String.format("Confirm Your %s Booking", bookingType));
        itemNameTextView.setText(itemName);

        String details = "";
        if ("Room".equals(bookingType)) {
            // TODO: Fetch or pass check-in/check-out dates
            details = "Check-in: [Date] - Check-out: [Date]"; // Placeholder
            priceTextView.setText(String.format(Locale.getDefault(),"Total Price: $%.2f", itemPrice)); // Price per night might need calculation for total
        } else if ("Service".equals(bookingType)) {
            details = String.format("Date: %s\nTime: %s", selectedDate, selectedTime);
            if (itemPrice > 0) {
                priceTextView.setText(String.format(Locale.getDefault(),"Price: $%.2f", itemPrice));
            } else {
                priceTextView.setText("Price: Varies / Included");
            }
        }
        itemDetailsTextView.setText(details);


        confirmBookingButton.setOnClickListener(v -> {
            // TODO: Implement the final booking API call here
            // On success:
            Toast.makeText(this, bookingType + " booked successfully! (Placeholder)", Toast.LENGTH_LONG).show();
            // Navigate to My Bookings or Main Activity
            Intent intent = new Intent(this, MyBookingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear back stack
            startActivity(intent);
            finish(); // Close confirmation screen

            // On failure:
            // Toast.makeText(this, "Booking failed. Please try again.", Toast.LENGTH_SHORT).show();
        });
    }
}