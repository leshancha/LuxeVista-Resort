package com.example.luxres;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view, onServiceListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.serviceName.setText(service.getName());
        holder.serviceDescription.setText(service.getDescription());
        // TODO: Load image if available using Glide/Picasso
        // holder.serviceImage.setImageResource(R.drawable.placeholder_service); // Placeholder
        if (service.getPrice() > 0) {
            holder.servicePrice.setText(String.format(Locale.getDefault(),"$%.2f", service.getPrice()));
            holder.servicePrice.setVisibility(View.VISIBLE);
        } else {
            holder.servicePrice.setVisibility(View.GONE); // Hide if no price or price varies
        }
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    // ViewHolder Class
    public static class ServiceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView serviceImage; // Optional
        TextView serviceName, serviceDescription, servicePrice;
        OnServiceListener onServiceListener;

        public ServiceViewHolder(@NonNull View itemView, OnServiceListener onServiceListener) {
            super(itemView);
            // serviceImage = itemView.findViewById(R.id.imageViewService);
            serviceName = itemView.findViewById(R.id.textViewServiceName);
            serviceDescription = itemView.findViewById(R.id.textViewServiceDescription);
            servicePrice = itemView.findViewById(R.id.textViewServicePrice);
            this.onServiceListener = onServiceListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onServiceListener.onServiceClick(getAdapterPosition());
        }
    }

    // Click Listener Interface
    public interface OnServiceListener {
        void onServiceClick(int position);
    }
}