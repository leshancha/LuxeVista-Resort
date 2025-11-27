package com.example.luxres;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView; // Added for icon
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.luxres.R;
import com.example.luxres.Booking;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    List<Booking> bookingList;
    OnBookingListener onBookingListener;

    public BookingAdapter(List<Booking> bookingList, OnBookingListener onBookingListener) {
        this.bookingList = bookingList;
        this.onBookingListener = onBookingListener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view, onBookingListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.itemName.setText(booking.getItemName());

        String details;
        int iconResId; // Resource ID for the icon

        if ("Room".equalsIgnoreCase(booking.getBookingType())) {
            details = String.format("Dates: %s to %s", booking.getStartDate(), booking.getEndDate());
            iconResId = R.drawable.ic_hotel; // Assign hotel icon
        } else { // Service
            details = String.format("Date: %s, Time: %s", booking.getStartDate(), booking.getEndDate()); // Using endDate for time here
            // Assign different icons based on service type if available, otherwise a generic one
            iconResId = R.drawable.ic_service; // Assign generic service icon (or spa, dining etc.)
        }
        holder.bookingDetails.setText(details);
        holder.itemIcon.setImageResource(iconResId); // Set the icon

        holder.bookingStatus.setText(String.format("Status: %s", booking.getStatus()));
        // Set status color (example)
        switch (booking.getStatus().toLowerCase(Locale.ROOT)) {
            case "confirmed":
                holder.bookingStatus.setTextColor(Color.parseColor("#4CAF50")); // Green
                break;
            case "pending":
                holder.bookingStatus.setTextColor(Color.parseColor("#FF9800")); // Orange
                break;
            case "cancelled":
                holder.bookingStatus.setTextColor(Color.parseColor("#F44336")); // Red
                break;
            default:
                holder.bookingStatus.setTextColor(Color.DKGRAY);
                break;
        }

        // Show/Hide cancel button based on status (example logic)
        if ("Confirmed".equalsIgnoreCase(booking.getStatus()) || "Pending".equalsIgnoreCase(booking.getStatus())) {
            holder.cancelButton.setVisibility(View.VISIBLE);
            holder.cancelButton.setEnabled(true); // Ensure it's clickable
        } else {
            holder.cancelButton.setVisibility(View.GONE); // Hide for cancelled/completed bookings
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    // ViewHolder Class
    public static class BookingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView itemIcon; // Added for icon
        TextView itemName, bookingDetails, bookingStatus; // Removed bookingType TextView, incorporated icon instead
        Button cancelButton;
        OnBookingListener onBookingListener;

        public BookingViewHolder(@NonNull View itemView, OnBookingListener listener) {
            super(itemView);
            itemIcon = itemView.findViewById(R.id.imageViewBookingIcon); // Find icon view
            itemName = itemView.findViewById(R.id.textViewBookingItemName);
            bookingDetails = itemView.findViewById(R.id.textViewBookingDetails);
            bookingStatus = itemView.findViewById(R.id.textViewBookingStatus);
            cancelButton = itemView.findViewById(R.id.buttonCancelBooking);
            this.onBookingListener = listener;

            itemView.setOnClickListener(this); // Handle click on the whole item

            // --- Ensure cancel button listener is set correctly ---
            cancelButton.setOnClickListener(v -> {
                if (onBookingListener != null) {
                    int currentPosition = getAdapterPosition();
                    if (currentPosition != RecyclerView.NO_POSITION) {
                        // Disable button immediately to prevent multiple clicks
                        cancelButton.setEnabled(false);
                        onBookingListener.onCancelBookingClick(currentPosition);
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (onBookingListener != null) {
                int currentPosition = getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    onBookingListener.onBookingClick(currentPosition);
                }
            }
        }
    }

    // Click Listener Interface
    public interface OnBookingListener {
        void onBookingClick(int position); // Click on item
        void onCancelBookingClick(int position); // Click on cancel button
    }
}
