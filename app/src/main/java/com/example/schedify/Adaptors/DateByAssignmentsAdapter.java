package com.example.schedify.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedify.Models.AssignmentsByDateGroup;
import com.example.schedify.R;

import java.util.List;

public class DateByAssignmentsAdapter extends RecyclerView.Adapter<DateByAssignmentsAdapter.ParentViewHolder> {

    private List<AssignmentsByDateGroup> assignmentsByDateGroupList;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private Context context;

    public DateByAssignmentsAdapter(Context context, List<AssignmentsByDateGroup> assignmentsByDateGroupList) {
        this.assignmentsByDateGroupList = assignmentsByDateGroupList;
        this.context = context;
    }

    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assignments_by_date_view_holder, parent, false);
        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentViewHolder holder, int position) {
        AssignmentsByDateGroup parentItem = assignmentsByDateGroupList.get(position);
        holder.dateGroup.setText(parentItem.getDate());

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                holder.DateByAssignmentsRecyclerView.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );
        layoutManager.setInitialPrefetchItemCount(parentItem.getAssignmentItemList().size());

        AssignmentItemAdapter assignmentItemAdapter = new AssignmentItemAdapter(context, parentItem.getAssignmentItemList());
        holder.DateByAssignmentsRecyclerView.setLayoutManager(layoutManager);
        holder.DateByAssignmentsRecyclerView.setAdapter(assignmentItemAdapter);
        holder.DateByAssignmentsRecyclerView.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount() {
        return assignmentsByDateGroupList.size();
    }

    public static class ParentViewHolder extends RecyclerView.ViewHolder {
        TextView dateGroup;
        RecyclerView DateByAssignmentsRecyclerView;

        public ParentViewHolder(@NonNull View itemView) {
            super(itemView);
            dateGroup = itemView.findViewById(R.id.tv_dateGroup);
            DateByAssignmentsRecyclerView = itemView.findViewById(R.id.date_by_assignments_recycler_view);
        }
    }
}