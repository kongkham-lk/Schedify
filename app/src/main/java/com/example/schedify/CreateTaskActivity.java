package com.example.schedify;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Calendar;
import java.util.Locale;

public class CreateTaskActivity extends AppCompatActivity {

    private EditText titleInput, descriptionInput;
    private Button datePicker, timePicker;
    int year, month, day, minute, hour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_create_task);

        titleInput = findViewById(R.id.title_input);
        descriptionInput = findViewById(R.id.description_input);
        datePicker = findViewById(R.id.date_picker);
        timePicker = findViewById(R.id.time_picker);
        Button returnBtn = findViewById(R.id.return_btn);
        Button saveBtn = findViewById(R.id.save_btn);

        Intent edit_intent = getIntent();
        if (edit_intent != null) {
            String loaded_title = edit_intent.getStringExtra("title");
            String loaded_date = edit_intent.getStringExtra("date");
            String loaded_time = edit_intent.getStringExtra("time");
            String loaded_description = edit_intent.getStringExtra("description");
            if (loaded_title != null) {
                titleInput.setText(loaded_title);
            }
            if (loaded_description != null) {
                descriptionInput.setText(loaded_description);
            }
            if (loaded_date != null) {
                datePicker.setText(loaded_date);
            }
            if (loaded_time != null) {
                timePicker.setText(loaded_time);
            }

        }

        returnBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("Fragment", "CreateFragment");
            startActivity(intent);
            finish();
        });

        timePicker.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(CreateTaskActivity.this, (view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                String formattedTime = sdf.format(calendar.getTime());
                timePicker.setText(formattedTime);
            }, hour, minute, false);
            timePickerDialog.show();
        });

        datePicker.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTaskActivity.this, (view1, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d", Locale.ENGLISH);
                String formattedDate = sdf.format(calendar.getTime()) + getDayOfMonthSuffix(dayOfMonth) + ", " + year;
                datePicker.setText(formattedDate);
            }, year, month, day);
            datePickerDialog.show();
        });

        saveBtn.setOnClickListener(view -> {
            String title = titleInput.getText().toString();
            String description = descriptionInput.getText().toString();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("Fragment", "CreateFragment");
            intent.putExtra("title", title);
            intent.putExtra("description", description);
            intent.putExtra("date", datePicker.getText().toString());
            intent.putExtra("time", timePicker.getText().toString());

            startActivity(intent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private String getDayOfMonthSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1: return "st";
            case 2: return "nd";
            case 3: return "rd";
            default: return "th";
        }
    }


}
