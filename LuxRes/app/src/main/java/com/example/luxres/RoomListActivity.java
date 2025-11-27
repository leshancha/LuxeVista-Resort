package com.example.luxres;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View; // Import View
import android.widget.TextView; // Import TextView
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale; // For case-insensitive comparison
import java.util.stream.Collectors; // Optional for filtering

public class RoomListActivity extends AppCompatActivity implements RoomAdapter.OnRoomListener {

    private static final String TAG = "RoomListActivity_DEBUG";

    RecyclerView roomRecyclerView;
    RoomAdapter roomAdapter;
    List<Room> fullRoomList = new ArrayList<>(); // Holds ALL rooms
    List<Room> filteredRoomList = new ArrayList<>(); // Holds filtered rooms for display
    TextView noResultsTextView; // TextView for empty state
    TextView titleTextView; // TextView for the title
    Toolbar toolbar;

    private long checkInDateMillis = -1;
    private long checkOutDateMillis = -1;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate --- START ---");

        Intent intent = getIntent(); // Get the intent that started this activity
        String searchQuery = intent.getStringExtra("SEARCH_QUERY");
        checkInDateMillis = intent.getLongExtra(Constants.EXTRA_CHECK_IN_DATE, -1); // Get check-in date
        checkOutDateMillis = intent.getLongExtra(Constants.EXTRA_CHECK_OUT_DATE, -1); // Get check-out date

        if (searchQuery == null) { searchQuery = ""; }
        Log.i(TAG, "Received search query: '" + searchQuery + "'");
        Log.i(TAG, "Received Check-in: " + checkInDateMillis + ", Check-out: " + checkOutDateMillis);

        // --- Check if dates are valid ---
        if (checkInDateMillis == -1 || checkOutDateMillis == -1) {
            Log.e(TAG, "FATAL: Check-in or Check-out date missing from Intent!");
            Toast.makeText(this, "Date information missing.", Toast.LENGTH_LONG).show();
            // finish(); // Optionally finish if dates are essential here
            // return;
        }

