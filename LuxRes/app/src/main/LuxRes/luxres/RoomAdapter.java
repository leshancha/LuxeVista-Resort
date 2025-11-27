package com.example.luxres;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.luxres.Room;
import java.util.List;
// TODO: Add an image loading library like Glide or Picasso dependency in build.gradle

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    List<Room> roomList;
    OnRoomListener onRoomListener;

    public RoomAdapter(List<Room> roomList, OnRoomListener onRoomListener) {
        this.roomList = roomList;
        this.onRoomListener = onRoomListener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view, onRoomListener);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.roomName.setText(room.getName());
        holder.roomDescription.setText(room.getDescription());
        holder.roomPrice.setText(String.format("$%.2f / night", room.getPrice()));
        // TODO: Load image using Glide/Picasso: Glide.with(holder.itemView.getContext()).load(room.getImageUrl()).into(holder.roomImage);
        holder.availabilityIndicator.setVisibility(room.isAvailable() ? View.GONE : View.VISIBLE); // Show indicator if *not* available
        holder.itemView.setAlpha(room.isAvailable() ? 1.0f : 0.5f); // Dim if unavailable
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    // ViewHolder Class
    public static class RoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView roomImage;
        TextView roomName, roomDescription, roomPrice;
        View availabilityIndicator; // e.g., a red overlay or text
        OnRoomListener onRoomListener;

        public RoomViewHolder(@NonNull View itemView, OnRoomListener onRoomListener) {
            super(itemView);
            roomImage = itemView.findViewById(R.id.imageViewRoom);
            roomName = itemView.findViewById(R.id.textViewRoomName);
            roomDescription = itemView.findViewById(R.id.textViewRoomDescription);
            roomPrice = itemView.findViewById(R.id.textViewRoomPrice);
            availabilityIndicator = itemView.findViewById(R.id.viewAvailabilityIndicator); // Add this view in item_room.xml
            this.onRoomListener = onRoomListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onRoomListener.onRoomClick(getAdapterPosition());
        }
    }

    // Click Listener Interface
    public interface OnRoomListener {
        void onRoomClick(int position);
    }
}