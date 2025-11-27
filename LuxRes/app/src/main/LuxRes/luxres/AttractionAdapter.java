package com.example.luxres;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.AttractionViewHolder> {

    List<Attraction> attractionList;
    OnAttractionListener onAttractionListener;

    public AttractionAdapter(List<Attraction> attractionList, OnAttractionListener onAttractionListener) {
        this.attractionList = attractionList;
        this.onAttractionListener = onAttractionListener;
    }

    @NonNull
    @Override
    public AttractionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attraction, parent, false);
        return new AttractionViewHolder(view, onAttractionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AttractionViewHolder holder, int position) {
        Attraction attraction = attractionList.get(position);
        holder.attractionName.setText(attraction.getName());
        holder.attractionDescription.setText(attraction.getDescription());
        holder.attractionType.setText(attraction.getType()); // Display type like "Offer", "Event"
        // TODO: Load image using Glide/Picasso
        // Glide.with(holder.itemView.getContext()).load(attraction.getImageUrl()).into(holder.attractionImage);
        holder.attractionImage.setImageResource(R.drawable.ic_launcher_foreground); // Placeholder
    }

    @Override
    public int getItemCount() {
        return attractionList.size();
    }

    // ViewHolder Class
    public static class AttractionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView attractionImage;
        TextView attractionName, attractionDescription, attractionType;
        OnAttractionListener onAttractionListener;

        public AttractionViewHolder(@NonNull View itemView, OnAttractionListener onAttractionListener) {
            super(itemView);
            attractionImage = itemView.findViewById(R.id.imageViewAttraction);
            attractionName = itemView.findViewById(R.id.textViewAttractionName);
            attractionDescription = itemView.findViewById(R.id.textViewAttractionDescription);
            attractionType = itemView.findViewById(R.id.textViewAttractionType); // Add this view
            this.onAttractionListener = onAttractionListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onAttractionListener.onAttractionClick(getAdapterPosition());
        }
    }

    // Click Listener Interface
    public interface OnAttractionListener {
        void onAttractionClick(int position);
    }
}