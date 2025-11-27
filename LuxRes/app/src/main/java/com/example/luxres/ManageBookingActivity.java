package com.example.luxres;

// ... other imports ...
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Import Log
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

        import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
// ... other imports ...
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class ManageBookingActivity extends AppCompatActivity {

    private static final String TAG = "ManageBooking_DEBUG";
    public static final String EXTRA_BOOKING = "booking_object";

    public static final String RESULT_BOOKING_UPDATED = "booking_updated";
    public static final String RESULT_BOOKING_CANCELLED = "booking_cancelled";
    public static final String RESULT_BOOKING_ID = "booking_id";

    // UI Elements
    TextView itemNameTextView, bookingIdTextView, currentDatesLabelTextView, currentDatesTextView;
    TextView selectedStartDateTextView, selectedEndDateTextView;
    Button pickStartDateButton, pickEndDateButton, saveChangesButton, cancelBookingButton;
    Toolbar toolbar;

    // Data
    Booking currentBooking;
    Calendar newStartDateCalendar;
    Calendar newEndDateCalendar;
    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
    boolean dateChanged = false; // Flag to track if user made changes


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Call super first
        Log.i(TAG, "onCreate --- START ---");

        // Initialize formatters here
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());

        try {
            // --- 1. Set Content View (MUST be before findViewById) ---
            Log.d(TAG, "Setting content view: R.layout.activity_manage_booking");
            setContentView(R.layout.activity_manage_booking);
            Log.d(TAG, "setContentView successful.");

            // --- 2. Initialize UI Elements (MOVED HERE) ---
            Log.d(TAG, "Finding UI elements...");
            toolbar = findViewById(R.id.toolbarManageBooking);
            itemNameTextView = findViewById(R.id.textViewManageItemName);
            bookingIdTextView = findViewById(R.id.textViewManageBookingId);
            currentDatesLabelTextView = findViewById(R.id.textViewManageCurrentDatesLabel);
            currentDatesTextView = findViewById(R.id.textViewManageCurrentDates);
            selectedStartDateTextView = findViewById(R.id.textViewSelectedStartDate);
            selectedEndDateTextView = findViewById(R.id.textViewSelectedEndDate);
            pickStartDateButton = findViewById(R.id.buttonPickStartDate);
            pickEndDateButton = findViewById(R.id.buttonPickEndDate);
            saveChangesButton = findViewById(R.id.buttonSaveChangesBooking);
            cancelBookingButton = findViewById(R.id.buttonCancelBookingManage);
            Log.d(TAG, "UI elements finding finished.");

            // --- 3. Check if any essential view is null (MOVED HERE) ---
            if (itemNameTextView == null || bookingIdTextView == null || currentDatesTextView == null ||
                    selectedStartDateTextView == null || selectedEndDateTextView == null ||
                    pickStartDateButton == null || pickEndDateButton == null || saveChangesButton == null || cancelBookingButton == null || currentDatesLabelTextView == null) {
                Log.e(TAG, "FATAL: One or more essential UI elements not found! Check layout IDs in activity_manage_booking.xml.");
                throw new RuntimeException("Essential UI element missing in layout activity_manage_booking.xml");
            }
            Log.d(TAG, "All essential UI elements found.");

            // --- 4. Get Booking Data from Intent ---
            Log.d(TAG, "Checking for Intent extra: " + EXTRA_BOOKING);
            if (getIntent() == null || !getIntent().hasExtra(EXTRA_BOOKING)) {
                throw new RuntimeException("Intent or Booking extra missing!");
            }
            currentBooking = getIntent().getParcelableExtra(EXTRA_BOOKING);
            if (currentBooking == null) {
                throw new RuntimeException("getParcelableExtra returned null!");
            }
            Log.i(TAG, "Received Booking: ID=" + currentBooking.getBookingId());

            // --- 5. Initialize Calendars ---
            Log.d(TAG, "Initializing Calendars...");
            initializeCalendars();
            Log.d(TAG, "Calendars initialized.");

            // --- 6. Setup Toolbar ---
            Log.d(TAG, "Setting up Toolbar...");
            if(toolbar != null) {
                setSupportActionBar(toolbar);
                if(getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setTitle("Manage Booking");
                    Log.d(TAG, "Toolbar setup complete.");
                } else { Log.e(TAG, "getSupportActionBar is null!"); }
            } else { Log.w(TAG, "Toolbar R.id.toolbarManageBooking not found!"); }


            // --- 7. Populate UI ---
            Log.d(TAG, "Calling populateInitialData...");
            populateInitialData();
            Log.d(TAG, "populateInitialData finished.");

            // --- 8. Set Listeners ---
            Log.d(TAG, "Setting listeners...");
            pickStartDateButton.setOnClickListener(v -> showDatePicker(true));
            pickEndDateButton.setOnClickListener(v -> {
                if (currentBooking == null) return;
                if ("Service".equalsIgnoreCase(currentBooking.getBookingType())) { showTimePicker(); }
                else { showDatePicker(false); }
            });
            saveChangesButton.setOnClickListener(v -> saveChanges());
            cancelBookingButton.setOnClickListener(v -> showCancelConfirmation());
            Log.d(TAG, "Listeners set.");

        } catch (Exception e) {
            Log.e(TAG, "--------------------------------------------------");
            Log.e(TAG, "FATAL ERROR during onCreate:", e);
            Log.e(TAG, "--------------------------------------------------");
            Toast.makeText(this, "Error loading booking details. Check logs.", Toast.LENGTH_LONG).show();
            finish(); // Close the crashing activity
            return; // Stop further execution in onCreate
        }
        Log.i(TAG, "onCreate --- FINISH ---");
    }

    // onOptionsItemSelected remains the same...
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // initializeCalendars remains the same...
    /** Initializes Calendar objects based on the current booking's dates/times */
    private void initializeCalendars() {
        Log.d(TAG, "initializeCalendars - START");
        newStartDateCalendar = Calendar.getInstance();
        newEndDateCalendar = Calendar.getInstance();
        if (currentBooking == null) { Log.e(TAG, "Cannot initialize calendars, currentBooking is null!"); return; }
        try {
            String startDateString = currentBooking.getStartDate();
            if (startDateString == null) throw new NullPointerException("Start date string is null");
            Date startDate = dateFormatter.parse(startDateString);
            if (startDate != null) newStartDateCalendar.setTime(startDate);
        } catch (Exception e) { Log.e(TAG, "Error parsing start date: '" + currentBooking.getStartDate() + "'", e); }
        try {
            String endDateString = currentBooking.getEndDate();
            if (endDateString == null) throw new NullPointerException("End date/time string is null");
            if ("Service".equalsIgnoreCase(currentBooking.getBookingType())) {
                Date serviceTime = timeFormatter.parse(endDateString);
                if (serviceTime != null) {
                    Calendar timeCal = Calendar.getInstance();
                    timeCal.setTime(serviceTime);
                    newStartDateCalendar.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
                    newStartDateCalendar.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
                    newEndDateCalendar.setTime(newStartDateCalendar.getTime());
                }
            } else {
                Date endDate = dateFormatter.parse(endDateString);
                if (endDate != null) newEndDateCalendar.setTime(endDate);
            }
        } catch (Exception e) { Log.e(TAG, "Error parsing end date/time: '" + currentBooking.getEndDate() + "'", e); }
        Log.d(TAG, "initializeCalendars - FINISH");
    }



    /** Populates the UI fields with the current booking details */
    private void populateInitialData() {
        Log.d(TAG, "populateInitialData - START");
        if (currentBooking == null || itemNameTextView == null || bookingIdTextView == null || currentDatesTextView == null /* || other views == null */) {
            Log.e(TAG, "Cannot populate UI, booking or essential views are null!");
            if(itemNameTextView != null) itemNameTextView.setText(R.string.error_loading_data);
            return;
        }
        try {
            String itemName = (currentBooking.getItemName() != null && !currentBooking.getItemName().isEmpty()) ? currentBooking.getItemName() : "Unknown Item";
            String bookingId = (currentBooking.getBookingId() != null && !currentBooking.getBookingId().isEmpty()) ? currentBooking.getBookingId() : "N/A";
            String startDateStrRaw = (currentBooking.getStartDate() != null && !currentBooking.getStartDate().isEmpty()) ? currentBooking.getStartDate() : "N/A";
            String endDateStrRaw = (currentBooking.getEndDate() != null && !currentBooking.getEndDate().isEmpty()) ? currentBooking.getEndDate() : "N/A";
            String bookingType = currentBooking.getBookingType();

            itemNameTextView.setText(itemName);
            bookingIdTextView.setText("Booking ID: " + bookingId);

            String startDateStr = "Start: " + startDateStrRaw;
            String endDateStr = "End: " + endDateStrRaw;
            String currentDatesDisplay;
            String labelText;

            if ("Service".equalsIgnoreCase(bookingType)) {
                labelText = "Current Booking Date/Time:";
                currentDatesDisplay = startDateStr + " at " + endDateStrRaw;
                pickStartDateButton.setText("Change Service Date");
                pickEndDateButton.setText("Change Service Time");
            } else {
                labelText = "Current Booking Dates:";
                currentDatesDisplay = startDateStr + "\n" + endDateStr;
                pickStartDateButton.setText("Change Check-in Date");
                pickEndDateButton.setText("Change Check-out Date");
            }
            currentDatesLabelTextView.setText(labelText);
            currentDatesTextView.setText(currentDatesDisplay);

            selectedStartDateTextView.setText("");
            selectedEndDateTextView.setText("");
            saveChangesButton.setEnabled(false);
        } catch (Exception e) {
            Log.e(TAG, "Error during populateInitialData", e);
            Toast.makeText(this, "Error displaying booking data.", Toast.LENGTH_SHORT).show();
            itemNameTextView.setText(R.string.error_loading_data);
        }
        Log.d(TAG, "populateInitialData - FINISH");
    }

    // showDatePicker remains the same...
    private void showDatePicker(boolean isStartDate) {
        Calendar calendarToShow = isStartDate ? newStartDateCalendar : newEndDateCalendar;
        if (calendarToShow == null) calendarToShow = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog( this,
                (view, year, month, dayOfMonth) -> {
                    Calendar calendarToUpdate = isStartDate ? newStartDateCalendar : newEndDateCalendar;
                    if (calendarToUpdate == null) { calendarToUpdate = Calendar.getInstance(); if(isStartDate) newStartDateCalendar = calendarToUpdate; else newEndDateCalendar = calendarToUpdate; }
                    calendarToUpdate.set(year, month, dayOfMonth);
                    updateSelectedDateDisplay(isStartDate);
                    dateChanged = true;
                    saveChangesButton.setEnabled(true);
                },
                calendarToShow.get(Calendar.YEAR), calendarToShow.get(Calendar.MONTH), calendarToShow.get(Calendar.DAY_OF_MONTH) );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        if (!isStartDate && newStartDateCalendar != null) {
            long minEndDateMillis = newStartDateCalendar.getTimeInMillis();
            if (!"Service".equalsIgnoreCase(currentBooking.getBookingType())) { minEndDateMillis += (24 * 60 * 60 * 1000); }
            datePickerDialog.getDatePicker().setMinDate(minEndDateMillis);
        }
        datePickerDialog.show();
    }

    // showTimePicker remains the same...
    private void showTimePicker() {
        Calendar calendarToShow = newEndDateCalendar;
        if (calendarToShow == null) calendarToShow = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog( this,
                (view, hourOfDay, minute) -> {
                    if (newEndDateCalendar == null) newEndDateCalendar = Calendar.getInstance();
                    if(newStartDateCalendar != null) { newEndDateCalendar.setTime(newStartDateCalendar.getTime()); }
                    newEndDateCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    newEndDateCalendar.set(Calendar.MINUTE, minute);
                    updateSelectedDateDisplay(false);
                    dateChanged = true;
                    saveChangesButton.setEnabled(true);
                },
                calendarToShow.get(Calendar.HOUR_OF_DAY), calendarToShow.get(Calendar.MINUTE), false );
        timePickerDialog.show();
    }

    // updateSelectedDateDisplay remains the same...
    private void updateSelectedDateDisplay(boolean isStartDate) {
        try {
            if (isStartDate) {
                if (newStartDateCalendar != null && selectedStartDateTextView != null) {
                    selectedStartDateTextView.setText("New Start: " + dateFormatter.format(newStartDateCalendar.getTime()));
                }
            } else {
                if (newEndDateCalendar != null && selectedEndDateTextView != null) {
                    String formattedValue = ("Service".equalsIgnoreCase(currentBooking.getBookingType()))
                            ? timeFormatter.format(newEndDateCalendar.getTime())
                            : dateFormatter.format(newEndDateCalendar.getTime());
                    selectedEndDateTextView.setText( ( "Service".equalsIgnoreCase(currentBooking.getBookingType()) ? "New Time: " : "New End: " ) + formattedValue);
                }
            }
        } catch (Exception e) { Log.e(TAG, "Error formatting date/time in updateSelectedDateDisplay", e); }
    }

    // saveChanges remains the same...
    private void saveChanges() {
        Log.d(TAG, "saveChanges called. dateChanged=" + dateChanged);
        if (!dateChanged) { Toast.makeText(this, "No changes were made.", Toast.LENGTH_SHORT).show(); return; }
        Log.d(TAG, "Validating dates...");
        if (newStartDateCalendar == null || newEndDateCalendar == null || (!"Service".equalsIgnoreCase(currentBooking.getBookingType()) && !newEndDateCalendar.after(newStartDateCalendar))) {
            Toast.makeText(this, "Please select valid dates.", Toast.LENGTH_SHORT).show(); return;
        }
        Log.d(TAG, "Validation passed.");
        String newStartDateStr = dateFormatter.format(newStartDateCalendar.getTime());
        String newEndDateStr = ("Service".equalsIgnoreCase(currentBooking.getBookingType())) ? timeFormatter.format(newEndDateCalendar.getTime()) : dateFormatter.format(newEndDateCalendar.getTime());
        Log.i(TAG, "Simulating API call to update booking ID " + currentBooking.getBookingId() + " with Start=" + newStartDateStr + ", End=" + newEndDateStr);
        boolean updateSuccess = simulateApiUpdate();
        if (updateSuccess) {
            currentBooking.setStartDate(newStartDateStr);
            currentBooking.setEndDate(newEndDateStr);
            Toast.makeText(this, "Booking dates updated successfully (Simulated).", Toast.LENGTH_LONG).show();
            populateInitialData();
            selectedStartDateTextView.setText("");
            selectedEndDateTextView.setText("");
            saveChangesButton.setEnabled(false);
            dateChanged = false;
            Intent resultIntent = new Intent();
            resultIntent.putExtra(RESULT_BOOKING_UPDATED, true);
            resultIntent.putExtra(RESULT_BOOKING_ID, currentBooking.getBookingId());
            setResult(Activity.RESULT_OK, resultIntent);
        } else {
            Toast.makeText(this, "Failed to update booking dates (Simulated). Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    // showCancelConfirmation remains the same...
    private void showCancelConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Booking")
                .setMessage("Are you sure you want to cancel this booking?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes, Cancel", (dialog, whichButton) -> performCancellation())
                .setNegativeButton("No", null)
                .show();
    }

    // performCancellation remains the same...
    private void performCancellation() {
        if (currentBooking == null) return;
        Log.d(TAG, "Simulating API call to cancel booking ID: " + currentBooking.getBookingId());
        boolean cancelSuccess = simulateApiUpdate();
        if (cancelSuccess) {
            Toast.makeText(this, "Booking cancelled successfully (Simulated).", Toast.LENGTH_LONG).show();
            Intent resultIntent = new Intent();
            resultIntent.putExtra(RESULT_BOOKING_CANCELLED, true);
            resultIntent.putExtra(RESULT_BOOKING_ID, currentBooking.getBookingId());
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        } else {
            Toast.makeText(this, "Failed to cancel booking (Simulated).", Toast.LENGTH_SHORT).show();
        }
    }

    // simulateApiUpdate remains the same...
    private boolean simulateApiUpdate() {
        Log.d(TAG, "Simulating API update/cancel... returning TRUE");
        return true; // Always succeed for testing
    }

}
