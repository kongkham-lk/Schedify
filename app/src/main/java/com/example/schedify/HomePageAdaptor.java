package com.example.schedify;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class HomePageAdaptor extends ArrayAdapter<CourseModel> {

    private final ArrayList<CourseModel> courses;
    private final ArrayList<Task> tasks;
    private final LayoutInflater layoutInflater;
    private Context context;

    public HomePageAdaptor(@NonNull Context context, int resource, @NonNull ArrayList<CourseModel> objects, ArrayList<Task> tasks) {
        super(context, resource, objects);
        this.context = context;
        this.courses = objects;
        this.tasks = tasks;
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

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.home_items_view_holder, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tv_courseCode = convertView.findViewById(R.id.tv_courseCode);
            viewHolder.tv_location = convertView.findViewById(R.id.tv_location);
            viewHolder.tv_start_time = convertView.findViewById(R.id.tv_startTime);
            viewHolder.tv_end_time = convertView.findViewById(R.id.tv_endTime);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CourseModel course = courses.get(position);
        setBackgroundColor(convertView, course.isExpired());

        viewHolder.tv_courseCode.setText(course.getTitle());
        viewHolder.tv_location.setText(course.getLocation());
        viewHolder.tv_start_time.setText(course.getStartTime());
        viewHolder.tv_end_time.setText(course.getEndTime());

        convertView.findViewById(R.id.card_container).setOnClickListener(v -> {
            if (course.getClassDayList().length <= 2) {
                int newPosition = 0;
                for (int i = 0; i < tasks.size(); i++) {
                    if (tasks.get(i).getTitle().toString().equals(course.getTitle()) &&
                            tasks.get(i).getDescription().toString().equals(course.getDescription()) &&
                            tasks.get(i).getLocation().toString().equals(course.getLocation()) &&
                            tasks.get(i).getTime().toString().equals(course.getStartTime() + " - " + course.getEndTime()) &&
                            tasks.get(i).getDate().toString().equals(course.getStartDate() + " - " + course.getEndDate())) {
                        newPosition = i;
                        break;
                    }
                }
                Intent edit_task = new Intent(context, CreateTaskActivity.class);
                edit_task.putExtra("title", course.getTitle());
                edit_task.putExtra("date", course.getStartDate() + " - " + course.getEndDate());
                edit_task.putExtra("time", course.getStartTime() + " - " + course.getEndTime());
                edit_task.putExtra("index", newPosition);
                edit_task.putExtra("location", course.getLocation());
                edit_task.putExtra("homePage", "HOMEEDIT");
                edit_task.putExtra("description", course.getDescription());
                context.startActivity(edit_task);
            }
        });

        // Add an OnClickListener for the list item
        convertView.setOnClickListener(v -> {
            if (course.getClassDayList().length > 2) {
                int courseId = course.getUrlID();
                String url = "https://moodle.tru.ca/course/view.php?id=" + courseId;
                ((MainActivity) context).fetchcourseRegistrationAPI(url);
            }
        });

        return convertView;
    }

    private void setBackgroundColor(View convertView, boolean isExpired) {
        int color = isExpired ?
                ContextCompat.getColor(getContext(), R.color.grey) :
                ContextCompat.getColor(getContext(), R.color.darkGray);
        convertView.findViewById(R.id.card_container).setBackgroundColor(color);
    }

    private static class ViewHolder {
        TextView tv_courseCode;
        TextView tv_location;
        TextView tv_start_time;
        TextView tv_end_time;
    }
}