package com.example.luxres; // Use your correct package name

import android.os.Bundle; // <<< REMOVE THIS IMPORT if not needed elsewhere
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// --- Import Glide ---
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
// --- End Glide Import ---

import java.util.List;

public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.AttractionViewHolder> {

    private List<Attraction> attractionList; // Made private
    private OnAttractionListener onAttractionListener; // Made private

    public AttractionAdapter(List<Attraction> attractionList, OnAttractionListener onAttractionListener) {
        this.attractionList = attractionList;
        this.onAttractionListener = onAttractionListener;
    }

    @NonNull
    @Override
    public AttractionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Ensure R.layout.item_attraction exists and is correct
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attraction, parent, false);
        return new AttractionViewHolder(view, onAttractionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AttractionViewHolder holder, int position) {
        Attraction attraction = attractionList.get(position);
        if (attraction == null) return; // Safety check

        // Ensure holder views are not null before setting text
        if (holder.attractionName != null) {
            holder.attractionName.setText(attraction.getName());
        }
        if (holder.attractionDescription != null) {
            holder.attractionDescription.setText(attraction.getDescription());
        }
        if (holder.attractionType != null) {
            holder.attractionType.setText(attraction.getType());
        }


        // --- Use Glide to load the image ---
        // Ensure holder.attractionImage is not null
        if (holder.attractionImage != null) {
            if (attraction.getImageUrl() != null && !attraction.getImageUrl().isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(attraction.getImageUrl())
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.ic_image_placeholder) // Ensure placeholder exists
                                .error(R.drawable.ic_error_placeholder) // Ensure error placeholder exists
                                .diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(holder.attractionImage);
            } else {
                // Set placeholder if URL is missing or empty
                holder.attractionImage.setImageResource(R.drawable.ic_image_placeholder);
            }
        } else {
            Log.e("AttractionAdapter", "ImageView attractionImage is null in ViewHolder!");
        }
        // --- End Glide usage ---
    }

    @Override
    public int getItemCount() {
        return (attractionList != null) ? attractionList.size() : 0;
    }

    // ViewHolder Class
    public static class AttractionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView attractionImage;
        TextView attractionName, attractionDescription, attractionType;
        OnAttractionListener onAttractionListener;

        public AttractionViewHolder(@NonNull View itemView, OnAttractionListener onAttractionListener) {
            super(itemView);
            // Ensure these IDs match item_attraction.xml
            attractionImage = itemView.findViewById(R.id.imageViewAttraction);
            attractionName = itemView.findViewById(R.id.textViewAttractionName);
            attractionDescription = itemView.findViewById(R.id.textViewAttractionDescription);
            attractionType = itemView.findViewById(R.id.textViewAttractionType);
            this.onAttractionListener = onAttractionListener;
            itemView.setOnClickListener(this);

            // Log if views are not found (optional debugging)
            if(attractionImage == null) Log.e("AttractionViewHolder", "imageViewAttraction not found!");
            if(attractionName == null) Log.e("AttractionViewHolder", "textViewAttractionName not found!");
            if(attractionDescription == null) Log.e("AttractionViewHolder", "textViewAttractionDescription not found!");
            if(attractionType == null) Log.e("AttractionViewHolder", "textViewAttractionType not found!");
        }

        @Override
        public void onClick(View v) {
            if (onAttractionListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onAttractionListener.onAttractionClick(position);
                }
            }
        }
    }

    // --- Click Listener Interface (Corrected) ---
    public interface OnAttractionListener {
        // void onCreate(Bundle savedInstanceState); // <<< REMOVE THIS LINE

        void onAttractionClick(int position); // Keep only the click method
    }
    // --- End Interface Correction ---
}