        try {
            setContentView(R.layout.activity_room_list);
            Log.i(TAG, "setContentView successful.");

            // --- Setup Toolbar (Optional) ---
            toolbar = findViewById(R.id.toolbar); // Assumes R.id.toolbar exists
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    // Title set later by updateTitle()
                }
            }

            // --- Find Views ---
            roomRecyclerView = findViewById(R.id.recyclerViewRooms);
            noResultsTextView = findViewById(R.id.textViewNoResults);
            titleTextView = findViewById(R.id.textViewListTitle);
            if (roomRecyclerView == null || noResultsTextView == null || titleTextView == null) {
                throw new RuntimeException("Essential view missing in layout activity_room_list.xml");
            }
            Log.d(TAG, "Essential views found.");

            roomRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            Log.d(TAG, "LayoutManager set.");

            // --- Get Search Query ---
            searchQuery = getIntent().getStringExtra("SEARCH_QUERY");
            if (searchQuery == null) { searchQuery = ""; }
            Log.i(TAG, "Received search query: '" + searchQuery + "'");

            // --- Load ALL rooms initially ---
            loadAllPlaceholderRooms(); // <<< This method now includes updated URLs
            Log.d(TAG, "Total rooms loaded: " + fullRoomList.size());

            // --- Filter rooms based on query ---
            filterRooms(searchQuery);
            Log.d(TAG, "Filtered rooms count: " + filteredRoomList.size());

            // --- Setup Adapter with the FILTERED list ---
            roomAdapter = new RoomAdapter(filteredRoomList, this); // Pass filtered list and listener
            roomRecyclerView.setAdapter(roomAdapter);
            Log.d(TAG, "Adapter set successfully.");

            // --- Update Title and Empty View Visibility ---
            updateTitle(searchQuery);
            checkEmptyView();

        } catch (Exception e) {
            Log.e(TAG, "FATAL ERROR during onCreate:", e);
            Toast.makeText(this, "Error loading room list. Check logs.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        Log.i(TAG, "onCreate --- FINISH ---");
    }

    public void onRoomClick(int position) {
        Log.d(TAG, "onRoomClick: position " + position);
        if (position >= 0 && position < filteredRoomList.size()) {
            Room selectedRoom = filteredRoomList.get(position);
            if (selectedRoom.isAvailable()) {
                Intent intent = new Intent(this, RoomDetailActivity.class);
                intent.putExtra("ROOM_OBJECT", selectedRoom); // Pass Room object
                // --- Pass dates along to RoomDetailActivity ---
                intent.putExtra(Constants.EXTRA_CHECK_IN_DATE, checkInDateMillis);
                intent.putExtra(Constants.EXTRA_CHECK_OUT_DATE, checkOutDateMillis);
                // --- End Pass Dates ---
                startActivity(intent);
            } else {
                Toast.makeText(this, "This room is currently unavailable", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.w(TAG, "Invalid position clicked: " + position);
        }
    }


    // Handle Toolbar back arrow click
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Loads the full list of placeholder rooms.
     * In a real app, this would fetch all rooms from an API.
     */
    private void loadAllPlaceholderRooms() {
        Log.d(TAG, "loadAllPlaceholderRooms - START");
        fullRoomList.clear();
        // Image URLs from Pexels (royalty-free) - replace if needed
        fullRoomList.add(new Room("r101", "Ocean View Suite", "Stunning ocean views, king bed, private balcony.", 450.00,
                "https://images.pexels.com/photos/189296/pexels-photo-189296.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1", // Ocean view focus
                true));
        fullRoomList.add(new Room("r102", "Deluxe Garden Room", "Spacious room with garden view, queen bed.", 250.00,
                "https://images.pexels.com/photos/271643/pexels-photo-271643.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1", // Room with green view
                true));
        fullRoomList.add(new Room("r103", "Standard City Room", "Comfortable room, city view, double bed.", 180.00,
                "https://images.pexels.com/photos/276724/pexels-photo-276724.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1", // Standard modern room
                false)); // Example unavailable room
        fullRoomList.add(new Room("r104", "Family Suite", "Two bedrooms, suitable for families, garden access.", 350.00,
                "https://images.pexels.com/photos/1457847/pexels-photo-1457847.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1", // Larger living area
                true));
        fullRoomList.add(new Room("r105", "Penthouse Suite", "Top floor luxury, panoramic ocean view, private jacuzzi.", 800.00,
                "https://images.pexels.com/photos/3201761/pexels-photo-3201761.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1", // Luxurious interior
                true));
        fullRoomList.add(new Room("r106", "Basic Double Room", "Simple and affordable double bed room.", 150.00,
                "https://images.pexels.com/photos/164595/pexels-photo-164595.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1", // Simple clean room
                true));
        Log.d(TAG, "loadAllPlaceholderRooms - FINISH");
    }

    /**
     * Filters the fullRoomList based on the search query and updates filteredRoomList.
     * @param query The search term entered by the user.
     */
    private void filterRooms(String query) {
        Log.d(TAG, "Filtering rooms with query: '" + query + "'");
        filteredRoomList.clear();
        if (query == null || query.trim().isEmpty()) {
            // Show all rooms if query is empty
            filteredRoomList.addAll(fullRoomList);
            // Optional: Only show available
            // filteredRoomList.addAll(fullRoomList.stream().filter(Room::isAvailable).collect(Collectors.toList()));
        } else {
            String lowerCaseQuery = query.toLowerCase(Locale.getDefault());
            for (Room room : fullRoomList) {
                if ((room.getName() != null && room.getName().toLowerCase(Locale.getDefault()).contains(lowerCaseQuery)) ||
                        (room.getDescription() != null && room.getDescription().toLowerCase(Locale.getDefault()).contains(lowerCaseQuery))) {
                    filteredRoomList.add(room);
                }
            }
        }
        Log.d(TAG, "Filtering complete. Found rooms: " + filteredRoomList.size());
        if (roomAdapter != null) {
            // This might be called before adapter is set initially, hence the null check
            roomAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Updates the title based on the search query.
     * @param query The search query used.
     */
    private void updateTitle(String query) {
        String title;
        if (query == null || query.trim().isEmpty()) {
            title = "Available Rooms";
        } else {
            title = "Rooms matching '" + query + "'";
        }
        if(titleTextView != null) titleTextView.setText(title);
        if (getSupportActionBar() != null) { getSupportActionBar().setTitle(title); }
    }

    /**
     * Shows or hides the 'No Results' message based on the filtered list size.
     */
    private void checkEmptyView() {
        if (noResultsTextView == null || roomRecyclerView == null) return; // Views might not be ready
        if (filteredRoomList.isEmpty()) {
            roomRecyclerView.setVisibility(View.GONE);
            noResultsTextView.setVisibility(View.VISIBLE);
            Log.d(TAG,"No results found, showing empty view.");
        } else {
            roomRecyclerView.setVisibility(View.VISIBLE);
            noResultsTextView.setVisibility(View.GONE);
            Log.d(TAG,"Results found, showing RecyclerView.");
        }
    }

    // --- RoomAdapter.OnRoomListener Implementation ---

}
