package com.example.luxres;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MyBookingsActivity extends AppCompatActivity implements BookingAdapter.OnBookingListener {

    RecyclerView bookingsRecyclerView;
    BookingAdapter bookingAdapter;
    List<Booking> bookingList = new ArrayList<>(); // Populate from API

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        bookingsRecyclerView = findViewById(R.id.recyclerViewBookings);
        bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // TODO: Fetch user's bookings (rooms and services) from API
        loadPlaceholderBookings(); // Replace with API call

        bookingAdapter = new BookingAdapter(bookingList, this);
        bookingsRecyclerView.setAdapter(bookingAdapter);
    }

    private void loadPlaceholderBookings() {
        // Replace with actual data fetched from API for the logged-in user
        bookingList.add(new Booking("b1001", "u123", "r101", "Ocean View Suite", "Room", "2025-07-10", "2025-07-15", 2250.00, "Confirmed"));
        bookingList.add(new Booking("b1002", "u123", "s101", "Relaxing Massage", "Service", "2025-07-11", "14:00", 120.00, "Confirmed"));
        bookingList.add(new Booking("b1003", "u123", "s103", "Poolside Cabana", "Service", "2025-07-12", "Full Day", 150.00, "Pending"));
        // ... add more bookings
    }

    @Override
    public void onBookingClick(int position) {
        Booking selectedBooking = bookingList.get(position);
        // TODO: Implement action on click (e.g., view details, modify, cancel - requires API support)
        Toast.makeText(this, "View details for: " + selectedBooking.getItemName(), Toast.LENGTH_SHORT).show();
        // Example: Open a detail view or show options dialog
    }

    @Override
    public void onCancelBookingClick(int position) {
        Booking bookingToCancel = bookingList.get(position);
        // TODO: Show confirmation dialog
        // TODO: Implement API call to cancel the booking
        Toast.makeText(this, "Cancel request for: " + bookingToCancel.getItemName() + " (Not Implemented)", Toast.LENGTH_SHORT).show();
        // On success, remove from list and notify adapter:
        // bookingList.remove(position);
        // bookingAdapter.notifyItemRemoved(position);
        // bookingAdapter.notifyItemRangeChanged(position, bookingList.size());
    }
}