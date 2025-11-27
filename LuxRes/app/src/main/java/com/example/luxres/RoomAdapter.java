package com.example.luxres; // Use your correct package name

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// --- Import Glide ---
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
// --- End Glide Import ---

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    List<Room> roomList;
    OnRoomListener onRoomListener;
    Random random = new Random();

    // --- Constructor (No Context needed) ---
    public RoomAdapter(List<Room> roomList, OnRoomListener onRoomListener) {
        this.roomList = roomList;
        this.onRoomListener = onRoomListener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Ensure this uses the correct layout (e.g., item_room_improved.xml if you made it)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view, onRoomListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        if (room == null) return; // Safety check

        holder.roomName.setText(room.getName());
        // Use description from improved layout if applicable
        holder.roomDescription.setText(room.getDescription());
        holder.roomPrice.setText(String.format(Locale.getDefault(), "$%.0f / night", room.getPrice()));

        // --- Use Glide to load the image ---
        if (room.getImageUrl() != null && !room.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext()) // Use context from the item view
                    .load(room.getImageUrl()) // Load the URL from the Room object
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_image_placeholder) // Placeholder while loading
                            .error(R.drawable.ic_error_placeholder) // Image on error
                            .diskCacheStrategy(DiskCacheStrategy.ALL)) // Cache strategy
                    .into(holder.roomImage); // Target ImageView
        } else {
            // Set placeholder if URL is missing or empty
            holder.roomImage.setImageResource(R.drawable.ic_image_placeholder);
        }
        // --- End Glide usage ---

        // --- Set Placeholder Rating ---
        if (holder.ratingBar != null) {
            holder.ratingBar.setRating(4.0f + random.nextFloat()); // Example rating
        }
        // if (holder.ratingValue != null) { holder.ratingValue.setText(...); }


        // --- Availability ---
        if (holder.availabilityIndicator != null) {
            holder.availabilityIndicator.setVisibility(room.isAvailable() ? View.GONE : View.VISIBLE);
        }
        holder.itemView.setAlpha(room.isAvailable() ? 1.0f : 0.6f);
        holder.itemView.setClickable(room.isAvailable());
    }

    @Override
    public int getItemCount() {
        return (roomList != null) ? roomList.size() : 0;
    }

    // --- ViewHolder Class (Ensure all views are found) ---
    public static class RoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView roomImage;
        TextView roomName, roomDescription, roomPrice, ratingValue;
        RatingBar ratingBar;
        View availabilityIndicator;
        OnRoomListener onRoomListener;

        public RoomViewHolder(@NonNull View itemView, OnRoomListener onRoomListener) {
            super(itemView);
            // Make sure ALL these IDs exist in item_room.xml
            roomImage = itemView.findViewById(R.id.imageViewRoom);
            roomName = itemView.findViewById(R.id.textViewRoomName);
            roomDescription = itemView.findViewById(R.id.textViewRoomDescription); // Check ID if layout changed
            roomPrice = itemView.findViewById(R.id.textViewRoomPrice);
            ratingBar = itemView.findViewById(R.id.ratingBarRoom); // Check ID if layout changed
            ratingValue = itemView.findViewById(R.id.textViewRatingValue); // Check ID if layout changed
            availabilityIndicator = itemView.findViewById(R.id.viewAvailabilityIndicator);
            this.onRoomListener = onRoomListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onRoomListener != null && itemView.isClickable()) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onRoomListener.onRoomClick(position);
                }
            }
        }
    }

    // Click Listener Interface
    public interface OnRoomListener {
        void onRoomClick(int position);
    }
}
