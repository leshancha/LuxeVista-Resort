package com.example.luxres;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
// Import User model if needed

public class ProfileActivity extends AppCompatActivity {

    TextView profileNameTextView, profileEmailTextView;
    Button updateProfileButton, changePasswordButton, viewBookingHistoryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileNameTextView = findViewById(R.id.textViewProfileName);
        profileEmailTextView = findViewById(R.id.textViewProfileEmail);
        updateProfileButton = findViewById(R.id.buttonUpdateProfile);
        changePasswordButton = findViewById(R.id.buttonChangePassword);
        viewBookingHistoryButton = findViewById(R.id.buttonViewBookingHistory); // Link to MyBookings

        // TODO: Load user data (from SharedPreferences, API, or passed Intent)
        loadPlaceholderProfileData();

        updateProfileButton.setOnClickListener(v -> {
            // TODO: Navigate to an EditProfileActivity or show a dialog
            Toast.makeText(this, "Update Profile Clicked (Not Implemented)", Toast.LENGTH_SHORT).show();
        });

        changePasswordButton.setOnClickListener(v -> {
            // TODO: Navigate to a ChangePasswordActivity or show a dialog
            Toast.makeText(this, "Change Password Clicked (Not Implemented)", Toast.LENGTH_SHORT).show();
        });

        viewBookingHistoryButton.setOnClickListener(v -> {
            // MyBookingsActivity shows current/upcoming, could be reused or a separate history screen needed
            startActivity(new Intent(this, MyBookingsActivity.class));
        });
    }

    private void loadPlaceholderProfileData() {
        // Replace with actual data loading
        profileNameTextView.setText("Guest User"); // Placeholder
        profileEmailTextView.setText("guest@example.com"); // Placeholder
        // Fetch User object and populate fields
    }
}