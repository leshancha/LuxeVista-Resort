package com.example.luxres;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class RoomListActivity extends AppCompatActivity implements RoomAdapter.OnRoomListener {

    RecyclerView roomRecyclerView;
    RoomAdapter roomAdapter;
    List<Room> roomList = new ArrayList<>(); // Populate this from API

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        roomRecyclerView = findViewById(R.id.recyclerViewRooms);
        roomRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // TODO: Fetch room data from API
        loadPlaceholderRooms(); // Replace with actual API call

        roomAdapter = new RoomAdapter(roomList, this);
        roomRecyclerView.setAdapter(roomAdapter);

        // TODO: Implement filtering/sorting controls (e.g., Spinners, Buttons)
    }

    private void loadPlaceholderRooms() {
        // Replace with actual data fetched from your backend API
        roomList.add(new Room("r101", "Ocean View Suite", "Stunning ocean views, king bed, private balcony.", 450.00, "url_to_image1", true));
        roomList.add(new Room("r102", "Deluxe Room", "Spacious room with garden view, queen bed.", 250.00, "url_to_image2", true));
        roomList.add(new Room("r103", "Standard Room", "Comfortable room, city view, double bed.", 180.00, "url_to_image3", false)); // Example unavailable
        // ... add more rooms
    }

    @Override
    public void onRoomClick(int position) {
        Room selectedRoom = roomList.get(position);
        if (selectedRoom.isAvailable()) {
            Intent intent = new Intent(this, RoomDetailActivity.class);
            // Pass room details (e.g., room ID or the whole object if Parcelable)
            intent.putExtra("ROOM_ID", selectedRoom.getId());
            intent.putExtra("ROOM_NAME", selectedRoom.getName()); // Example passing data
            startActivity(intent);
        } else {
            Toast.makeText(this, "This room is currently unavailable", Toast.LENGTH_SHORT).show();
        }
    }
}