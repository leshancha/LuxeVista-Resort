package com.example.luxres;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.Locale;

public class ServiceDetailActivity extends AppCompatActivity {

    TextView serviceNameTextView, serviceDescriptionTextView, servicePriceTextView, selectedDateTextView, selectedTimeTextView;
    Button datePickerButton, timePickerButton, reserveButton;
    Service currentService;
    Calendar selectedDateTime = Calendar.getInstance(); // Store selected date/time

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        serviceNameTextView = findViewById(R.id.textViewServiceNameDetail);
        serviceDescriptionTextView = findViewById(R.id.textViewServiceDescriptionDetail);
        servicePriceTextView = findViewById(R.id.textViewServicePriceDetail);
        selectedDateTextView = findViewById(R.id.textViewSelectedDate);
        selectedTimeTextView = findViewById(R.id.textViewSelectedTime);
        datePickerButton = findViewById(R.id.buttonPickDate);
        timePickerButton = findViewById(R.id.buttonPickTime);
        reserveButton = findViewById(R.id.buttonReserveService);

        String serviceId = getIntent().getStringExtra("SERVICE_ID");
        String serviceName = getIntent().getStringExtra("SERVICE_NAME"); // Example

        if (serviceId != null) {
            // TODO: Fetch full service details from API using serviceId
            loadPlaceholderServiceDetails(serviceId, serviceName); // Replace with API call result
        } else {
            Toast.makeText(this, "Error: Service details not found.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        datePickerButton.setOnClickListener(v -> showDatePickerDialog());
        timePickerButton.setOnClickListener(v -> showTimePickerDialog());
        // Disable time picker initially until date is selected? Or allow both.
        // Disable reserve button initially until date/time selected & availability checked

        reserveButton.setOnClickListener(v -> {
            // TODO: Check if date/time are selected and potentially check availability via API
            if (selectedDateTextView.getText().length() > 0 && selectedTimeTextView.getText().length() > 0) {
                // Navigate to Confirmation or directly make reservation API call
                Intent intent = new Intent(this, BookingConfirmationActivity.class);
                intent.putExtra("BOOKING_TYPE", "Service");
                intent.putExtra("ITEM_ID", currentService.getId());
                intent.putExtra("ITEM_NAME", currentService.getName());
                intent.putExtra("ITEM_PRICE", currentService.getPrice()); // Price might be different based on time/options
                intent.putExtra("SELECTED_DATE", selectedDateTextView.getText().toString());
                intent.putExtra("SELECTED_TIME", selectedTimeTextView.getText().toString());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please select a date and time.", Toast.LENGTH_SHORT).show();
            }
        });

        updateDateTimeDisplay(); // Initialize display
    }

    private void loadPlaceholderServiceDetails(String serviceId, String serviceName) {
        // Simulate loading data - Replace with actual data from API
        currentService = new Service(serviceId, serviceName != null ? serviceName : "Default Service", "Detailed description of the service.", 99.00, "Category", null);
        serviceNameTextView.setText(currentService.getName());
        serviceDescriptionTextView.setText(currentService.getDescription());
        if (currentService.getPrice() > 0) {
            servicePriceTextView.setText(String.format(Locale.getDefault(),"Price: $%.2f", currentService.getPrice()));
        } else {
            servicePriceTextView.setText("Price varies or included");
        }
    }

    private void showDatePickerDialog() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateTimeDisplay();
                    // TODO: Optionally trigger availability check for the selected date
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(now.getTimeInMillis()); // Prevent past dates
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, minute);
                    updateDateTimeDisplay();
                    // TODO: Optionally trigger availability check for the selected date/time
                },
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                false // Use true for 24-hour format if desired
        );
        timePickerDialog.show();
    }

    private void updateDateTimeDisplay() {
        // Only display if date/time has been explicitly picked, avoid default values initially if needed
        if (selectedDateTime.get(Calendar.YEAR) != Calendar.getInstance().get(Calendar.YEAR) ||
                selectedDateTime.get(Calendar.DAY_OF_YEAR) != Calendar.getInstance().get(Calendar.DAY_OF_YEAR) ||
                selectedDateTextView.getText().length() > 0) { // Check if already set
            String dateFormat = "EEE, MMM d, yyyy"; // e.g., Wed, Jul 4, 2025
            selectedDateTextView.setText(android.text.format.DateFormat.format(dateFormat, selectedDateTime));
        }

        if (selectedDateTime.get(Calendar.HOUR_OF_DAY) != Calendar.getInstance().get(Calendar.HOUR_OF_DAY) ||
                selectedDateTime.get(Calendar.MINUTE) != Calendar.getInstance().get(Calendar.MINUTE) ||
                selectedTimeTextView.getText().length() > 0) { // Check if already set
            String timeFormat = "h:mm a"; // e.g., 1:30 PM
            selectedTimeTextView.setText(android.text.format.DateFormat.format(timeFormat, selectedDateTime));
        }
        // TODO: Enable/disable reserve button based on selection and availability check
    }
}