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


import com.example.schedify.Activities.CreateTaskActivity;
import com.example.schedify.Models.TaskModel;
import com.example.schedify.R;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.taskViewHolder> {
    @NonNull
    private final List<TaskModel> taskModelList;
    Context context;

    public TaskAdapter(@NonNull List<TaskModel> taskModelList, Context context) {
        this.taskModelList = taskModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public TaskAdapter.taskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_list_view_holder, parent, false);
        return new taskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.taskViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TaskModel taskModel = taskModelList.get(position);
        holder.titleText.setText(taskModel.getTitle());
        holder.timeText.setText(taskModel.getTime());
        holder.dateText.setText(taskModel.getDate());
        holder.locationText.setText(taskModel.getLocation());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent edit_task = new Intent(context, CreateTaskActivity.class);
                edit_task.putExtra("title", taskModel.getTitle());
                edit_task.putExtra("date", taskModel.getDate());
                edit_task.putExtra("location", taskModel.getLocation());
                edit_task.putExtra("time", taskModel.getTime());
                edit_task.putExtra("index", position);
                edit_task.putExtra("description", taskModel.getDescription());
                context.startActivity(edit_task);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskModelList.size();
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
