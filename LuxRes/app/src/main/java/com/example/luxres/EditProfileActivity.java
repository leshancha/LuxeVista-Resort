package com.example.luxres;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.luxres.R;
import com.example.luxres.AppDatabase;
import com.example.luxres.UserDao;
import com.example.luxres.UserEntity;
import com.example.luxres.Constants;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";

    // UI Elements
    TextView emailTextView;
    EditText nameEditText;
    Button saveChangesButton;

    // Database and SharedPreferences
    private UserDao userDao;
    private SharedPreferences sharedPreferences;
    private int loggedInUserId = -1;
    private UserEntity currentUser; // To hold the user data being edited

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize UI
        emailTextView = findViewById(R.id.textViewEditEmail);
        nameEditText = findViewById(R.id.editTextEditName);
        saveChangesButton = findViewById(R.id.buttonSaveChanges);

        // Get DB and SharedPreferences instances
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        userDao = db.userDao();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Get logged-in user ID
        loggedInUserId = sharedPreferences.getInt(Constants.PREF_USER_ID, -1);

        if (loggedInUserId == -1) {
            // Should not happen if ProfileActivity checks login status, but handle defensively
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_LONG).show();
            finish(); // Close this activity
            return;
        }

        // Load current user data
        loadUserData();

        // Set Save button listener
        saveChangesButton.setOnClickListener(v -> saveProfileChanges());
    }

    /**
     * Loads the current user's data from the database into the UI fields.
     */
    private void loadUserData() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            currentUser = userDao.findById(loggedInUserId);
            runOnUiThread(() -> {
                if (currentUser != null) {
                    emailTextView.setText(currentUser.email);
                    nameEditText.setText(currentUser.name);
                } else {
                    Toast.makeText(EditProfileActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Could not find user with ID: " + loggedInUserId);
                    finish(); // Close if data can't be loaded
                }
            });
        });
    }

    /**
     * Validates input and saves the updated profile information to the database.
     */
    private void saveProfileChanges() {
        String newName = nameEditText.getText().toString().trim();

        if (newName.isEmpty()) {
            nameEditText.setError("Name cannot be empty");
            return;
        }

        if (currentUser == null) {
            Toast.makeText(this, "Error: User data not loaded.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update the UserEntity object
        currentUser.name = newName;

        // Save changes to DB on background thread
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                int rowsAffected = userDao.updateUser(currentUser);
                runOnUiThread(() -> {
                    if (rowsAffected > 0) {
                        Toast.makeText(EditProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        // Also update SharedPreferences if name is stored there
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Constants.PREF_USER_NAME, newName);
                        editor.apply();
                        finish(); // Close activity and return to ProfileActivity
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Update failed, rowsAffected = " + rowsAffected + " for user ID: " + loggedInUserId);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(EditProfileActivity.this, "An error occurred while updating.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error updating user in database", e);
                });
            }
        });
    }
}
