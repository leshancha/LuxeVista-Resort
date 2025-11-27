package com.example.luxres;

import android.content.Intent;
import android.os.Bundle;
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
        setContentView(R.layout.activity_service_list);

        serviceRecyclerView = findViewById(R.id.recyclerViewServices);
        serviceRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // TODO: Fetch service data from API (can filter by category)
        loadPlaceholderServices(); // Replace with actual API call

        serviceAdapter = new ServiceAdapter(serviceList, this);
        serviceRecyclerView.setAdapter(serviceAdapter);
    }

    private void loadPlaceholderServices() {
        // Replace with actual data fetched from your backend API
        serviceList.add(new Service("s101", "Relaxing Massage", "60-minute full body massage.", 120.00, "Spa", null));
        serviceList.add(new Service("s102", "Fine Dining Reservation", "Book a table at our 'Azure' restaurant.", 0, "Dining", null)); // Price might be N/A here
        serviceList.add(new Service("s103", "Poolside Cabana", "Reserve a private cabana for the day.", 150.00, "Activity", null));
        serviceList.add(new Service("s104", "Guided Beach Tour", "Explore the scenic coastline.", 50.00, "Activity", null));
        // ... add more services
    }

    @Override
    public void onServiceClick(int position) {
        Service selectedService = serviceList.get(position);
        Intent intent = new Intent(this, ServiceDetailActivity.class);
        // Pass service details (e.g., service ID)
        intent.putExtra("SERVICE_ID", selectedService.getId());
        intent.putExtra("SERVICE_NAME", selectedService.getName()); // Example
        startActivity(intent);
    }
}