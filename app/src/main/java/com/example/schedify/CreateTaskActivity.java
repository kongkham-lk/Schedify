package com.example.schedify;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class CreateTaskActivity extends AppCompatActivity {

    private EditText titleInput, descriptionInput, locationInput;
    private Button datePicker, timePicker, datePickerEnd, timePickerEnd;
    int year, month, day, minute, hour;
    int index = -1;
    private boolean editing = false;
    private ImageButton deleteBtn;
    private long minDateForEndDate = 0;
    private long maxDateForStartDate = Long.MAX_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_task);

        titleInput = findViewById(R.id.title_input);
        descriptionInput = findViewById(R.id.description_input);
        datePicker = findViewById(R.id.date_picker);
        timePicker = findViewById(R.id.time_picker);
        datePickerEnd = findViewById(R.id.date_picker_end);
        timePickerEnd = findViewById(R.id.time_picker_end);
        ImageButton returnBtn = findViewById(R.id.return_btn);
        Button saveBtn = findViewById(R.id.save_btn);
        deleteBtn = findViewById(R.id.delete_btn);
        locationInput = findViewById(R.id.location_input);

        Intent edit_intent = getIntent();
        if (edit_intent != null) {
            String loaded_title = edit_intent.getStringExtra("title");
            String loaded_date = edit_intent.getStringExtra("date");
            String loaded_time = edit_intent.getStringExtra("time");
            String loaded_description = edit_intent.getStringExtra("description");
            String loaded_location = edit_intent.getStringExtra("location");
            int position = edit_intent.getIntExtra("index", -1);
            if (loaded_title != null) {
                editing = true;
                titleInput.setText(loaded_title);
                deleteBtn.setVisibility(View.VISIBLE);
            }
            if (loaded_description != null) {
                descriptionInput.setText(loaded_description);
            }
            if (loaded_date != null) {
                String[] parts = loaded_date.split(" - ");
                datePicker.setText(parts[0]);
                datePickerEnd.setText(parts[1]);
            }
            if (loaded_time != null) {
                String[] parts = loaded_time.split(" - ");
                timePicker.setText(parts[0]);
                timePickerEnd.setText(parts[1]);
            }
            if (loaded_location != null) {
                locationInput.setText(loaded_location);
            }
            if (position != -1) {
                index = position;
            }

        }
        if (timePicker.getText().toString().isEmpty()) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            int secondHour = hour + 1;
            if (secondHour == 24) {
                secondHour = 0;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            String formattedTime = sdf.format(calendar.getTime());
            timePicker.setText(formattedTime);
            calendar.set(Calendar.HOUR_OF_DAY, secondHour);

            SimpleDateFormat sdf4 = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            String formattedTime4 = sdf4.format(calendar.getTime());
            timePickerEnd.setText(formattedTime4);

            SimpleDateFormat sdf2 = new SimpleDateFormat("MMM d", Locale.ENGLISH);
            String formattedDate = sdf2.format(calendar.getTime()) + getDayOfMonthSuffix(day);
            datePicker.setText(formattedDate);

            SimpleDateFormat sdf3 = new SimpleDateFormat("MMM d", Locale.ENGLISH);
            String formattedDate2 = sdf3.format(calendar.getTime()) + getDayOfMonthSuffix(day);
            datePickerEnd.setText(formattedDate2);

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

            TimePickerDialog timePickerDialog = new TimePickerDialog(CreateTaskActivity.this, 2, (view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                String formattedTime = sdf.format(calendar.getTime());
                timePicker.setText(formattedTime);
            }, hour, minute, false);
            timePickerDialog.show();
        });

        timePickerEnd.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(CreateTaskActivity.this, 2,(view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                String formattedTime = sdf.format(calendar.getTime());
                timePickerEnd.setText(formattedTime);
            }, hour, minute, false);

            timePickerDialog.show();
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deleteBtn.getVisibility() == View.VISIBLE && index != -1) {

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("Fragment", "CreateFragment");
                    intent.putExtra("delete", index);

                    startActivity(intent);
                    finish();
                }
            }
        });

        datePicker.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTaskActivity.this, (view1, year1, month1, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year1);
                calendar.set(Calendar.MONTH, month1);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("MMM d", Locale.ENGLISH);
                String formattedDate = sdf.format(calendar.getTime()) + getDayOfMonthSuffix(dayOfMonth);
                datePicker.setText(formattedDate);

                setMinDateForEndDatePicker(calendar);

            }, year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(maxDateForStartDate);

            datePickerDialog.show();
        });

        datePickerEnd.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTaskActivity.this, (view1, year1, month1, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year1);
                calendar.set(Calendar.MONTH, month1);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("MMM d", Locale.ENGLISH);
                String formattedDate = sdf.format(calendar.getTime()) + getDayOfMonthSuffix(dayOfMonth);
                datePickerEnd.setText(formattedDate);
                setMaxDateForStartDate(calendar);
            }, year, month, day);

            datePickerDialog.show();
            datePickerDialog.getDatePicker().setMinDate(minDateForEndDate);
        });

        saveBtn.setOnClickListener(view -> {
            if(titleInput.getText().toString().isEmpty() || datePicker.getText().toString().isEmpty() || datePickerEnd.getText().toString().isEmpty() || timePicker.getText().toString().isEmpty() || timePickerEnd.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Title and Date cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                String title = titleInput.getText().toString();
                String description = descriptionInput.getText().toString();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("Fragment", "CreateFragment");
                intent.putExtra("title", title);
                intent.putExtra("description", description);
                intent.putExtra("date", (datePicker.getText().toString()) + " - " + datePickerEnd.getText().toString());
                intent.putExtra("time", (timePicker.getText().toString()) + " - " + timePickerEnd.getText().toString());
                intent.putExtra("location", locationInput.getText().toString());
                if (editing) {
                    intent.putExtra("index", index);
                }
                startActivity(intent);
                finish();
            }
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

    private void setMinDateForEndDatePicker(Calendar startDate) {
        minDateForEndDate = startDate.getTimeInMillis();
    }

    private void setMaxDateForStartDate(Calendar endDate) {
        maxDateForStartDate = endDate.getTimeInMillis();
    }


}
