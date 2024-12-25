package com.example.schedify.Adaptors;

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

import com.example.schedify.Activities.MainActivity;
import com.example.schedify.Models.Task;
import com.example.schedify.Models.Course;
import com.example.schedify.Activities.CreateTaskActivity;
import com.example.schedify.R;
import com.example.schedify.Components.WebViewLoginDialog;
import com.example.schedify.Util.Checker;
import com.example.schedify.Util.Transformer;

import java.util.ArrayList;

public class HomePageAdaptor extends ArrayAdapter<Course> {

    private final ArrayList<Course> courses;
    private final ArrayList<Task> tasks;
    private final LayoutInflater layoutInflater;
    private Context context;

    public HomePageAdaptor(@NonNull Context context, int resource, @NonNull ArrayList<Course> objects, ArrayList<Task> tasks) {
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
            viewHolder.tv_title = convertView.findViewById(R.id.tv_title);
            viewHolder.tv_location = convertView.findViewById(R.id.tv_location);
            viewHolder.tv_start_time = convertView.findViewById(R.id.tv_startTime);
            viewHolder.tv_end_time = convertView.findViewById(R.id.tv_endTime);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Course course = courses.get(position);
        String[] times = course.getTime().split(" - ");
        setBackgroundColor(convertView, Checker.isTimeExpired(course));

        viewHolder.tv_title.setText(Transformer.replaceUnderscoreWithComma(Transformer.replaceUnderscoreWithComma(course.getTitle())));
        viewHolder.tv_location.setText(Transformer.replaceUnderscoreWithComma(Transformer.replaceUnderscoreWithComma(course.getLocation())));
        viewHolder.tv_start_time.setText(Transformer.convertStringTimeRawToStringTimeDisplay(times[0]));
        viewHolder.tv_end_time.setText(Transformer.convertStringTimeRawToStringTimeDisplay(times[1]));

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
                                targetTask.getTime().toString().equals(course.getTime()) &&
                                targetTask.getDate().toString().equals(course.getDate())) {
                            newPosition = i;
                            break;
                        }
                    }
                    Intent edit_task = new Intent(context, CreateTaskActivity.class);
                    edit_task.putExtra("title", course.getTitle());
                    edit_task.putExtra("date", course.getDate());
                    edit_task.putExtra("time", course.getTime());
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
        TextView tv_title;
        TextView tv_location;
        TextView tv_start_time;
        TextView tv_end_time;
    }
}