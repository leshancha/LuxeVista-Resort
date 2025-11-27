package com.example.luxres;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class AttractionsActivity extends AppCompatActivity implements AttractionAdapter.OnAttractionListener {

    RecyclerView attractionsRecyclerView;
    AttractionAdapter attractionAdapter;
    List<Attraction> attractionList = new ArrayList<>(); // Populate from API

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attractions);

        attractionsRecyclerView = findViewById(R.id.recyclerViewAttractions);
        attractionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // TODO: Fetch attractions/offers data from API
        loadPlaceholderAttractions(); // Replace with API call

        attractionAdapter = new AttractionAdapter(attractionList, this);
        attractionsRecyclerView.setAdapter(attractionAdapter);
    }

    private void loadPlaceholderAttractions() {
        // Replace with actual data fetched from API
        attractionList.add(new Attraction("a101", "Summer Spa Package", "Get 20% off on couple massages.", "url_spa", "Offer", null));
        attractionList.add(new Attraction("a102", "Nearby Beach Club", "Visit the famous 'Sunset Beach Club'.", "url_beach", "Nearby Attraction", "geo:0,0?q=Sunset Beach Club")); // Example map link
        attractionList.add(new Attraction("a103", "Weekend Jazz Night", "Live jazz music at the hotel bar every Friday.", "url_jazz", "Hotel Event", null));
        attractionList.add(new Attraction("a104", "Local Market Tour", "Explore the vibrant local market.", "url_market", "Nearby Attraction", "https://example.com/market-info")); // Example web link
        // ... add more attractions/offers
    }

    @Override
    public void onAttractionClick(int position) {
        Attraction selectedAttraction = attractionList.get(position);
        String link = selectedAttraction.getExternalLink();

        if (link != null && !link.isEmpty()) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Could not open link", Toast.LENGTH_SHORT).show();
            }
        } else {
            // No external link, just show details or do nothing
            Toast.makeText(this, "More details about: " + selectedAttraction.getName(), Toast.LENGTH_SHORT).show();
            // Could open a detail dialog/activity if needed
        }
    }
}