package com.example.luxres;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.luxres.R;
import com.example.luxres.AppDatabase;
import com.example.luxres.UserDao;
import com.example.luxres.UserEntity;
import com.example.luxres.Constants;
import com.example.luxres.PasswordUtils; // Import hashing util

public class ChangePasswordActivity extends AppCompatActivity {

    private static final String TAG = "ChangePasswordActivity";

    // UI Elements
    EditText currentPasswordEditText, newPasswordEditText, confirmNewPasswordEditText;
    Button changePasswordButton;

    // Database and SharedPreferences
    private UserDao userDao;
    private SharedPreferences sharedPreferences;
    private int loggedInUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Initialize UI
        currentPasswordEditText = findViewById(R.id.editTextCurrentPassword);
        newPasswordEditText = findViewById(R.id.editTextNewPassword);
        confirmNewPasswordEditText = findViewById(R.id.editTextConfirmNewPassword);
        changePasswordButton = findViewById(R.id.buttonConfirmChangePassword);

        // Get DB and SharedPreferences instances
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        userDao = db.userDao();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Get logged-in user ID
        loggedInUserId = sharedPreferences.getInt(Constants.PREF_USER_ID, -1);

        if (loggedInUserId == -1) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_LONG).show();
            finish(); // Close this activity
            return;
        }

        changePasswordButton.setOnClickListener(v -> attemptPasswordChange());
    }

    /**
     * Attempts to change the user's password after validation and verification.
     */
    private void attemptPasswordChange() {
        String currentPassword = currentPasswordEditText.getText().toString();
        String newPassword = newPasswordEditText.getText().toString();
        String confirmNewPassword = confirmNewPasswordEditText.getText().toString();

        // --- Input Validation ---
        boolean valid = true;
        if (currentPassword.isEmpty()) {
            currentPasswordEditText.setError("Current password is required");
            valid = false;
        }
        if (newPassword.isEmpty() || newPassword.length() < 6) {
            newPasswordEditText.setError("New password must be at least 6 characters");
            valid = false;
        }
        if (!newPassword.equals(confirmNewPassword)) {
            confirmNewPasswordEditText.setError("Passwords do not match");
            valid = false;
        }
        if (!valid) return; // Stop if validation fails

        // --- Verify Current Password and Update (on background thread) ---
        AppDatabase.databaseWriteExecutor.execute(() -> {
            UserEntity user = userDao.findById(loggedInUserId);
            if (user == null) {
                runOnUiThread(() -> Toast.makeText(ChangePasswordActivity.this, "Error: Could not find user data.", Toast.LENGTH_SHORT).show());
                return;
            }

            // Verify the entered current password against the stored hash
            // WARNING: Uses basic SHA-256 comparison (NOT FOR PRODUCTION)
            if (PasswordUtils.verifyPassword(currentPassword, user.passwordHash)) {
                // Current password is correct, proceed to hash and update new password

                // Hash the new password
                // WARNING: Uses basic SHA-256 without salt (NOT FOR PRODUCTION)
                String newPasswordHash = PasswordUtils.hashPassword(newPassword);
                if (newPasswordHash == null) {
                    runOnUiThread(() -> Toast.makeText(ChangePasswordActivity.this, "Error processing new password.", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Update the user entity with the new hash
                user.passwordHash = newPasswordHash;

                // Update the user in the database
                try {
                    int rowsAffected = userDao.updateUser(user);
                    runOnUiThread(() -> {
                        if (rowsAffected > 0) {
                            Toast.makeText(ChangePasswordActivity.this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                            finish(); // Close activity on success
                        } else {
                            Toast.makeText(ChangePasswordActivity.this, "Failed to update password.", Toast.LENGTH_SHORT).show();
                            Log.w(TAG,"Password update failed, rowsAffected = " + rowsAffected + " for user ID: " + loggedInUserId);
                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(ChangePasswordActivity.this, "An error occurred during update.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error updating password in database", e);
                    });
                }

            } else {
                // Current password verification failed
                runOnUiThread(() -> {
                    currentPasswordEditText.setError("Incorrect current password");
                    Toast.makeText(ChangePasswordActivity.this, "Incorrect current password.", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
