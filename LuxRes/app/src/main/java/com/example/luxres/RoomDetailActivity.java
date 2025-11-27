package com.example.luxres; // Use your correct package name

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.Locale;

public class RoomDetailActivity extends AppCompatActivity {

    private static final String TAG = "RoomDetail_DEBUG";

    // UI Elements
    ImageView roomImageView;
    TextView roomNameTextView, roomDescriptionTextView, roomPriceTextView, roomAmenitiesTextView;
    Button bookNowButton;
    Toolbar toolbar;
    private Room currentRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate --- START ---");

        try {
            setContentView(R.layout.activity_room_detail);
            Log.d(TAG, "setContentView successful.");

            // --- Setup Toolbar ---
            toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setTitle("");
                }
            }

            // --- Find Views ---
            Log.d(TAG, "Finding views...");
            roomImageView = findViewById(R.id.imageViewRoomDetail);
            roomNameTextView = findViewById(R.id.textViewRoomNameDetail);
            roomDescriptionTextView = findViewById(R.id.textViewRoomDescriptionDetail);
            roomPriceTextView = findViewById(R.id.textViewRoomPriceDetail);
            roomAmenitiesTextView = findViewById(R.id.textViewRoomAmenities);
            bookNowButton = findViewById(R.id.buttonBookNow); // *** Check this ID ***
            Log.d(TAG, "Finished finding views.");

            // --- Check Views ---
            if (roomImageView == null || roomNameTextView == null || bookNowButton == null /* || check others */) {
                throw new RuntimeException("Essential UI element missing in layout activity_room_detail.xml");
            }
            Log.d(TAG, "Essential views found.");


            // --- Retrieve Room Data ---
            Log.d(TAG, "Retrieving room data from Intent...");
            Intent receivedIntent = getIntent();
            if (receivedIntent != null && receivedIntent.hasExtra("ROOM_OBJECT")) {
                currentRoom = receivedIntent.getParcelableExtra("ROOM_OBJECT");
                if (currentRoom != null) {
                    Log.i(TAG, "Successfully received Room Object: ID=" + currentRoom.getId() + ", Name=" + currentRoom.getName() + ", Available=" + currentRoom.isAvailable());
                    populateUi(); // Populate UI now
                } else { throw new RuntimeException("Null Room Object received."); }
            } else { throw new RuntimeException("Room data 'ROOM_OBJECT' missing in Intent."); }


            // --- Set Book Now Button Listener ---
            Log.d(TAG, "Setting OnClickListener for bookNowButton");
            bookNowButton.setOnClickListener(v -> {
                Log.d(TAG, ">>> bookNowButton CLICKED!");

                // 1. Check if currentRoom is valid
                if (currentRoom == null) {
                    Log.e(TAG, "Cannot book, currentRoom is null!");
                    Toast.makeText(this, "Error: Room data missing.", Toast.LENGTH_SHORT).show();
                    return; // Stop execution
                }
                Log.d(TAG, "currentRoom is not null (ID: " + currentRoom.getId() + ")");

                // 2. Check if room is available
                Log.d(TAG, "Checking availability: " + currentRoom.isAvailable());
                if (currentRoom.isAvailable()) {
                    Log.i(TAG, "Room is available, navigating to BookingConfirmationActivity...");
                    Intent intent = new Intent(this, BookingConfirmationActivity.class);
                    // --- Pass necessary data ---
                    intent.putExtra("BOOKING_TYPE", "Room"); // Indicate it's a room booking
                    intent.putExtra("ITEM_ID", currentRoom.getId());
                    intent.putExtra("ITEM_NAME", currentRoom.getName());
                    intent.putExtra("ITEM_PRICE", currentRoom.getPrice());
                    // --- Dates are NOT passed from here anymore ---

                    try {
                        // 3. Attempt to start activity
                        startActivity(intent);
                        Log.d(TAG, "startActivity(BookingConfirmationActivity) called successfully.");
                    } catch (Exception e) {
                        // 4. Catch potential errors starting the activity (e.g., not declared in Manifest)
                        Log.e(TAG, "Error starting BookingConfirmationActivity", e);
                        Toast.makeText(this, "Could not proceed to booking confirmation.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 5. Handle case where room is not available
                    Log.w(TAG, "Booking button clicked, but room is not available.");
                    Toast.makeText(this, "This room is currently unavailable.", Toast.LENGTH_SHORT).show();
                }
            }); // End of OnClickListener


        } catch (Exception e) {
            Log.e(TAG, "FATAL ERROR during onCreate:", e);
            Toast.makeText(this, "Error loading room details. Check logs.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        Log.i(TAG, "onCreate --- FINISH ---");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Populates UI elements with data from currentRoom */
    private void populateUi() {
        // ... (populateUi method remains the same as previous version, using Glide) ...
        Log.d(TAG, "populateUi - START");
        if (currentRoom == null || roomNameTextView == null /* || check other views */) { return; }
        try {
            String name = currentRoom.getName() != null ? currentRoom.getName() : "Room Details";
            roomNameTextView.setText(name);
            if (getSupportActionBar() != null) { getSupportActionBar().setTitle(name); }
            roomDescriptionTextView.setText(currentRoom.getDescription() != null ? currentRoom.getDescription() : "N/A");
            roomPriceTextView.setText(String.format(Locale.getDefault(), "$%.2f / night", currentRoom.getPrice()));
            roomAmenitiesTextView.setText("Amenities: WiFi, AC, TV, Minibar"); // Example
            String imageUrl = currentRoom.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty() && roomImageView != null) {
                Glide.with(this).load(imageUrl).apply(new RequestOptions().placeholder(R.drawable.ic_image_placeholder).error(R.drawable.ic_error_placeholder).diskCacheStrategy(DiskCacheStrategy.ALL)).into(roomImageView);
            } else { if(roomImageView != null) roomImageView.setImageResource(R.drawable.ic_image_placeholder); }
            boolean isAvailable = currentRoom.isAvailable();
            if(bookNowButton != null) { bookNowButton.setEnabled(isAvailable); bookNowButton.setAlpha(isAvailable ? 1.0f : 0.5f); }
        } catch (Exception e) { Log.e(TAG, "Error during populateUi", e); }
        Log.d(TAG, "populateUi - FINISH");
    }

} // End of Activity Class
