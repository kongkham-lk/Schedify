package com.example.schedify.Adaptors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedify.Models.SettingsModel;
import com.example.schedify.R;
import com.example.schedify.Util.SessionManager;

import java.util.ArrayList;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.MyViewHolder> {

    private final ArrayList<SettingsModel> settingsModelOptions;

    public SettingsAdapter(ArrayList<SettingsModel> settingsModelOptions) {
        this.settingsModelOptions = settingsModelOptions;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_logout.setText(settingsModelOptions.get(position).getOptionName());
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
        return settingsModelOptions.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_logout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_logout = itemView.findViewById(R.id.tv_logout);
        }
    }
}