package com.example.luxres;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import java.util.Locale;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    List<Service> serviceList;
    OnServiceListener onServiceListener;

    public ServiceAdapter(List<Service> serviceList, OnServiceListener onServiceListener) {
        this.serviceList = serviceList;
        this.onServiceListener = onServiceListener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use the updated item_service layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view, onServiceListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);
        if (service == null) return; // Safety check

        holder.serviceName.setText(service.getName());
        holder.serviceDescription.setText(service.getDescription());

        // --- Load image using Glide ---
        if (service.getImageUrl() != null && !service.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(service.getImageUrl())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_service) // Use service placeholder
                            .error(R.drawable.ic_error_placeholder) // Use error placeholder
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(holder.serviceImage); // Target the ImageView
        } else {
            // Set placeholder if URL is missing
            holder.serviceImage.setImageResource(R.drawable.ic_service);
        }
        // --- End Glide usage ---


        // --- Handle Price Display ---
        if (service.getPrice() > 0) {
            holder.servicePrice.setText(String.format(Locale.getDefault(),"$%.2f", service.getPrice()));
            holder.servicePrice.setVisibility(View.VISIBLE);
        } else {
            holder.servicePrice.setVisibility(View.GONE); // Hide if no price or price varies
        }
    }

    @Override
    public int getItemCount() {
        return (serviceList != null) ? serviceList.size() : 0;
    }

    // ViewHolder Class
    public static class ServiceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView serviceImage; // <<< Add ImageView variable
        TextView serviceName, serviceDescription, servicePrice;
        OnServiceListener onServiceListener;

        public ServiceViewHolder(@NonNull View itemView, OnServiceListener onServiceListener) {
            super(itemView);
            // --- Find the ImageView ---
            serviceImage = itemView.findViewById(R.id.imageViewService); // <<< Find by ID
            // --- End Find ImageView ---
            serviceName = itemView.findViewById(R.id.textViewServiceName);
            serviceDescription = itemView.findViewById(R.id.textViewServiceDescription);
            servicePrice = itemView.findViewById(R.id.textViewServicePrice);
            this.onServiceListener = onServiceListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onServiceListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onServiceListener.onServiceClick(position);
                }
            }
        }
    }

    // Click Listener Interface
    public interface OnServiceListener {
        void onServiceClick(int position);
    }
}