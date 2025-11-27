package com.example.luxres; // Use your correct package name

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View; // Import View
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton; // Import ImageButton
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull; // Import NonNull
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager; // Import BiometricManager
import androidx.biometric.BiometricPrompt; // Import BiometricPrompt
import androidx.core.content.ContextCompat; // Import ContextCompat

import java.util.concurrent.Executor; // Import Executor

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity"; // Tag for logging

    // UI Elements
    EditText emailEditText, passwordEditText;
    Button loginButton;
    ImageButton biometricLoginButton; // Added
    TextView registerTextView;

    // Database & SharedPreferences
    private UserDao userDao;
    private SharedPreferences sharedPreferences;

    // Biometric related
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI Elements
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);
        registerTextView = findViewById(R.id.textViewRegister);
        biometricLoginButton = findViewById(R.id.buttonBiometricLogin); // Find the biometric button

        // Get DB and SharedPreferences
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        userDao = db.userDao();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Pre-fill email if remembered
        String lastEmail = sharedPreferences.getString(Constants.PREF_LAST_EMAIL, null);
        if (!TextUtils.isEmpty(lastEmail)) {
            emailEditText.setText(lastEmail);
            passwordEditText.requestFocus();
        }

        // Setup Biometric Prompt
        setupBiometricLogin();

        // Set listeners
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            if (validateInput(email, password)) {
                loginUser(email, password, false); // Pass false for password login
            }
        });

        registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Biometric button listener
        biometricLoginButton.setOnClickListener(v -> {
            Log.d(TAG, "Biometric button clicked");
            // Check again before showing prompt (user might have disabled biometrics)
            if (canAuthenticateWithBiometrics() == BiometricManager.BIOMETRIC_SUCCESS) {
                biometricPrompt.authenticate(promptInfo);
            } else {
                Toast.makeText(this, "Biometric authentication not available.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- Biometric Setup ---
    private void setupBiometricLogin() {
        executor = ContextCompat.getMainExecutor(this); // Get an executor for the main thread

        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                // Handle error, e.g., user cancelled, too many attempts, hardware unavailable
                // Don't show error for user cancellation (ERROR_USER_CANCELED)
                if (errorCode != BiometricPrompt.ERROR_USER_CANCELED && errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    Toast.makeText(getApplicationContext(), "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
                }
                Log.e(TAG, "Biometric Authentication error: " + errorCode + " :: " + errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                // Authentication successful!
                Toast.makeText(getApplicationContext(), "Biometric Authentication succeeded!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Biometric Authentication succeeded!");

                // --- Perform login using remembered user ---
                // Retrieve the email/ID of the user who last logged in successfully with password
                String lastEmail = sharedPreferences.getString(Constants.PREF_LAST_EMAIL, null);
                int lastUserId = sharedPreferences.getInt(Constants.PREF_USER_ID, -1); // Get user ID if stored

                if (!TextUtils.isEmpty(lastEmail) && lastUserId != -1) {
                    Log.d(TAG, "Attempting biometric login for user: " + lastEmail);
                    // We trust the biometric prompt, so we log the user in directly
                    // In a real app, you might use the result to unlock stored credentials/tokens
                    loginUser(lastEmail, null, true); // Pass true for biometric login, password not needed here
                } else {
                    // This shouldn't happen if biometric button is only shown after a successful password login
                    Log.e(TAG, "Biometric success, but no last logged-in user found in SharedPreferences!");
                    Toast.makeText(LoginActivity.this, "Please log in with password first.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                // Authentication failed (e.g., fingerprint not recognized)
                Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Biometric Authentication failed");
            }
        });

        // Configure the Biometric Prompt dialog
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Login for LuxeVista")
                .setSubtitle("Log in using your fingerprint or face")
                .setNegativeButtonText("Use Password") // Allow user to cancel and use password
                // .setConfirmationRequired(false) // Optional: Require explicit confirmation after successful scan
                .build();

        // Check if biometrics can be used and show the button
        if (canAuthenticateWithBiometrics() == BiometricManager.BIOMETRIC_SUCCESS) {
            biometricLoginButton.setVisibility(View.VISIBLE);
            Log.d(TAG, "Biometric authentication available. Button shown.");
        } else {
            biometricLoginButton.setVisibility(View.GONE);
            Log.d(TAG, "Biometric authentication not available. Button hidden.");
        }
    }

    /**
     * Checks if the device supports and has enrolled biometrics.
     * @return BiometricManager constant (BIOMETRIC_SUCCESS, BIOMETRIC_ERROR_NO_HARDWARE, etc.)
     */
    private int canAuthenticateWithBiometrics() {
        BiometricManager biometricManager = BiometricManager.from(this);
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.BIOMETRIC_WEAK);
        // BIOMETRIC_STRONG: Fingerprint, Face (Secure)
        // BIOMETRIC_WEAK: Pattern, PIN, Password (Less secure, but might be needed depending on device capabilities)
        // DEVICE_CREDENTIAL: Allow using device PIN/Pattern/Password as fallback (requires different PromptInfo setup)
    }
    // --- End Biometric Setup ---


    // --- Updated loginUser method ---
    private void loginUser(String email, @Nullable String password, boolean isBiometricLogin) {
        Log.d(TAG, "loginUser called. Email: " + email + ", isBiometric: " + isBiometricLogin);
        AppDatabase.databaseWriteExecutor.execute(() -> {
            UserEntity user = userDao.findByEmail(email);

            runOnUiThread(() -> {
                if (user != null) {
                    boolean loginSuccess = false;
                    if (isBiometricLogin) {
                        // If biometric login, we assume authentication succeeded earlier
                        loginSuccess = true;
                        Log.d(TAG, "Biometric login successful for user ID: " + user.uid);
                    } else if (password != null && PasswordUtils.verifyPassword(password, user.passwordHash)) {
                        // If password login, verify the password
                        loginSuccess = true;
                        Log.d(TAG, "Password verification successful for user ID: " + user.uid);
                    }

                    if (loginSuccess) {
                        // Only show toast on password login for clarity
                        if (!isBiometricLogin) {
                            Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        }

                        // Store user session info and last email used for password login
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(Constants.PREF_USER_ID, user.uid);
                        editor.putString(Constants.PREF_USER_EMAIL, user.email);
                        editor.putString(Constants.PREF_USER_NAME, user.name);
                        // Only save email as "last used" if it was a password login
                        if (!isBiometricLogin) {
                            editor.putString(Constants.PREF_LAST_EMAIL, email);
                        }
                        editor.apply();

                        // Navigate to MainActivity
                        navigateToMain();

                    } else if (!isBiometricLogin) {
                        // Password incorrect (only show error for password attempts)
                        passwordEditText.setError("Incorrect password");
                        Toast.makeText(LoginActivity.this, "Invalid email or password.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // User not found (only show error for password attempts)
                    if (!isBiometricLogin) {
                        emailEditText.setError("Email not registered");
                        Toast.makeText(LoginActivity.this, "Invalid email or password.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Should not happen if lastEmail was valid, but handle defensively
                        Log.e(TAG, "Biometric login failed: User email '" + email + "' not found in DB.");
                        Toast.makeText(LoginActivity.this, "Biometric login failed. User not found.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    // Helper method for navigation
    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // validateInput method remains the same...
    private boolean validateInput(String email, String password) { /* ... */ return true; }

}
