package com.example.schedify;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class HomePageAdaptor extends ArrayAdapter<CourseModel> {

    private final ArrayList<CourseModel> courses;
    private final LayoutInflater layoutInflater;

    public HomePageAdaptor(@NonNull Context context, int resource, @NonNull ArrayList<CourseModel> objects) {
        super(context, resource, objects);
        this.courses = objects;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return courses.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        // Check if convertView is null
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.home_items_view_holder, parent, false);
            viewHolder = new ViewHolder();

            // Initialize the views
            viewHolder.tv_courseCode = convertView.findViewById(R.id.tv_courseCode);
            viewHolder.tv_location = convertView.findViewById(R.id.tv_location);
            viewHolder.tv_start_time = convertView.findViewById(R.id.tv_startTime);
            viewHolder.tv_end_time = convertView.findViewById(R.id.tv_endTime);

            // Cache the ViewHolder
            convertView.setTag(viewHolder);
        } else {
            // Retrieve the cached ViewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Get the current CourseModel object
        CourseModel course = courses.get(position);

        if (course.isExpired()) {
            convertView.findViewById(R.id.card_container).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grey)); // Replace with your grey color
        } else {
            convertView.findViewById(R.id.card_container).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.darkGray)); // Default background
        }

        // Bind data to the views
        if (course != null) {
            viewHolder.tv_courseCode.setText(course.getTitle());
            viewHolder.tv_location.setText(course.getLocation());
            viewHolder.tv_start_time.setText(course.getStartTime());
            viewHolder.tv_end_time.setText(course.getEndTime());
        }

        return convertView;
    }

    // Static ViewHolder class to cache view references
    private static class ViewHolder {
        ImageView img_resource;
        TextView tv_courseCode;
        TextView tv_location;
        TextView tv_start_time;
        TextView tv_end_time;
    }
}
