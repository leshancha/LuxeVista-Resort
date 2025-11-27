package com.example.luxres;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ServiceListActivity extends AppCompatActivity implements ServiceAdapter.OnServiceListener {

    RecyclerView serviceRecyclerView;
    ServiceAdapter serviceAdapter;
    List<Service> serviceList = new ArrayList<>(); // Populate this from API

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate started");

        try {
            setContentView(R.layout.activity_service_list); // Check layout name
            Log.d(TAG, "setContentView successful");

            // Setup Toolbar if present...
            // Toolbar toolbar = findViewById(R.id.toolbar); setSupportActionBar(toolbar); ...

            serviceRecyclerView = findViewById(R.id.recyclerViewServices); // Check ID
            if (serviceRecyclerView == null) {
                throw new RuntimeException("RecyclerView R.id.recyclerViewServices not found!");
            }
            Log.d(TAG, "RecyclerView found.");

            serviceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            Log.d(TAG, "LayoutManager set.");

            // Load placeholder data with URLs
            Log.d(TAG, "Loading placeholder services...");
            loadPlaceholderServices(); // <<< Updated method
            Log.d(TAG, "Placeholder services loaded: " + serviceList.size());


            // Create and set adapter
            Log.d(TAG, "Creating ServiceAdapter...");
            serviceAdapter = new ServiceAdapter(serviceList, this);
            Log.d(TAG, "Setting adapter...");
            serviceRecyclerView.setAdapter(serviceAdapter);
            Log.d(TAG, "Adapter set.");

        } catch (Exception e) {
            Log.e(TAG, "FATAL ERROR during onCreate:", e);
            Toast.makeText(this, "Error loading services. Check logs.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        Log.i(TAG, "onCreate finished");
    }

    private void loadPlaceholderServices() {
        serviceList.clear();
        // Replace with actual URLs (Examples from Pexels)
        serviceList.add(new Service("s101", "Relaxing Massage", "60-minute full body massage.", 120.00, "Spa",
                "https://images.pexels.com/photos/3757955/pexels-photo-3757955.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1")); // Spa image
        serviceList.add(new Service("s102", "Fine Dining Reservation", "Book a table at our 'Azure' restaurant.", 0, "Dining",
                "https://images.pexels.com/photos/67468/pexels-photo-67468.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1")); // Dining image
        serviceList.add(new Service("s103", "Poolside Cabana", "Reserve a private cabana for the day.", 150.00, "Activity",
                "https://images.pexels.com/photos/221457/pexels-photo-221457.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1")); // Poolside image
        serviceList.add(new Service("s104", "Guided Beach Tour", "Explore the scenic coastline.", 50.00, "Activity",
                "https://images.pexels.com/photos/1007657/pexels-photo-1007657.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1")); // Beach tour image
        // ... add more services
    }

    @Override
    public void onServiceClick(int position) {
        Log.d(TAG, "onServiceClick: position " + position);
        if (position >= 0 && position < serviceList.size()) {
            Service selectedService = serviceList.get(position);
            Intent intent = new Intent(this, ServiceDetailActivity.class);
            intent.putExtra("SERVICE_ID", selectedService.getId());
            intent.putExtra("SERVICE_NAME", selectedService.getName());
            startActivity(intent);
        } else {
            Log.w(TAG, "Invalid position clicked: " + position);
        }
    }
}