package com.example.luxres;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
// TODO: Import Glide or Picasso

public class RoomDetailActivity extends AppCompatActivity {

    ImageView roomImageView;
    TextView roomNameTextView, roomDescriptionTextView, roomPriceTextView, roomAmenitiesTextView;
    Button bookNowButton;
    private Room currentRoom; // Hold the room details

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        roomImageView = findViewById(R.id.imageViewRoomDetail);
        roomNameTextView = findViewById(R.id.textViewRoomNameDetail);
        roomDescriptionTextView = findViewById(R.id.textViewRoomDescriptionDetail);
        roomPriceTextView = findViewById(R.id.textViewRoomPriceDetail);
        roomAmenitiesTextView = findViewById(R.id.textViewRoomAmenities); // Add this TextView in XML
        bookNowButton = findViewById(R.id.buttonBookNow);

        // Retrieve the Room object passed via Intent (ensure Room implements Parcelable)
        // OR retrieve Room ID and fetch details from API
        String roomId = getIntent().getStringExtra("ROOM_ID");
        String roomName = getIntent().getStringExtra("ROOM_NAME"); // Example if only ID passed

        if (roomId != null) {
            // TODO: Fetch full room details from API using roomId
            loadPlaceholderRoomDetails(roomId, roomName); // Replace with API call result
        } else {
            Toast.makeText(this, "Error: Room details not found.", Toast.LENGTH_LONG).show();
            finish(); // Close activity if no ID
            return;
        }


        bookNowButton.setOnClickListener(v -> {
            if (currentRoom != null && currentRoom.isAvailable()) {
                // Navigate to Booking Confirmation or further booking steps
                Intent intent = new Intent(this, BookingConfirmationActivity.class);
                intent.putExtra("BOOKING_TYPE", "Room");
                intent.putExtra("ITEM_ID", currentRoom.getId());
                intent.putExtra("ITEM_NAME", currentRoom.getName());
                intent.putExtra("ITEM_PRICE", currentRoom.getPrice()); // Pass necessary details
                startActivity(intent);
            } else {
                Toast.makeText(this, "This room cannot be booked at the moment.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPlaceholderRoomDetails(String roomId, String roomName) {
        // Simulate loading data - Replace with actual data from API
        currentRoom = new Room(roomId, roomName != null ? roomName : "Deluxe Room", "A beautifully appointed room with excellent views and amenities.", 299.99, "url_placeholder", true);
        // Populate UI
        roomNameTextView.setText(currentRoom.getName());
        roomDescriptionTextView.setText(currentRoom.getDescription());
        roomPriceTextView.setText(String.format("$%.2f / night", currentRoom.getPrice()));
        roomAmenitiesTextView.setText("Amenities: WiFi, AC, TV, Minibar, Balcony"); // Example - fetch this data

        // TODO: Load image using Glide/Picasso
        // Glide.with(this).load(currentRoom.getImageUrl()).into(roomImageView);
        roomImageView.setImageResource(R.drawable.ic_launcher_background); // Placeholder image

        bookNowButton.setEnabled(currentRoom.isAvailable());
    }
}