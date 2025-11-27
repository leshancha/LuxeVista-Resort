package com.example.luxres; // Use your correct package name

// --- Ensure ALL necessary imports are present ---
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull; // Import NonNull if needed for onOptionsItemSelected
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;


public class BookingConfirmationActivity extends AppCompatActivity {

    private static final String TAG = "BookingConfirm_DEBUG";

    // UI Elements
    TextView confirmationTitle, itemNameTextView, itemDetailsTextView, priceTextView;
    Button confirmBookingButton;
    Toolbar toolbar;
    LinearLayout roomDateSelectionLayout;
    Button pickCheckInButton, pickCheckOutButton;
    TextView checkInDateTextView, checkOutDateTextView;
    LinearLayout serviceDetailsLayout;

    // Database and SharedPreferences
    private BookingDao bookingDao;
    private SharedPreferences sharedPreferences;
    private int loggedInUserId = -1;

    // Data passed from previous screen
    String bookingType, itemId, itemName;
    double itemPrice;
    String serviceDate, serviceTime;

    // Calendars for Room Date Selection
    private Calendar checkInCalendar = null;
    private Calendar checkOutCalendar = null;

    // Date formatters
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
    private SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());

    boolean dateChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call super first
        Log.i(TAG, "onCreate --- START ---");

        // Initialize formatters (can be done here or as member initializers)
        // dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        // timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());

        try {
            // --- 1. Set Content View (MUST be before findViewById) ---
            setContentView(R.layout.activity_booking_confirmation);
            Log.d(TAG, "setContentView successful.");

            // --- 2. Initialize UI Elements (MOVED HERE, AFTER setContentView) ---
            Log.d(TAG, "Finding UI elements...");
            toolbar = findViewById(R.id.toolbar); // Check ID in XML
            confirmationTitle = findViewById(R.id.textViewConfirmationTitle); // Check ID
            itemNameTextView = findViewById(R.id.textViewConfirmItemName); // Check ID
            itemDetailsTextView = findViewById(R.id.textViewConfirmDetails); // Check ID (for Service details)
            priceTextView = findViewById(R.id.textViewConfirmPrice); // Check ID
            confirmBookingButton = findViewById(R.id.buttonConfirmBooking); // Check ID
            roomDateSelectionLayout = findViewById(R.id.layoutRoomDateSelection); // Check ID
            pickCheckInButton = findViewById(R.id.buttonConfirmPickCheckIn); // Check ID
            pickCheckOutButton = findViewById(R.id.buttonConfirmPickCheckOut); // Check ID
            checkInDateTextView = findViewById(R.id.textViewConfirmCheckInDate); // Check ID
            checkOutDateTextView = findViewById(R.id.textViewConfirmCheckOutDate); // Check ID
            serviceDetailsLayout = findViewById(R.id.layoutServiceDetails); // Check ID
            Log.d(TAG, "UI elements finding finished.");

            // --- 3. Check if any essential view is null (MOVED HERE) ---
            if (confirmationTitle == null || itemNameTextView == null || itemDetailsTextView == null ||
                    priceTextView == null || confirmBookingButton == null || roomDateSelectionLayout == null ||
                    pickCheckInButton == null || pickCheckOutButton == null || checkInDateTextView == null ||
                    checkOutDateTextView == null || serviceDetailsLayout == null) {
                Log.e(TAG, "FATAL: One or more essential UI elements not found! Check layout IDs in activity_booking_confirmation.xml.");
                throw new RuntimeException("Essential UI element missing in layout activity_booking_confirmation.xml");
            }
            Log.d(TAG, "All essential UI elements found.");


            // --- 4. Setup Toolbar ---
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setTitle("Confirm Booking");
                }
            } else { Log.w(TAG, "Toolbar R.id.toolbar not found!"); }


            // --- 5. Get DB, SharedPreferences, UserID ---
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            bookingDao = db.bookingDao();
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            loggedInUserId = sharedPreferences.getInt(Constants.PREF_USER_ID, -1);
            if (loggedInUserId == -1) {
                Log.e(TAG, "User not logged in!");
                Toast.makeText(this, "User session error.", Toast.LENGTH_SHORT).show();
                finish(); return;
            }


            // --- 6. Get Core details from Intent ---
            Intent intent = getIntent();
            if (intent == null) { throw new RuntimeException("Intent is null."); }
            bookingType = intent.getStringExtra("BOOKING_TYPE");
            itemId = intent.getStringExtra("ITEM_ID");
            itemName = intent.getStringExtra("ITEM_NAME");
            itemPrice = intent.getDoubleExtra("ITEM_PRICE", 0.0);
            if (bookingType == null || itemId == null || itemName == null) {
                throw new RuntimeException("Core booking details missing in Intent.");
            }
            Log.d(TAG, "Received Core Data: Type=" + bookingType);


            // --- 7. Populate UI based on Type ---
            confirmationTitle.setText(String.format("Confirm Your %s Booking", bookingType));
            itemNameTextView.setText(itemName);

            if ("Room".equalsIgnoreCase(bookingType)) {
                serviceDetailsLayout.setVisibility(View.GONE);
                roomDateSelectionLayout.setVisibility(View.VISIBLE);
                priceTextView.setText(String.format(Locale.getDefault(),"Price per night: $%.2f", itemPrice));
                initializeDefaultRoomDates();
                updateDateDisplay(true);
                updateDateDisplay(false);
            } else if ("Service".equalsIgnoreCase(bookingType)) {
                roomDateSelectionLayout.setVisibility(View.GONE);
                serviceDetailsLayout.setVisibility(View.VISIBLE);
                serviceDate = intent.getStringExtra("SELECTED_DATE");
                serviceTime = intent.getStringExtra("SELECTED_TIME");
                if (serviceDate != null && serviceTime != null) {
                    itemDetailsTextView.setText(String.format("Date: %s\nTime: %s", serviceDate, serviceTime));
                } else { itemDetailsTextView.setText("Error: Service date/time missing."); }
                if (itemPrice > 0) { priceTextView.setText(String.format(Locale.getDefault(),"Price: $%.2f", itemPrice)); }
                else { priceTextView.setText("Price: Varies / Included"); }
            }
            Log.d(TAG, "UI Population finished.");


            // --- 8. Set Listeners ---
            Log.d(TAG, "Setting listeners...");
            pickCheckInButton.setOnClickListener(v -> showDatePickerDialog(true));
            pickCheckOutButton.setOnClickListener(v -> showDatePickerDialog(false));
            confirmBookingButton.setOnClickListener(v -> { // Ensure this button variable is not null
                Log.i(TAG, ">>> Confirm Booking button CLICKED! <<<");
                saveBookingToDatabase();
            });
            Log.d(TAG, "Listeners set.");


        } catch (Exception e) {
            Log.e(TAG, "--------------------------------------------------");
            Log.e(TAG, "FATAL ERROR during onCreate:", e); // This will print the specific error
            Log.e(TAG, "--------------------------------------------------");
            Toast.makeText(this, "Error displaying confirmation. Check logs.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        Log.i(TAG, "onCreate --- FINISH ---");
    }

    // --- Handle Toolbar back arrow ---
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { // Added NonNull
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Sets default check-in (today) and check-out (tomorrow) dates */
    private void initializeDefaultRoomDates() {
        checkInCalendar = Calendar.getInstance();
        checkInCalendar.set(Calendar.HOUR_OF_DAY, 0); checkInCalendar.set(Calendar.MINUTE, 0); checkInCalendar.set(Calendar.SECOND, 0); checkInCalendar.set(Calendar.MILLISECOND, 0);
        checkOutCalendar = (Calendar) checkInCalendar.clone();
        checkOutCalendar.add(Calendar.DAY_OF_MONTH, 1);
        Log.d(TAG, "Initialized default room dates: IN=" + dbDateFormat.format(checkInCalendar.getTime()) + ", OUT=" + dbDateFormat.format(checkOutCalendar.getTime()));
    }

    /** Shows DatePickerDialog for check-in or check-out */
    private void showDatePickerDialog(boolean isCheckIn) {
        Calendar initialCalendar = isCheckIn ? checkInCalendar : checkOutCalendar;
        if (initialCalendar == null) initialCalendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (datePicker, year, month, day) -> {
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.clear();
                    selectedCalendar.set(year, month, day);
                    if (isCheckIn) {
                        if (checkOutCalendar != null && !selectedCalendar.before(checkOutCalendar)) {
                            Toast.makeText(this, "Check-in must be before check-out.", Toast.LENGTH_SHORT).show(); return;
                        }
                        checkInCalendar = selectedCalendar;
                        updateDateDisplay(true);
                        Log.d(TAG, "Check-in date selected via picker: " + displayDateFormat.format(checkInCalendar.getTime()));
                    } else {
                        if (checkInCalendar == null) {
                            Toast.makeText(this, "Select check-in date first.", Toast.LENGTH_SHORT).show(); return;
                        }
                        if (!selectedCalendar.after(checkInCalendar)) {
                            Toast.makeText(this, "Check-out must be after check-in.", Toast.LENGTH_SHORT).show(); return;
                        }
                        checkOutCalendar = selectedCalendar;
                        updateDateDisplay(false);
                        Log.d(TAG, "Check-out date selected via picker: " + displayDateFormat.format(checkOutCalendar.getTime()));
                    }
                },
                initialCalendar.get(Calendar.YEAR), initialCalendar.get(Calendar.MONTH), initialCalendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - (1000*60*60*12));
        if (!isCheckIn && checkInCalendar != null) {
            long minCheckoutMillis = checkInCalendar.getTimeInMillis() + (24 * 60 * 60 * 1000);
            datePickerDialog.getDatePicker().setMinDate(minCheckoutMillis);
        }
        datePickerDialog.show();
    }

    /** Updates the check-in or check-out date TextView */
    private void updateDateDisplay(boolean isCheckIn) {
        TextView targetTextView = isCheckIn ? checkInDateTextView : checkOutDateTextView;
        Calendar targetCalendar = isCheckIn ? checkInCalendar : checkOutCalendar;
        if (targetTextView != null && targetCalendar != null) {
            targetTextView.setText(displayDateFormat.format(targetCalendar.getTime()));
            Log.d(TAG, "Updated " + (isCheckIn ? "CheckIn" : "CheckOut") + " TextView to: " + targetTextView.getText());
        } else {
            Log.w(TAG, "Cannot update date display - TextView or Calendar is null.");
        }
    }


    /** Saves the booking to the database */
    private void saveBookingToDatabase() {
        Log.i(TAG, "--- saveBookingToDatabase START ---");

        if (bookingDao == null) { Log.e(TAG, "DAO is null!"); Toast.makeText(this, "DB Error", Toast.LENGTH_SHORT).show(); return; }
        if (loggedInUserId == -1) { Log.e(TAG, "User ID is -1!"); Toast.makeText(this, "User Error", Toast.LENGTH_SHORT).show(); return; }

        String newBookingId = UUID.randomUUID().toString();
        String status = "Confirmed";
        String startDateForDb = null;
        String endDateForDb = null;

        Log.d(TAG, "Validating booking type and dates...");
        if ("Room".equalsIgnoreCase(bookingType)) {
            if (checkInCalendar != null && checkOutCalendar != null) {
                if (!checkOutCalendar.after(checkInCalendar)) {
                    Log.e(TAG, "Validation failed: Check-out must be after check-in.");
                    Toast.makeText(this, "Check-out must be after check-in.", Toast.LENGTH_SHORT).show(); return;
                }
                startDateForDb = dbDateFormat.format(checkInCalendar.getTime());
                endDateForDb = dbDateFormat.format(checkOutCalendar.getTime());
                Log.d(TAG, "Using selected room dates for DB: " + startDateForDb + " to " + endDateForDb);
            } else {
                Log.e(TAG, "Cannot save room booking: Dates not selected/valid!");
                Toast.makeText(this, "Please select valid check-in and check-out dates.", Toast.LENGTH_SHORT).show(); return;
            }
        } else if ("Service".equalsIgnoreCase(bookingType)) {
            if (serviceDate != null && serviceTime != null) {
                startDateForDb = serviceDate; endDateForDb = serviceTime;
                Log.d(TAG, "Using service date/time for DB: " + startDateForDb + " at " + endDateForDb);
            } else {
                Log.e(TAG, "Cannot save service booking: Date/Time missing!");
                Toast.makeText(this, "Cannot save booking: Service Date/Time missing.", Toast.LENGTH_SHORT).show(); return;
            }
        } else { Log.e(TAG, "Unknown booking type: " + bookingType); return; }
        Log.d(TAG, "Date validation passed.");

        if (itemId == null || itemName == null) {
            Log.e(TAG, "Cannot save booking: ItemID or ItemName is null.");
            Toast.makeText(this, "Cannot save booking: Missing item information.", Toast.LENGTH_SHORT).show(); return;
        }
        Log.d(TAG, "Other field validation passed.");

        Log.d(TAG, "Creating BookingEntity...");
        BookingEntity newBooking = new BookingEntity( newBookingId, loggedInUserId, itemId, itemName, bookingType, startDateForDb, endDateForDb, itemPrice, status );
        Log.d(TAG, "BookingEntity created with ID: " + newBookingId);

        Log.d(TAG, "Executing database insertion in background...");
        confirmBookingButton.setEnabled(false);
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                Log.d(TAG, "Background task: Calling bookingDao.insertBooking...");
                bookingDao.insertBooking(newBooking);
                Log.i(TAG, "Background task: Booking saved successfully to DB. ID: " + newBookingId);
                runOnUiThread(() -> {
                    try {
                        Log.d(TAG, "Main thread: Updating UI after successful save...");
                        Toast.makeText(this, bookingType + " booked successfully!", Toast.LENGTH_LONG).show();
                        Intent resultIntent = new Intent();
                        setResult(Activity.RESULT_OK, resultIntent);
                        Log.d(TAG, "Main thread: Setting result OK and finishing activity.");
                        finish();
                    } catch (Exception uiEx) { Log.e(TAG, "Error updating UI after save", uiEx); }
                });
            } catch (Exception e) {
                Log.e(TAG, "Background task: Error saving booking to database:", e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Booking failed. Error saving data. Check logs.", Toast.LENGTH_LONG).show();
                    confirmBookingButton.setEnabled(true);
                });
            }
        });
        Log.i(TAG, "--- saveBookingToDatabase FINISH ---");
    }

} // End of Activity Class
