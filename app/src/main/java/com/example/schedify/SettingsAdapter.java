package com.example.schedify;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.MyViewHolder> {

    private final ArrayList<Settings> settingsOptions;

    public SettingsAdapter(ArrayList<Settings> settingsOptions) {
        this.settingsOptions = settingsOptions;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.settingsPrompt.setText(settingsOptions.get(position).getOptionName());
    }

    @Override
    public int getItemCount() {
        return settingsOptions.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView settingsPrompt;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            settingsPrompt = itemView.findViewById(R.id.settingsOption);
        }
    }
}