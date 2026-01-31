package com.example.hazirjanabvendorportal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Adapter_ServiceRegistration extends RecyclerView.Adapter<Adapter_ServiceRegistration.ViewHolder> {
    private List<Class_ServiceRegistration> services;

    public Adapter_ServiceRegistration(List<Class_ServiceRegistration> services) {
        this.services = services;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.template_service_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Class_ServiceRegistration service = services.get(position);
        holder.tvServiceName.setText(service.getName());
        holder.cbSelect.setChecked(service.isSelected());

        holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            service.setSelected(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceName;
        CheckBox cbSelect;

        public ViewHolder(View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            cbSelect = itemView.findViewById(R.id.cbSelect);
        }
    }
}

