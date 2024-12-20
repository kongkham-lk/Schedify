package com.example.schedify;

import android.content.Context;
import android.content.Intent;
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
            viewHolder.tv_courseCode = convertView.findViewById(R.id.tv_title);
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
            boolean isWebViewOpen = WebViewLoginDialog.isOpen;
            if (!isWebViewOpen) {
                if (course.getClassDayList().length <= 2) { // for task row
                    int newPosition = 0;
                    for (int i = 0; i < tasks.size(); i++) {
                        Task targetTask = tasks.get(i);
                        if (targetTask.getTitle().toString().equals(course.getTitle()) &&
                                targetTask.getDescription().toString().equals(course.getDescription()) &&
                                targetTask.getLocation().toString().equals(course.getLocation()) &&
                                targetTask.getTime().toString().equals(course.getStartTime() + " - " + course.getEndTime()) &&
                                targetTask.getDate().toString().equals(course.getStartDate() + " - " + course.getEndDate())) {
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
                } else { // for course reg row
                    int courseId = course.getUrlID();
                    String url = "https://moodle.tru.ca/course/view.php?id=" + courseId;
                    ((MainActivity) context).fetchcourseRegistrationAPI(url);
                }
            }
        });

        return convertView;
    }

    private void setBackgroundColor(View convertView, boolean isExpired) {
        int color = isExpired ?
                ContextCompat.getColor(getContext(), R.color.item_greyOut) :
                ContextCompat.getColor(getContext(), R.color.item_highlight);
        ((TextView)convertView.findViewById(R.id.tv_title)).setTextColor(color);
        ((TextView)convertView.findViewById(R.id.tv_location)).setTextColor(color);
        ((TextView)convertView.findViewById(R.id.tv_startTime)).setTextColor(color);
        ((TextView)convertView.findViewById(R.id.tv_endTime)).setTextColor(color);
        ((ImageView)convertView.findViewById(R.id.line)).setBackgroundColor(color);
    }

    private static class ViewHolder {
        TextView tv_courseCode;
        TextView tv_location;
        TextView tv_start_time;
        TextView tv_end_time;
    }
}