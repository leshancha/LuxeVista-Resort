package com.example.luxres;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager; // Use androidx.preference.PreferenceManager if targeting newer APIs
import android.util.Log; // For logging errors
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.luxres.R;
import com.example.luxres.AppDatabase; // Import database
import com.example.luxres.UserDao;     // Import DAO
import com.example.luxres.UserEntity;  // Import Entity
import com.example.luxres.Constants; // Import constants for SharedPreferences keys

/**
 * Activity to display the logged-in user's profile information.
 * Retrieves user data from the Room database based on the ID stored in SharedPreferences.
 */
public class ProfileActivity extends AppCompatActivity {

    // UI Elements
    TextView profileNameTextView, profileEmailTextView;
    Button updateProfileButton, changePasswordButton, viewBookingHistoryButton;

    // Database Access Object
    private UserDao userDao;
    // SharedPreferences for retrieving login state
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize UI Elements
        profileNameTextView = findViewById(R.id.textViewProfileName);
        profileEmailTextView = findViewById(R.id.textViewProfileEmail);
        updateProfileButton = findViewById(R.id.buttonUpdateProfile);
        changePasswordButton = findViewById(R.id.buttonChangePassword);
        viewBookingHistoryButton = findViewById(R.id.buttonViewBookingHistory);

        // Get DAO instance
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        userDao = db.userDao();

        // Initialize SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Load the user's profile data from the database
        loadUserProfile();

        // --- Set Button Listeners (Placeholders for further implementation) ---
        updateProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        changePasswordButton.setOnClickListener(v -> {
            // Navigate to ChangePasswordActivity
            Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        viewBookingHistoryButton.setOnClickListener(v -> {
            // Navigate to the activity displaying the user's bookings
            startActivity(new Intent(this, MyBookingsActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload profile data in case it was changed
        loadUserProfile();
    }

    /**
     * Loads the user profile information from the database.
     * Retrieves the logged-in user's ID from SharedPreferences and fetches
     * the corresponding user details from the Room database on a background thread.
     * Updates the UI TextViews with the retrieved name and email.
     */
    private void loadUserProfile() {
        // Retrieve the logged-in user's ID stored during login
        // Use -1 or another invalid ID as the default value if the key is not found
        int loggedInUserId = sharedPreferences.getInt(Constants.PREF_USER_ID, -1);

        if (loggedInUserId != -1) {
            // User ID found in SharedPreferences, proceed to fetch data from DB

            // --- Fetch user details from DB on background thread ---
            AppDatabase.databaseWriteExecutor.execute(() -> {
                // Query the database for the user by their unique ID
                UserEntity user = userDao.findById(loggedInUserId);

                // --- Update UI on the main thread with the fetched data ---
                runOnUiThread(() -> {
                    if (user != null) {
                        // User found, update the TextViews
                        profileNameTextView.setText(user.name);
                        profileEmailTextView.setText(user.email);
                    } else {
                        // This case should ideally not happen if the ID stored in SharedPreferences is valid,
                        // but handle it defensively (e.g., user deleted from DB while logged in?).
                        Log.e("ProfileActivity", "User not found in DB for ID: " + loggedInUserId);
                        Toast.makeText(ProfileActivity.this, "Error loading profile data.", Toast.LENGTH_SHORT).show();
                        // Consider logging the user out or navigating back to login
                        // clearLoginSessionAndNavigateToLogin();
                    }
                });
            });
        } else {
            // No valid user ID found in SharedPreferences - user is not logged in properly.
            Toast.makeText(this, "User session not found. Please log in again.", Toast.LENGTH_LONG).show();
            Log.e("ProfileActivity", "No valid user ID found in SharedPreferences.");
            // Navigate back to LoginActivity and clear the task stack
            clearLoginSessionAndNavigateToLogin();
        }
    }

    /**
     * Helper method to clear login data from SharedPreferences and navigate to LoginActivity.
     */
    private void clearLoginSessionAndNavigateToLogin() {
        // Clear stored user data from SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Constants.PREF_USER_ID);
        editor.remove(Constants.PREF_USER_EMAIL);
        editor.remove(Constants.PREF_USER_NAME);
        editor.apply();

        // Navigate back to LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Close the ProfileActivity
    }
}
