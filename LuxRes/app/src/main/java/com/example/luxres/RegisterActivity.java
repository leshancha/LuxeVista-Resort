package com.example.luxres;

import android.os.Bundle;
import android.util.Log; // For logging errors
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.luxres.R;
import com.example.luxres.AppDatabase; // Import database
import com.example.luxres.UserDao;     // Import DAO
import com.example.luxres.UserEntity;  // Import Entity
import com.example.luxres.PasswordUtils; // Import hashing util

/**
 * Activity for user registration.
 * Handles input validation, password hashing, and saving user details to the Room database.
 */
public class RegisterActivity extends AppCompatActivity {

    // UI Elements
    EditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    Button registerButton;

    // Database Access Object
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI Elements
        nameEditText = findViewById(R.id.editTextName);
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword);
        registerButton = findViewById(R.id.buttonRegister);

        // Get DAO instance from the AppDatabase singleton
        // getApplicationContext() is used to ensure the context lives as long as the application
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        userDao = db.userDao();

        // Set listener for the register button
        registerButton.setOnClickListener(v -> {
            // Retrieve user input
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            // Validate input before proceeding
            if (validateInput(name, email, password, confirmPassword)) {
                // If input is valid, attempt to register the user
                registerUser(name, email, password);
            }
        });
    }

    /**
     * Validates the user registration input fields.
     * Sets errors on EditText fields if validation fails.
     *
     * @param name            User's full name.
     * @param email           User's email address.
     * @param password        User's chosen password.
     * @param confirmPassword Password confirmation field.
     * @return true if all inputs are valid, false otherwise.
     */
    private boolean validateInput(String name, String email, String password, String confirmPassword) {
        // Reset previous errors
        nameEditText.setError(null);
        emailEditText.setError(null);
        passwordEditText.setError(null);
        confirmPasswordEditText.setError(null);

        boolean isValid = true;

        if (name.isEmpty()) {
            nameEditText.setError("Name cannot be empty");
            isValid = false;
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email address");
            isValid = false;
        }
        if (password.isEmpty() || password.length() < 6) { // Example: enforce minimum password length
            passwordEditText.setError("Password must be at least 6 characters");
            isValid = false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            isValid = false;
        }
        return isValid;
    }

    /**
     * Handles the user registration process.
     * Hashes the password and attempts to insert the new user into the database
     * on a background thread. Provides UI feedback based on the result.
     *
     * @param name     User's full name.
     * @param email    User's email address.
     * @param password User's plain text password.
     */
    private void registerUser(String name, String email, String password) {
        // --- IMPORTANT: Hash the password before storing it ---
        // WARNING: PasswordUtils.hashPassword uses basic SHA-256 without salt (NOT FOR PRODUCTION)
        String passwordHash = PasswordUtils.hashPassword(password);
        if (passwordHash == null) {
            // Handle hashing error (should not happen with SHA-256 unless input is null/empty, which validation prevents)
            Toast.makeText(this, "Error processing password.", Toast.LENGTH_SHORT).show();
            Log.e("RegisterActivity", "Password hashing returned null for: " + password);
            return;
        }

        // Create a UserEntity object with the hashed password
        UserEntity newUser = new UserEntity(name, email, passwordHash);

        // --- Perform database insertion on a background thread ---
        // It's crucial not to block the main UI thread with database operations.
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                // Attempt to insert the user. This returns the new row ID, or -1 on conflict (e.g., duplicate email).
                long result = userDao.insertUser(newUser);

                // --- Update UI on the main thread ---
                // Use runOnUiThread to post actions back to the main thread for UI updates.
                runOnUiThread(() -> {
                    if (result != -1) {
                        // Insertion successful
                        Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                        // Close the registration activity and return to the previous one (usually LoginActivity)
                        finish();
                    } else {
                        // Insertion failed - likely due to duplicate email (unique constraint)
                        emailEditText.setError("Email already registered");
                        Toast.makeText(RegisterActivity.this, "Registration failed. Email might already exist.", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                // Catch any other unexpected exceptions during database operation
                runOnUiThread(() -> {
                    Log.e("RegisterActivity", "Error inserting user into database", e);
                    Toast.makeText(RegisterActivity.this, "Registration failed due to an error. Please try again.", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
