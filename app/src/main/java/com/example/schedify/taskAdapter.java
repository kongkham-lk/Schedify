package com.example.schedify;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class taskAdapter extends RecyclerView.Adapter<taskAdapter.taskViewHolder> {
    @NonNull
    private final List<Task> taskList;

    public taskAdapter(@NonNull List<Task> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public taskAdapter.taskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_list_view_holder, parent, false);
        return new taskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull taskAdapter.taskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.titleText.setText(task.getTitle());
        holder.timeText.setText(task.getTime());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class taskViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView timeText;

        public taskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.taskTitle);
            timeText = itemView.findViewById(R.id.taskDueDate);
        }
    }
}
