package com.example.luxres; // Use your correct package name

// ... (Keep all other imports: Intent, Uri, Bundle, Log, MenuItem, Toast, AppCompatActivity, Toolbar, RecyclerView, LinearLayoutManager) ...
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AttractionsActivity extends AppCompatActivity implements AttractionAdapter.OnAttractionListener {

    private static final String TAG = "AttractionsActivity";

    RecyclerView attractionsRecyclerView;
    AttractionAdapter attractionAdapter;
    List<Attraction> attractionList = new ArrayList<>();
    Toolbar toolbar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ... (onCreate logic remains the same - setup Toolbar, RecyclerView, load data, set adapter) ...
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_attractions);
            toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) { setSupportActionBar(toolbar); if (getSupportActionBar() != null) { getSupportActionBar().setDisplayHomeAsUpEnabled(true); getSupportActionBar().setTitle("Offers & Attractions"); } }
            attractionsRecyclerView = findViewById(R.id.recyclerViewAttractions);
            if (attractionsRecyclerView == null) { throw new RuntimeException("RecyclerView not found!"); }
            attractionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            loadPlaceholderAttractions();
            // Use the corrected AttractionAdapter from previous step
            attractionAdapter = new AttractionAdapter(attractionList, this);
            attractionsRecyclerView.setAdapter(attractionAdapter);
        } catch (Exception e) { Log.e(TAG, "FATAL ERROR during onCreate:", e); finish(); }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadPlaceholderAttractions() {
        // ... (loadPlaceholderAttractions method remains the same) ...
        attractionList.clear();
        attractionList.add(new Attraction("a101", "Summer Spa Package", "Get 20% off on couple massages.", "https://images.pexels.com/photos/3768916/pexels-photo-3768916.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1", "Offer", null));
        attractionList.add(new Attraction("a102", "Nearby Beach Club", "Visit the famous 'Sunset Beach Club'.", "https://images.pexels.com/photos/1032650/pexels-photo-1032650.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1", "Nearby Attraction", "geo:0,0?q=Sunset Beach Club"));
        attractionList.add(new Attraction("a103", "Weekend Jazz Night", "Live jazz music at the hotel bar every Friday.", "https://images.pexels.com/photos/167491/pexels-photo-167491.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1", "Hotel Event", null));
        attractionList.add(new Attraction("a104", "Local Market Tour", "Explore the vibrant local market.", "https://images.pexels.com/photos/2253643/pexels-photo-2253643.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1", "Nearby Attraction", null));
    }

    // --- Updated onAttractionClick ---
    @Override
    public void onAttractionClick(int position) {
        Log.d(TAG, "onAttractionClick: position " + position);
        if (position >= 0 && position < attractionList.size()) {
            Attraction selectedAttraction = attractionList.get(position);
            if (selectedAttraction == null) {
                Log.e(TAG, "selectedAttraction is null at position: " + position);
                return;
            }

            String link = selectedAttraction.getExternalLink();
            // --- Check for external link first ---
            if (link != null && !link.isEmpty()) {
                // Try to open the external link (map, website, etc.)
                try {
                    Log.d(TAG, "Attempting to open external link: " + link);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "Could not open link: " + link, e);
                    Toast.makeText(this, "Could not open link", Toast.LENGTH_SHORT).show();
                }
            } else {
                // --- If no external link, open Detail Activity ---
                Log.d(TAG, "No external link, opening AttractionDetailActivity for: " + selectedAttraction.getName());
                Intent detailIntent = new Intent(this, AttractionDetailActivity.class);
                // Ensure Attraction model implements Parcelable
                detailIntent.putExtra(AttractionDetailActivity.EXTRA_ATTRACTION, selectedAttraction);
                try {
                    startActivity(detailIntent);
                    Log.d(TAG,"Successfully started AttractionDetailActivity");
                } catch (Exception e) {
                    Log.e(TAG, "Error starting AttractionDetailActivity", e);
                    Toast.makeText(this, "Could not open details.", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Log.w(TAG, "Invalid position clicked: " + position);
        }
    }
    // --- End Update ---
}
