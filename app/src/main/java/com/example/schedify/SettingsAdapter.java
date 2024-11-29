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
        holder.tv_logout.setText(settingsOptions.get(position).getOptionName());
        holder.tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionManager sessionManager = new SessionManager(view.getContext());
                sessionManager.signOut();
            }
        });
    }

    @Override
    public int getItemCount() {
        return settingsOptions.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_logout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_logout = itemView.findViewById(R.id.tv_logout);
        }
    }
}