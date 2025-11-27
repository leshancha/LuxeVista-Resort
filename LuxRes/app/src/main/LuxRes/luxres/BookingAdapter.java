package com.example.luxres;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
        holder.bookingType.setText(String.format("Type: %s", booking.getBookingType()));

        String details;
        if ("Room".equals(booking.getBookingType())) {
            details = String.format("Dates: %s to %s", booking.getStartDate(), booking.getEndDate());
        } else { // Service
            details = String.format("Date: %s, Time: %s", booking.getStartDate(), booking.getEndDate()); // Using endDate for time here
        }
        holder.bookingDetails.setText(details);

        holder.bookingStatus.setText(String.format("Status: %s", booking.getStatus()));
        // Set status color (example)
        switch (booking.getStatus().toLowerCase()) {
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
        } else {
            holder.cancelButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    // ViewHolder Class
    public static class BookingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView itemName, bookingType, bookingDetails, bookingStatus;
        Button cancelButton; // Optional: Button to cancel booking
        OnBookingListener onBookingListener;

        public BookingViewHolder(@NonNull View itemView, OnBookingListener onBookingListener) {
            super(itemView);
            itemName = itemView.findViewById(R.id.textViewBookingItemName);
            bookingType = itemView.findViewById(R.id.textViewBookingType);
            bookingDetails = itemView.findViewById(R.id.textViewBookingDetails);
            bookingStatus = itemView.findViewById(R.id.textViewBookingStatus);
            cancelButton = itemView.findViewById(R.id.buttonCancelBooking); // Add this button to item_booking.xml
            this.onBookingListener = onBookingListener;

            itemView.setOnClickListener(this); // Handle click on the whole item
            cancelButton.setOnClickListener(v -> { // Handle click specifically on cancel button
                if(onBookingListener != null && getAdapterPosition() != RecyclerView.NO_POSITION){
                    onBookingListener.onCancelBookingClick(getAdapterPosition());
                }
            });
        }

        @Override
        public void onClick(View v) {
            if(onBookingListener != null && getAdapterPosition() != RecyclerView.NO_POSITION){
                onBookingListener.onBookingClick(getAdapterPosition());
            }
        }
    }

    // Click Listener Interface
    public interface OnBookingListener {
        void onBookingClick(int position); // Click on item
        void onCancelBookingClick(int position); // Click on cancel button
    }
}