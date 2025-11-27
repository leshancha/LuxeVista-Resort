package com.example.luxres; // Use your correct package name

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.example.luxres.R; // Use your correct R file import
import com.example.luxres.Attraction; // Use your correct model import
import com.google.android.material.appbar.CollapsingToolbarLayout; // Import CollapsingToolbarLayout

public class AttractionDetailActivity extends AppCompatActivity {

    private static final String TAG = "AttractionDetail";
    public static final String EXTRA_ATTRACTION = "attraction_object";

    private ImageView detailImageView;
    private TextView nameTextView, typeTextView, descriptionTextView;
    private Button externalLinkButton;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Attraction currentAttraction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_detail);

        // --- Find Views ---
        toolbar = findViewById(R.id.toolbar_attraction); // Use ID from detail layout
        collapsingToolbarLayout = findViewById(R.id.toolbar_layout_attraction); // Use ID from detail layout
        detailImageView = findViewById(R.id.imageViewAttractionDetail);
        nameTextView = findViewById(R.id.textViewAttractionNameDetail);
        typeTextView = findViewById(R.id.textViewAttractionTypeDetail);
        descriptionTextView = findViewById(R.id.textViewAttractionDescriptionDetail);
        externalLinkButton = findViewById(R.id.buttonAttractionExternalLink);

        // --- Setup Toolbar ---
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // Title will be set later or managed by CollapsingToolbarLayout
            if (collapsingToolbarLayout != null) {
                collapsingToolbarLayout.setTitle(" "); // Set initial blank title
            }
        }

        // --- Get Attraction Data ---
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_ATTRACTION)) {
            currentAttraction = intent.getParcelableExtra(EXTRA_ATTRACTION);
        }

        if (currentAttraction == null) {
            Log.e(TAG, "Attraction data not received!");
            Toast.makeText(this, "Error loading details.", Toast.LENGTH_SHORT).show();
            finish(); // Close if no data
            return;
        }

        // --- Populate UI ---
        populateUi();
    }

    private void populateUi() {
        if (currentAttraction == null) return;

        // Set Text
        nameTextView.setText(currentAttraction.getName());
        typeTextView.setText(currentAttraction.getType());
        descriptionTextView.setText(currentAttraction.getDescription());
        // Set CollapsingToolbar title (optional, shows when collapsed)
        if (collapsingToolbarLayout != null) {
            collapsingToolbarLayout.setTitle(currentAttraction.getName());
        } else if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(currentAttraction.getName()); // Fallback if no collapsing toolbar
        }


        // Load Image
        if (currentAttraction.getImageUrl() != null && !currentAttraction.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(currentAttraction.getImageUrl())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_image_placeholder)
                            .error(R.drawable.ic_error_placeholder)
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(detailImageView);
        } else {
            detailImageView.setImageResource(R.drawable.ic_image_placeholder);
        }

        // Handle External Link Button
        String link = currentAttraction.getExternalLink();
        if (link != null && !link.isEmpty()) {
            externalLinkButton.setVisibility(View.VISIBLE);
            externalLinkButton.setOnClickListener(v -> openExternalLink(link));
        } else {
            externalLinkButton.setVisibility(View.GONE);
        }
    }

    private void openExternalLink(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Could not open external link: " + url, e);
            Toast.makeText(this, "Could not open link", Toast.LENGTH_SHORT).show();
        }
    }


    // Handle Toolbar back arrow click
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close this activity and return to previous one
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
