package com.example.luxres;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnBookRoom, btnReserveService, btnMyBookings, btnAttractions, btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Optional: Setup Toolbar
        // Toolbar toolbar = findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);
        // getSupportActionBar().setTitle("LuxeVista Resort");

        btnBookRoom = findViewById(R.id.buttonBookRoom);
        btnReserveService = findViewById(R.id.buttonReserveService);
        btnMyBookings = findViewById(R.id.buttonMyBookings);
        btnAttractions = findViewById(R.id.buttonAttractions);
        btnProfile = findViewById(R.id.buttonProfile); // Add a profile button

        btnBookRoom.setOnClickListener(v -> startActivity(new Intent(this, RoomListActivity.class)));
        btnReserveService.setOnClickListener(v -> startActivity(new Intent(this, ServiceListActivity.class)));
        btnMyBookings.setOnClickListener(v -> startActivity(new Intent(this, MyBookingsActivity.class)));
        btnAttractions.setOnClickListener(v -> startActivity(new Intent(this, AttractionsActivity.class)));
        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        // TODO: Maybe load some featured offer or welcome message here
    }

    // TODO: Add options menu (e.g., for Logout)
     /*
     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.main_menu, menu); // Create main_menu.xml in res/menu
         return true;
     }

     @Override
     public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         if (item.getItemId() == R.id.action_logout) {
             // TODO: Implement logout logic (clear credentials, navigate to LoginActivity)
             Toast.makeText(this, "Logout Clicked", Toast.LENGTH_SHORT).show();
             startActivity(new Intent(this, LoginActivity.class));
             finishAffinity(); // Close all activities in the task
             return true;
         }
         return super.onOptionsItemSelected(item);
     }
     */
}