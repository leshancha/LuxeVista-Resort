package com.example.luxres;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.luxres.R;
import com.example.luxres.BookingAdapter;
import com.example.luxres.Booking;
import com.example.luxres.Constants; // Import constants

import java.util.ArrayList;
import java.util.Iterator; // Needed for safe removal while iterating
import java.util.List;
import java.util.stream.Collectors; // Alternative for filtering

/**
 * Activity to display the logged-in user's bookings.
 * Includes functionality to view and cancel bookings.
 */
public class MyBookingsActivity extends AppCompatActivity implements BookingAdapter.OnBookingListener {

    private static final String TAG = "MyBookingsActivity";

    RecyclerView bookingsRecyclerView;
    BookingAdapter bookingAdapter;
    List<Booking> userBookingList = new ArrayList<>(); // List to hold only the user's bookings
    TextView emptyBookingsTextView;

    SharedPreferences sharedPreferences;
    private int loggedInUserId = -1; // Store the logged-in user's ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        bookingsRecyclerView = findViewById(R.id.recyclerViewBookings);
        emptyBookingsTextView = findViewById(R.id.textViewEmptyBookings); // Get reference to empty text view
        bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Retrieve the logged-in user's ID (ensure it's stored during login)
        loggedInUserId = sharedPreferences.getInt(Constants.PREF_USER_ID, -1);

        if (loggedInUserId == -1) {
            // User not logged in, handle appropriately
            Toast.makeText(this, "User session not found. Please log in.", Toast.LENGTH_LONG).show();
            // Navigate back to Login
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return; // Stop further execution
        }

        // Load and filter bookings
        loadAndFilterUserBookings();

        // Setup adapter
        bookingAdapter = new BookingAdapter(userBookingList, this);
        bookingsRecyclerView.setAdapter(bookingAdapter);

        // Check if the list is empty and show/hide the message
        checkEmptyView();
    }

    /**
     * Loads placeholder booking data and filters it for the current user.
     * In a real app, this would involve an API call with the user ID.
     */
    private void loadAndFilterUserBookings() {
        // --- Placeholder Data Loading ---
        // In a real app, replace this with an API call: getMyBookings(loggedInUserId)
        List<Booking> allBookings = getPlaceholderAllBookings();

        // --- Filtering Logic ---
        String currentUserIdString = String.valueOf(loggedInUserId); // Assuming user ID in Booking model is String for consistency with placeholder
        userBookingList.clear(); // Clear previous list
        for (Booking booking : allBookings) {
            // Compare the booking's userId with the logged-in user's ID
            if (booking.getUserId().equals(currentUserIdString)) {
                userBookingList.add(booking);
            }
        }
        // Alternative using streams (requires API level 24+)
        // userBookingList = allBookings.stream()
        //         .filter(booking -> booking.getUserId().equals(currentUserIdString))
        //         .collect(Collectors.toList());

        Log.d(TAG, "Loaded " + userBookingList.size() + " bookings for user ID: " + loggedInUserId);
    }

    /**
     * Generates placeholder booking data for demonstration.
     * Includes bookings for different user IDs.
     * @return A list of all placeholder bookings.
     */
    private List<Booking> getPlaceholderAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        // Bookings for User ID 1 (example logged-in user)
        bookings.add(new Booking("b1001", "1", "r101", "Ocean View Suite", "Room", "2025-07-10", "2025-07-15", 2250.00, "Confirmed"));
        bookings.add(new Booking("b1002", "1", "s101", "Relaxing Massage", "Service", "2025-07-11", "14:00", 120.00, "Confirmed"));
        bookings.add(new Booking("b1003", "1", "s103", "Poolside Cabana", "Service", "2025-07-12", "Full Day", 150.00, "Pending"));
        bookings.add(new Booking("b1005", "1", "r102", "Deluxe Room", "Room", "2025-08-01", "2025-08-05", 1000.00, "Cancelled")); // Example cancelled booking

        // Bookings for User ID 2 (another user)
        bookings.add(new Booking("b1004", "2", "r103", "Standard Room", "Room", "2025-07-20", "2025-07-22", 360.00, "Confirmed"));

        return bookings;
    }

    /**
     * Checks if the booking list is empty and updates the visibility
     * of the RecyclerView and the empty message TextView.
     */
    private void checkEmptyView() {
        if (userBookingList.isEmpty()) {
            bookingsRecyclerView.setVisibility(View.GONE);
            emptyBookingsTextView.setVisibility(View.VISIBLE);
        } else {
            bookingsRecyclerView.setVisibility(View.VISIBLE);
            emptyBookingsTextView.setVisibility(View.GONE);
        }
    }


    // --- BookingAdapter.OnBookingListener Implementation ---

    @Override
    public void onBookingClick(int position) {
        if (position >= 0 && position < userBookingList.size()) {
            Booking selectedBooking = userBookingList.get(position);
            // TODO: Implement action on click (e.g., view details)
            Toast.makeText(this, "View details for: " + selectedBooking.getItemName(), Toast.LENGTH_SHORT).show();
            // Example: Open a detail view or show options dialog
            // Intent intent = new Intent(this, BookingDetailActivity.class);
            // intent.putExtra("BOOKING_ID", selectedBooking.getBookingId());
            // startActivity(intent);
        } else {
            Log.e(TAG, "Invalid position clicked in onBookingClick: " + position);
        }
    }

    @Override
    public void onCancelBookingClick(int position) {
        if (position >= 0 && position < userBookingList.size()) {
            Booking bookingToCancel = userBookingList.get(position);

            // Show confirmation dialog before cancelling
            new AlertDialog.Builder(this)
                    .setTitle("Cancel Booking")
                    .setMessage("Are you sure you want to cancel the booking for '" + bookingToCancel.getItemName() + "'?")
                    .setIcon(android.R.drawable.ic_dialog_alert) // Simple alert icon
                    .setPositiveButton("Yes, Cancel", (dialog, whichButton) -> {
                        // --- User confirmed cancellation ---
                        performCancellation(bookingToCancel, position);
                    })
                    .setNegativeButton("No", null) // Do nothing on "No"
                    .show();
        } else {
            Log.e(TAG, "Invalid position clicked in onCancelBookingClick: " + position);
        }
    }

    /**
     * Performs the booking cancellation logic.
     * In a real app, this would involve an API call. Here, we simulate it
     * and update the local list and adapter.
     *
     * @param booking  The booking object to cancel.
     * @param position The position of the booking in the list.
     */
    private void performCancellation(Booking booking, int position) {
        Log.d(TAG, "Attempting to cancel booking ID: " + booking.getBookingId()
                + " of type: " + booking.getBookingType() // Log the type
                + " for item: " + booking.getItemName());

        // --- Simulate API Call ---
        // In a real app: makeRetrofitApiCallToCancel(booking.getBookingId(), loggedInUserId, booking.getBookingType());
        boolean cancellationSuccess = simulateApiCancellation(booking.getBookingId()); // Simulate success/failure

        if (cancellationSuccess) {
            Toast.makeText(this, "Booking for '" + booking.getItemName() + "' cancelled.", Toast.LENGTH_SHORT).show();

            // --- Update UI ---
            userBookingList.remove(position);
            bookingAdapter.notifyItemRemoved(position);
            bookingAdapter.notifyItemRangeChanged(position, userBookingList.size() - position);
            checkEmptyView();

        } else {
            // Cancellation failed (simulated)
            Toast.makeText(this, "Failed to cancel booking. Please try again.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Simulated API cancellation failed for booking ID: " + booking.getBookingId());
            // Re-enable cancel button if needed, as the operation failed
            bookingAdapter.notifyItemChanged(position); // Refresh item to potentially re-enable button
        }
    }

    /**
     * Simulates the result of an API call to cancel a booking.
     * Replace with actual network logic.
     * @param bookingId The ID of the booking to cancel.
     * @return true if simulated cancellation is successful, false otherwise.
     */
    private boolean simulateApiCancellation(String bookingId) {
        // Simulate network delay or potential failure
        try {
            Thread.sleep(50); // Simulate short delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // Simulate a small chance of failure
        return Math.random() > 0.1; // 90% chance of success
    }
}
