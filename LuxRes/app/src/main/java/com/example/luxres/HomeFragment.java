package com.example.luxres; // Use your correct package name

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView; // Keep TextView if used for Destination
import android.widget.Toast;
// Removed date/calendar related imports

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment_DEBUG";

    // --- UI Elements (Date TextViews Removed) ---
    EditText searchRoomsEditText;
    Button browseServicesButton, browseAttractionsButton, viewAllRoomsButton;
    // Keep textViewDestination if it's still in your layout
    TextView textViewDestination;
    // --- End UI Elements ---

    // --- Removed Calendar variables ---

    // Required empty public constructor
    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated started");

        try {
            // --- Initialize views (Date TextViews Removed) ---
            textViewDestination = view.findViewById(R.id.textViewDestination); // Keep if exists
            searchRoomsEditText = view.findViewById(R.id.editTextSearchRooms);
            browseServicesButton = view.findViewById(R.id.buttonBrowseServices);
            browseAttractionsButton = view.findViewById(R.id.buttonBrowseAttractions);
            viewAllRoomsButton = view.findViewById(R.id.buttonViewAllRooms);

            // --- Check if views were found ---
            if (searchRoomsEditText == null || browseServicesButton == null ||
                    browseAttractionsButton == null || viewAllRoomsButton == null || textViewDestination == null) {
                Log.e(TAG, "One or more views were not found! Check fragment_home.xml IDs.");
                // Optionally show an error message or handle gracefully
                return; // Stop further setup if views are missing
            }
            Log.d(TAG, "All essential views found successfully.");

        } catch (Exception e) {
            Log.e(TAG, "Error finding views in onViewCreated", e);
            return; // Stop if there's an error finding views
        }


        // --- Set listeners (Date listeners removed) ---
        Log.d(TAG, "Setting listeners...");

        searchRoomsEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                Log.d(TAG, ">>> Search action performed on EditText!");
                String searchQuery = searchRoomsEditText.getText().toString().trim();
                hideKeyboard();
                navigateToRoomList(searchQuery); // Navigate with only search query
                return true;
            }
            return false;
        });

        viewAllRoomsButton.setOnClickListener(v -> {
            Log.d(TAG, ">>> viewAllRoomsButton CLICKED!");
            hideKeyboard(); // Hide keyboard if open
            navigateToRoomList(""); // Navigate with empty query
        });

        browseServicesButton.setOnClickListener(v -> {
            Log.d(TAG, ">>> browseServicesButton CLICKED!");
            navigateToServiceList();
        });

        browseAttractionsButton.setOnClickListener(v -> {
            Log.d(TAG, ">>> browseAttractionsButton CLICKED!");
            navigateToAttractions();
        });

        Log.d(TAG, "Listeners set.");
    }

    // --- Removed Date Picker Logic ---
    // private void showDatePickerDialog(boolean isCheckIn) { ... }
    // private void updateDateDisplay(...) { ... }
    // --- End Removed Date Picker Logic ---


    /**
     * Navigates to the RoomListActivity, passing only the search query.
     * @param query The search query entered by the user (can be empty).
     */
    private void navigateToRoomList(String query) {
        // --- Removed Date Validation ---

        Log.d(TAG, "Navigating to RoomListActivity. Query: '" + query + "'");
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), RoomListActivity.class);
            intent.putExtra("SEARCH_QUERY", query); // Only pass search query
            // --- Removed passing date extras ---
            try {
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error starting RoomListActivity", e);
                Toast.makeText(getContext(), "Could not open Room List.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "Cannot navigate to RoomListActivity, getActivity() is null");
            Toast.makeText(getContext(), "Error navigating.", Toast.LENGTH_SHORT).show();
        }
    }

    /** Navigates to the ServiceListActivity */
    private void navigateToServiceList() {
        Log.d(TAG, "Attempting navigation to ServiceListActivity...");
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), ServiceListActivity.class);
            try { startActivity(intent); } catch (Exception e) { Log.e(TAG, "Error starting ServiceListActivity", e); }
        } else { Log.e(TAG, "Cannot navigate to ServiceListActivity, getActivity() is null"); }
    }

    /** Navigates to the AttractionsActivity */
    private void navigateToAttractions() {
        Log.d(TAG, "Attempting navigation to AttractionsActivity...");
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), AttractionsActivity.class);
            try { startActivity(intent); } catch (Exception e) { Log.e(TAG, "Error starting AttractionsActivity", e); }
        } else { Log.e(TAG, "Cannot navigate to AttractionsActivity, getActivity() is null"); }
    }

    /** Helper method to hide the soft keyboard */
    private void hideKeyboard() {
        if (getActivity() != null && getActivity().getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                Log.d(TAG, "Keyboard hidden.");
            }
        }
    }
} // End of Fragment Class
