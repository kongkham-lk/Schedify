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

    TextView text_view_one;
    TextView text_view_two;






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
        TextView text_view_one = (TextView) v.findViewById(R.id.text_view_one);
        TextView text_view_two = (TextView) v.findViewById(R.id.text_view_two);


        text_view_one.setText(courses.get(position).getCourse_code());
        text_view_two.setText(courses.get(position).getCourse_name());


        return v;
    }


}