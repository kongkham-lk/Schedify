package com.example.schedify;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class AssignmentAdapter extends android.widget.BaseAdapter {
    private Context context;
    private List<AssignmentDetail> assignments;

    public AssignmentAdapter(Context context, List<AssignmentDetail> assignments) {
        this.context = context;
        this.assignments = assignments;
    }

    @Override
    public int getCount() {
        return assignments.size();
    }

    @Override
    public Object getItem(int position) {
        return assignments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.home_items_view_holder, parent, false);
        }

        // Get assignment details
        AssignmentDetail assignment = assignments.get(position);

        // Bind data to views
        TextView title = convertView.findViewById(R.id.taskTitle);
        TextView courseDetails = convertView.findViewById(R.id.location_text);
        TextView dueTime = convertView.findViewById(R.id.taskStartTime);

        title.setText(assignment.getTitle());
        courseDetails.setText(assignment.getCourseDetails());
        dueTime.setText(assignment.getDueTime());

        return convertView;
    }
}

