package com.example.luxres;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.luxres.R;
import com.example.luxres.HomeFragment;
import com.example.luxres.MyBookingsFragment;
import com.example.luxres.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavView = findViewById(R.id.bottom_navigation_view);

        // Set listener for bottom navigation item selection
        bottomNavView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.navigation_bookings) {
                selectedFragment = new MyBookingsFragment();
            } else if (itemId == R.id.navigation_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                // Replace the current fragment with the selected one
                loadFragment(selectedFragment);
                return true; // Indicate item selection was handled
            }
            return false; // Item selection not handled
        });

        // Load the default fragment (HomeFragment) when the activity is first created
        if (savedInstanceState == null) {
            bottomNavView.setSelectedItemId(R.id.navigation_home); // Set Home as default
            loadFragment(new HomeFragment());
        }
    }

    /**
     * Replaces the content of the fragment container with the given fragment.
     * @param fragment The fragment to display.
     */
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back.
        transaction.replace(R.id.nav_host_fragment_container, fragment);
        // transaction.addToBackStack(null); // Optional: Add transactions to back stack
        transaction.commit();
    }

    // Optional: Handle back press to potentially navigate bottom nav or exit
     /* @Override
     public void onBackPressed() {
         if (bottomNavView.getSelectedItemId() == R.id.navigation_home) {
             super.onBackPressed(); // Exit app if on home
         } else {
             bottomNavView.setSelectedItemId(R.id.navigation_home); // Go to home otherwise
         }
     }*/
}
