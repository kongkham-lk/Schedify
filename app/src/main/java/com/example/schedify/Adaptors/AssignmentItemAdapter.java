package com.example.schedify.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedify.Activities.MainActivity;
import com.example.schedify.Models.AssignmentDetail;
import com.example.schedify.R;

import java.util.List;

public class AssignmentItemAdapter extends RecyclerView.Adapter<AssignmentItemAdapter.ChildViewHolder> {

    private List<AssignmentDetail> assignmentList;
    private Context context;

    public AssignmentItemAdapter(Context context, List<AssignmentDetail> assignmentList) {
        this.assignmentList = assignmentList;
        this.context = context;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assignment_view_holder, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        AssignmentDetail targetAssignmentItem = assignmentList.get(position);
        holder.title.setText(targetAssignmentItem.getTitle());
        holder.description.setText(targetAssignmentItem.getDescription());
        holder.dueTime.setText(targetAssignmentItem.getDueTime());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = targetAssignmentItem.getAssignmentLink();
                ((MainActivity) context).fetchAPIData(url);
            }
        });
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    public static class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;
        TextView dueTime;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            description = itemView.findViewById(R.id.tv_description);
            dueTime = itemView.findViewById(R.id.tv_dueTime);
        }
    }
}

