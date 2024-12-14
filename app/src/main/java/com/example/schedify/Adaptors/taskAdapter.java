package com.example.schedify.Adaptors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.schedify.Models.Task;
import com.example.schedify.Activities.CreateTaskActivity;
import com.example.schedify.R;

import java.util.List;

public class taskAdapter extends RecyclerView.Adapter<taskAdapter.taskViewHolder> {
    @NonNull
    private final List<Task> taskList;
    Context context;

    public taskAdapter(@NonNull List<Task> taskList, Context context) {
        this.taskList = taskList;
        this.context = context;
    }

    @NonNull
    @Override
    public taskAdapter.taskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_list_view_holder, parent, false);
        return new taskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull taskAdapter.taskViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Task task = taskList.get(position);
        holder.titleText.setText(task.getTitle());
        holder.timeText.setText(task.getTime());
        holder.dateText.setText(task.getDate());
        holder.locationText.setText(task.getLocation());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent edit_task = new Intent(context, CreateTaskActivity.class);
                edit_task.putExtra("title", task.getTitle());
                edit_task.putExtra("date", task.getDate());
                edit_task.putExtra("location", task.getLocation());
                edit_task.putExtra("time", task.getTime());
                edit_task.putExtra("index", position);
                edit_task.putExtra("description", task.getDescription());
                context.startActivity(edit_task);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class taskViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, timeText, dateText, locationText;

        public taskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.tv_title);
            timeText = itemView.findViewById(R.id.tv_startTime);
            dateText = itemView.findViewById(R.id.tv_endTime);
            locationText = itemView.findViewById(R.id.tv_location);
        }
    }
}
