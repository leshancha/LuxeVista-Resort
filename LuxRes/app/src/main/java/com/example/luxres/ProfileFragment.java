package com.example.luxres;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton; // Added
import android.widget.RadioGroup;  // Added
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate; // Import AppCompatDelegate
import androidx.fragment.app.Fragment;
import com.example.luxres.R;
import com.example.luxres.ChangePasswordActivity;
import com.example.luxres.EditProfileActivity;
import com.example.luxres.LoginActivity;
import com.example.luxres.AppDatabase;
import com.example.luxres.UserDao;
import com.example.luxres.UserEntity;
import com.example.luxres.Constants;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    // UI Elements
    TextView profileNameTextView, profileEmailTextView;
    Button updateProfileButton, changePasswordButton, logoutButton;
    RadioGroup themeRadioGroup; // Added
    RadioButton lightRadioButton, darkRadioButton, systemRadioButton; // Added

    // Database and SharedPreferences
    private UserDao userDao;
    private SharedPreferences sharedPreferences;
    private int loggedInUserId = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI
        profileNameTextView = view.findViewById(R.id.textViewProfileName);
        profileEmailTextView = view.findViewById(R.id.textViewProfileEmail);
        updateProfileButton = view.findViewById(R.id.buttonUpdateProfile);
        changePasswordButton = view.findViewById(R.id.buttonChangePassword);
        logoutButton = view.findViewById(R.id.buttonLogout);
        themeRadioGroup = view.findViewById(R.id.radioGroupTheme); // Find RadioGroup
        lightRadioButton = view.findViewById(R.id.radioButtonLight);
        darkRadioButton = view.findViewById(R.id.radioButtonDark);
        systemRadioButton = view.findViewById(R.id.radioButtonSystem);


        // Get DB and SharedPreferences instances
        if (getContext() != null) {
            AppDatabase db = AppDatabase.getDatabase(getContext().getApplicationContext());
            userDao = db.userDao();
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            loggedInUserId = sharedPreferences.getInt(Constants.PREF_USER_ID, -1);
        }

        if (loggedInUserId == -1) {
            handleNotLoggedIn();
            return;
        }

        loadUserProfile(); // Load data initially
        setupThemeSwitcher(); // Setup theme radio buttons

        // Set listeners
        updateProfileButton.setOnClickListener(v -> navigateToEditProfile());
        changePasswordButton.setOnClickListener(v -> navigateToChangePassword());
        logoutButton.setOnClickListener(v -> logoutUser());
    }

    // --- Theme Switcher Setup ---
    private void setupThemeSwitcher() {
        // Get the currently saved mode preference
        int currentMode = sharedPreferences.getInt(Constants.PREF_THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        // Check the correct radio button based on the saved preference
        switch (currentMode) {
            case AppCompatDelegate.MODE_NIGHT_NO: // Light
                lightRadioButton.setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_YES: // Dark
                darkRadioButton.setChecked(true);
                break;
            default: // System default or unspecified
                systemRadioButton.setChecked(true);
                break;
        }

        // Set listener for theme changes
        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int selectedMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM; // Default

            if (checkedId == R.id.radioButtonLight) {
                selectedMode = AppCompatDelegate.MODE_NIGHT_NO;
            } else if (checkedId == R.id.radioButtonDark) {
                selectedMode = AppCompatDelegate.MODE_NIGHT_YES;
            } else if (checkedId == R.id.radioButtonSystem) {
                selectedMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            }

            // Only apply if the mode actually changed
            if (AppCompatDelegate.getDefaultNightMode() != selectedMode) {
                // Apply the selected theme mode
                AppCompatDelegate.setDefaultNightMode(selectedMode);
                // Save the preference
                sharedPreferences.edit().putInt(Constants.PREF_THEME_MODE, selectedMode).apply();
                // Note: The theme change usually requires the Activity to be recreated to take full effect.
                // You might need to restart the activity or prompt the user.
                if (getActivity() != null) {
                    // getActivity().recreate(); // Force activity recreation (can be jarring)
                    Toast.makeText(getContext(), "Theme changed. Restart app for full effect.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // --- Other ProfileFragment methods (loadUserProfile, logoutUser, etc.) ---
    // ... (Keep the existing methods for loadUserProfile, handleNotLoggedIn, navigations, logoutUser) ...

    private void handleNotLoggedIn() {
        // ... (same as before) ...
        Toast.makeText(getContext(), "User session not found. Please log in.", Toast.LENGTH_LONG).show();
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void loadUserProfile() {
        // ... (same as before) ...
        if (userDao == null || loggedInUserId == -1) { return; }
        AppDatabase.databaseWriteExecutor.execute(() -> {
            UserEntity user = userDao.findById(loggedInUserId);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (user != null) {
                        profileNameTextView.setText(user.name);
                        profileEmailTextView.setText(user.email);
                    } else {
                        handleNotLoggedIn();
                    }
                });
            }
        });
    }

    private void navigateToEditProfile() {
        // ... (same as before) ...
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        }
    }

    private void navigateToChangePassword() {
        // ... (same as before) ...
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        }
    }

    private void logoutUser() {
        // ... (same as before) ...
        if (getContext() == null || getActivity() == null) return;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Constants.PREF_USER_ID);
        editor.remove(Constants.PREF_USER_EMAIL);
        editor.remove(Constants.PREF_USER_NAME);
        editor.apply();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

}
