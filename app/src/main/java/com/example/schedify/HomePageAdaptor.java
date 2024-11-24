package com.example.schedify;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class HomePageAdaptor extends ArrayAdapter<CourseModel> {

    ArrayList<CourseModel> courses;

    LayoutInflater layoutInflater;
    ImageView img_resource;
    TextView courseCode;
    TextView tv_location;
    TextView start_time;
    TextView end_time;








    public HomePageAdaptor(@NonNull Context context, int resource,  ArrayList<CourseModel> objects) {
        super(context, resource, objects);

        this.courses = objects;
        layoutInflater = LayoutInflater.from(context);


    }


    @Override
    public int getCount() {
        return courses.size();
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = layoutInflater.inflate(R.layout.home_items, null);

        ImageView course_img = (ImageView) v. findViewById(R.id.course_img);
        TextView text_view_one = (TextView) v.findViewById(R.id.courseCode);
        TextView tv_location = (TextView) v.findViewById(R.id.tv_location);
        TextView start_time = (TextView) v.findViewById(R.id.startTime);
        TextView end_time = (TextView) v.findViewById(R.id.endTime);





        course_img.setImageResource(courses.get(position).getCourse_img());
        text_view_one.setText(courses.get(position).getCourseCode());
        tv_location.setText(courses.get(position).getTv_location());
        start_time.setText(courses.get(position).getStartTime());
        end_time.setText(courses.get(position).getEndTime());






        return v;
    }


}