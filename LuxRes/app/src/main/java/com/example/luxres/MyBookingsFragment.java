package com.example.luxres; // Use your correct package name

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyBookingsFragment extends Fragment implements BookingAdapter.OnBookingListener {

    private static final String TAG = "MyBookingsFragment_DEBUG"; // Unique Tag

    RecyclerView bookingsRecyclerView;
    BookingAdapter bookingAdapter;
    // Start with an empty list, data will be loaded
    List<Booking> userBookingList = new ArrayList<>();
    TextView emptyBookingsTextView;

    SharedPreferences sharedPreferences;
    int loggedInUserId = -1;

    private BookingDao bookingDao;

    // Activity Result Launcher for ManageBookingActivity
    private ActivityResultLauncher<Intent> manageBookingLauncher;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");
        // Initialize DAO here as context is guaranteed
        try {
            AppDatabase db = AppDatabase.getDatabase(context.getApplicationContext());
            bookingDao = db.bookingDao();
            Log.d(TAG, "BookingDao initialized in onAttach.");
        } catch (Exception e) {
            Log.e(TAG, "Error getting database in onAttach", e);
            // Handle error - perhaps show a message later or prevent functionality
        }
    }

    // --- Activity Result Handling ---
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        // --- REMOVED DAO initialization from here ---
        // if (getContext() != null) { ... }

        // Initialize Activity Result Launcher
        manageBookingLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // ... (Result handling remains the same) ...
                    Log.d(TAG, "Activity Result Received: " + result.getResultCode());
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            boolean updated = data.getBooleanExtra(ManageBookingActivity.RESULT_BOOKING_UPDATED, false);
                            boolean cancelled = data.getBooleanExtra(ManageBookingActivity.RESULT_BOOKING_CANCELLED, false);
                            if (updated || cancelled) {
                                Log.i(TAG, "Refreshing booking list due to update/cancellation.");
                                refreshBookings();
                            }
                        }
                    }
                });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_my_bookings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");

        bookingsRecyclerView = view.findViewById(R.id.recyclerViewBookings);
        emptyBookingsTextView = view.findViewById(R.id.textViewEmptyBookings);
        bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getContext() != null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            loggedInUserId = sharedPreferences.getInt(Constants.PREF_USER_ID, -1);
        }

        // --- Check DAO and User ID again ---
        if (loggedInUserId == -1 || bookingDao == null) {
            Log.e(TAG, "User not logged in or DAO not initialized. Cannot proceed.");
            if (bookingDao == null) {
                // Attempt to initialize DAO again if it failed in onAttach
                if(getContext() != null) {
                    AppDatabase db = AppDatabase.getDatabase(getContext().getApplicationContext());
                    bookingDao = db.bookingDao();
                    Log.w(TAG, "DAO initialized in onViewCreated as fallback.");
                }
            }
            // If still null or user not logged in, redirect
            if (loggedInUserId == -1 || bookingDao == null) {
                handleNotLoggedIn();
                return;
            }
        }

        // Setup adapter with the (initially empty) list
        bookingAdapter = new BookingAdapter(userBookingList, this);
        bookingsRecyclerView.setAdapter(bookingAdapter);

        // Load initial data
        refreshBookings();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        // Refresh data when the fragment becomes visible
        if (loggedInUserId != -1) {
            refreshBookings();
        } else {
            handleNotLoggedIn();
        }
    }

    // --- Refreshes the booking list from the source (simulated) ---
    @SuppressLint("NotifyDataSetChanged")
    private void refreshBookings() {
        Log.d(TAG, "Refreshing bookings for user ID: " + loggedInUserId);
        if (bookingDao == null || loggedInUserId == -1) {
            Log.e(TAG, "Cannot refresh bookings, DAO or UserID invalid.");
            return;
        }

        // Perform DB query on background thread
        AppDatabase.databaseWriteExecutor.execute(() -> {
            // Fetch BookingEntity objects from DB
            List<BookingEntity> bookingEntities = bookingDao.getBookingsForUser(loggedInUserId);
            Log.d(TAG, "Loaded " + bookingEntities.size() + " booking entities from DB.");

            // Map BookingEntity to Booking model (used by Adapter)
            List<Booking> newBookingList = mapEntitiesToModels(bookingEntities);

            // Update UI on the main thread
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    userBookingList.clear();
                    userBookingList.addAll(newBookingList);

                    if (bookingAdapter != null) {
                        bookingAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Adapter notified of data change.");
                    } else {
                        Log.w(TAG, "Adapter is null during refresh UI update.");
                    }
                    checkEmptyView(); // Update empty view visibility
                });
            }
        });
    }

    private List<Booking> mapEntitiesToModels(List<BookingEntity> entities) {
        List<Booking> models = new ArrayList<>();
        for (BookingEntity entity : entities) {
            models.add(new Booking(
                    entity.bookingId,
                    String.valueOf(entity.userId), // Convert int userId back to String if needed by model
                    entity.itemId,
                    entity.itemName,
                    entity.bookingType,
                    entity.startDate,
                    entity.endDate,
                    entity.totalPrice,
                    entity.status
            ));
        }
        // Alternative using streams (API 24+)
        /*
        return entities.stream()
                 .map(entity -> new Booking(entity.bookingId, String.valueOf(entity.userId), entity.itemId, entity.itemName,
                                            entity.bookingType, entity.startDate, entity.endDate, entity.totalPrice, entity.status))
                 .collect(Collectors.toList());
        */
        return models;
    }


    // --- BookingAdapter.OnBookingListener Implementation ---
    @Override
    public void onBookingClick(int position) {
        // ... (same as previous version, uses manageBookingLauncher) ...
        if (getContext() == null || getActivity() == null || position < 0 || position >= userBookingList.size()) return;
        Booking selectedBooking = userBookingList.get(position);
        if (selectedBooking == null) return;
        Log.d(TAG, "onBookingClick: Sending Booking ID=" + selectedBooking.getBookingId());
        Intent intent = new Intent(getActivity(), ManageBookingActivity.class);
        intent.putExtra(ManageBookingActivity.EXTRA_BOOKING, selectedBooking);
        manageBookingLauncher.launch(intent);
    }

    // --- Updated onCancelBookingClick ---
    @Override
    public void onCancelBookingClick(int position) {
        if (getContext() == null || position < 0 || position >= userBookingList.size()) {
            Log.e(TAG,"onCancelBookingClick: Invalid context or position: " + position);
            return;
        }
        Booking bookingToCancel = userBookingList.get(position);
        if (bookingToCancel == null) {
            Log.e(TAG,"onCancelBookingClick: Booking object is null at position: " + position);
            return;
        }
        final String bookingIdToCancel = bookingToCancel.getBookingId();
        final String bookingNameToCancel = bookingToCancel.getItemName(); // For dialog message

        new AlertDialog.Builder(getContext())
                .setTitle("Cancel Booking")
                .setMessage("Are you sure you want to cancel the booking for '" + bookingNameToCancel + "'?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes, Cancel", (dialog, whichButton) -> {
                    // --- User confirmed cancellation ---
                    performCancellation(bookingIdToCancel, position); // Pass ID and position
                })
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Simulates cancelling the booking via an API call and updates the list on success.
     */
    private void performCancellation(String bookingId, int adapterPosition) {
        Log.d(TAG, "Attempting to cancel booking ID: " + bookingId + " from database.");
        if (bookingDao == null) {
            Log.e(TAG, "Cannot cancel booking, DAO is null.");
            Toast.makeText(getContext(), "Error: Cannot access booking data.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Perform DB delete on background thread
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                // --- Delete from Database ---
                int rowsAffected = bookingDao.deleteBookingById(bookingId);
                // --- OR Update Status ---
                // int rowsAffected = bookingDao.updateBookingStatus(bookingId, "Cancelled");

                Log.d(TAG, "DB operation finished. Rows affected: " + rowsAffected);

                // Update UI on main thread
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (rowsAffected > 0) {
                            Toast.makeText(getContext(), "Booking cancelled successfully.", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "Booking cancelled successfully in DB for ID: " + bookingId);
                            // Remove from the local list and notify adapter
                            // Find item again in case list changed
                            int currentPosition = -1;
                            for(int i=0; i < userBookingList.size(); i++){
                                if(userBookingList.get(i).getBookingId().equals(bookingId)){
                                    currentPosition = i;
                                    break;
                                }
                            }
                            if (currentPosition != -1) {
                                userBookingList.remove(currentPosition);
                                if (bookingAdapter != null) {
                                    bookingAdapter.notifyItemRemoved(currentPosition);
                                    bookingAdapter.notifyItemRangeChanged(currentPosition, userBookingList.size() - currentPosition);
                                }
                            } else {
                                Log.w(TAG, "Item not found in list after DB delete, refreshing full list.");
                                refreshBookings(); // Fallback refresh
                            }
                            checkEmptyView();
                        } else {
                            Toast.makeText(getContext(), "Failed to cancel booking. Please try again.", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Failed to cancel/update booking in DB for ID: " + bookingId + ". Rows affected: " + rowsAffected);
                            // Optional: Notify adapter to redraw item if needed
                            if (bookingAdapter != null) bookingAdapter.notifyItemChanged(adapterPosition);
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error cancelling booking in database for ID: " + bookingId, e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "An error occurred during cancellation.", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }


    // --- Other methods ---
    private void handleNotLoggedIn() {
        Toast.makeText(getContext(), "User session not found. Please log in.", Toast.LENGTH_LONG).show();
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void checkEmptyView() {
        if (getView() == null || emptyBookingsTextView == null || bookingsRecyclerView == null) return;
        if (userBookingList.isEmpty()) {
            bookingsRecyclerView.setVisibility(View.GONE);
            emptyBookingsTextView.setVisibility(View.VISIBLE);
            Log.d(TAG,"No bookings found, showing empty view.");
        } else {
            bookingsRecyclerView.setVisibility(View.VISIBLE);
            emptyBookingsTextView.setVisibility(View.GONE);
            Log.d(TAG,"Bookings found, showing RecyclerView.");
        }
    }

} // End of Fragment Class
