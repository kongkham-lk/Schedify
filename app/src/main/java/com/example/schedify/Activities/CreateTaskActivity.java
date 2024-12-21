package com.example.schedify.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schedify.R;
import com.example.schedify.Util.Checker;
import com.example.schedify.Util.Transformer;

import java.time.LocalTime;
import java.util.Calendar;

public class CreateTaskActivity extends AppCompatActivity {

    private EditText titleInput, descriptionInput, locationInput;
    private Button datePickerStart, timePickerStart, datePickerEnd, timePickerEnd;
    int index = -1;
    private boolean fromHome = false;
    private boolean editing = false;
    private ImageButton deleteBtn;
    private long minDateForEndDate = 0;
    private long maxDateForStartDate = Long.MAX_VALUE;
    private String[] prevSaveDates = new String[2];
    private String[] prevSaveTimes = new String[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_task);

        titleInput = findViewById(R.id.edt_title);
        descriptionInput = findViewById(R.id.edt_description);
        datePickerStart = findViewById(R.id.btn_date_picker_start);
        timePickerStart = findViewById(R.id.btn_time_picker_start);
        datePickerEnd = findViewById(R.id.btn_date_picker_end);
        timePickerEnd = findViewById(R.id.btn_time_picker_end);
        ImageButton returnBtn = findViewById(R.id.btn_back);
        Button saveBtn = findViewById(R.id.btn_save);
        deleteBtn = findViewById(R.id.btn_delete);
        locationInput = findViewById(R.id.edt_location);

        Intent edit_intent = getIntent();
        if (edit_intent != null) {
            String loaded_title = edit_intent.getStringExtra("title");
            String loaded_date = edit_intent.getStringExtra("date");
            String loaded_time = edit_intent.getStringExtra("time");
            String loaded_description = edit_intent.getStringExtra("description");
            String loaded_location = edit_intent.getStringExtra("location");
            String locatedPage = edit_intent.getStringExtra("homePage");
            int position = edit_intent.getIntExtra("index", -1);
            if (loaded_title != null) {
                editing = true;
                titleInput.setText(loaded_title.replaceAll("_", ","));
                deleteBtn.setVisibility(View.VISIBLE);
            }
            if (loaded_description != null) {
                descriptionInput.setText(loaded_description.replaceAll("_", ","));
            }
            if (loaded_date != null) {
                String[] dates = loaded_date.split(" - ");
                datePickerStart.setText(Transformer.convertStringDateRawToStringDateDisplay(dates[0]));
                datePickerEnd.setText(Transformer.convertStringDateRawToStringDateDisplay(dates[1]));
                prevSaveDates = dates;
            }
            if (loaded_time != null) {
                String[] times = loaded_time.split(" - ");
                timePickerStart.setText(Transformer.convertStringTimeRawToStringTimeDisplay(times[0]));
                timePickerEnd.setText(Transformer.convertStringTimeRawToStringTimeDisplay(times[1]));
                prevSaveTimes = times;
            }
            if (loaded_location != null) {
                locationInput.setText(loaded_location.replaceAll("_", ","));
            }
            if (position != -1) {
                index = position;
            }
            if (locatedPage != null) {
                fromHome = true;
            }

        }
        if (timePickerStart.getText().toString().isEmpty()) {
            Calendar calendar = Calendar.getInstance();

            if (!Checker.isContainNullElement(prevSaveTimes)) {
                timePickerStart.setText(prevSaveTimes[0]);
                timePickerEnd.setText(prevSaveTimes[1]);
            } else {
                String formattedStartTime = Transformer.convertObjectToStringTimeDisplay(calendar.getTime());
                timePickerStart.setText(formattedStartTime);
                prevSaveTimes[0] = Transformer.convertObjectTimeToStringTimeRaw(calendar.getTime());

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int secondHour = hour + 1;
                calendar.set(Calendar.HOUR_OF_DAY, secondHour == 24 ? 0 : secondHour); // increment 1 hour

                String formattedEndTime = Transformer.convertObjectToStringTimeDisplay(calendar.getTime());
                timePickerEnd.setText(formattedEndTime);
                prevSaveTimes[1] = Transformer.convertObjectTimeToStringTimeRaw(calendar.getTime());
            }

            if (!Checker.isContainNullElement(prevSaveDates)) {
                String startDate = Transformer.convertStringDateRawToStringDateDisplay(prevSaveDates[0]);
                String endDate = Transformer.convertStringDateRawToStringDateDisplay(prevSaveDates[1]);
                datePickerStart.setText(startDate);
                datePickerEnd.setText(endDate);
            } else {
                String formattedStartDate = Transformer.convertObjectToStringDateDisplay(calendar.getTime());// + getDayOfMonthSuffix(day);
                datePickerStart.setText(formattedStartDate);
                String formattedEndDate = Transformer.convertObjectToStringDateDisplay(calendar.getTime());// + getDayOfMonthSuffix(day);
                datePickerEnd.setText(formattedEndDate);
                prevSaveDates[0] = prevSaveDates[1] = Transformer.convertObjectToStringDateRaw(calendar.getTime());
            }
        }

        returnBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("Fragment", "CreateFragment");
            startActivity(intent);
            finish();
        });

        timePickerStart.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            LocalTime prevSaveStartTime = getPrevSaveTime(prevSaveTimes != null ? prevSaveTimes[0] : null);
            int hour = prevSaveStartTime.getHour();
            int minute = prevSaveStartTime.getMinute();

            TimePickerDialog timePickerDialog = new TimePickerDialog(CreateTaskActivity.this, 2, (tempView, tempHourOfDay, tempMinute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, tempHourOfDay);
                calendar.set(Calendar.MINUTE, tempMinute);
                String formattedTime = Transformer.convertObjectToStringTimeDisplay(calendar.getTime());
                timePickerStart.setText(formattedTime);
                prevSaveTimes[0] = Transformer.convertObjectTimeToStringTimeRaw(calendar.getTime());
            }, hour, minute, false);
            timePickerDialog.show();
        });

        timePickerEnd.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            LocalTime prevSaveEndTime = getPrevSaveTime(prevSaveTimes != null ? prevSaveTimes[1] : null);
            int hour = prevSaveEndTime.getHour();
            int minute = prevSaveEndTime.getMinute();

            TimePickerDialog timePickerDialog = new TimePickerDialog(CreateTaskActivity.this, 2,(tempView, tempHourOfDay, tempMinute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, tempHourOfDay);
                calendar.set(Calendar.MINUTE, tempMinute);
                String formattedTime = Transformer.convertObjectToStringTimeDisplay(calendar.getTime());
                timePickerEnd.setText(formattedTime);
                prevSaveTimes[1] = Transformer.convertObjectTimeToStringTimeRaw(calendar.getTime());
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

        datePickerStart.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            Calendar prevSaveStartDate = getPrevSaveDate(prevSaveDates != null ? prevSaveDates[0] : null);
            int year = prevSaveStartDate.get(Calendar.YEAR);
            int month = prevSaveStartDate.get(Calendar.MONTH);
            int day = prevSaveStartDate.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTaskActivity.this, (tempView, tempYear, tempMonth, tempDayOfMonth) -> {
                calendar.set(Calendar.YEAR, tempYear);
                calendar.set(Calendar.MONTH, tempMonth);
                calendar.set(Calendar.DAY_OF_MONTH, tempDayOfMonth);
                String formattedDate = Transformer.convertObjectToStringDateDisplay(calendar.getTime());// + getDayOfMonthSuffix(dayOfMonth);
                datePickerStart.setText(formattedDate);
                setMinDateForEndDatePicker(calendar);
                prevSaveDates[0] = Transformer.convertObjectToStringDateRaw(calendar.getTime());
            }, year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(maxDateForStartDate);
            datePickerDialog.show();
        });

        datePickerEnd.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            Calendar prevSaveEndDate = getPrevSaveDate(prevSaveDates != null ? prevSaveDates[1] : null);
            int year = prevSaveEndDate.get(Calendar.YEAR);
            int month = prevSaveEndDate.get(Calendar.MONTH);
            int day = prevSaveEndDate.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTaskActivity.this, (tempView, tempYear, tempMonth, tempDayOfMonth) -> {
                calendar.set(Calendar.YEAR, tempYear);
                calendar.set(Calendar.MONTH, tempMonth);
                calendar.set(Calendar.DAY_OF_MONTH, tempDayOfMonth);
                String formattedDate = Transformer.convertObjectToStringDateDisplay(calendar.getTime());// + getDayOfMonthSuffix(dayOfMonth);
                datePickerEnd.setText(formattedDate);
                setMaxDateForStartDate(calendar);
                prevSaveDates[1] = Transformer.convertObjectToStringDateRaw(calendar.getTime());
            }, year, month, day);
            datePickerDialog.show();
            datePickerDialog.getDatePicker().setMinDate(minDateForEndDate);
        });

        saveBtn.setOnClickListener(view -> {
            if(titleInput.getText().toString().isEmpty() || datePickerStart.getText().toString().isEmpty() || datePickerEnd.getText().toString().isEmpty()
                    || timePickerStart.getText().toString().isEmpty() || timePickerEnd.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Title and Date cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                String title = titleInput.getText().toString().replaceAll(",", "_");
                String description = descriptionInput.getText().toString().replaceAll(",", "_");
                String location = locationInput.getText().toString().replaceAll(",", "_");

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                if (fromHome)
                    intent.putExtra("Fragment", "HomeFragment");
                else
                    intent.putExtra("Fragment", "CreateFragment");

                intent.putExtra("title", title);
                intent.putExtra("description", description);
                intent.putExtra("date", (String.join(" - ", prevSaveDates)));
                intent.putExtra("time", (String.join(" - ", prevSaveTimes)));
                intent.putExtra("location", location);
                if (editing)
                    intent.putExtra("index", index);

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

    private void setMinDateForEndDatePicker(Calendar startDate) {
        minDateForEndDate = startDate.getTimeInMillis();
    }

    private void setMaxDateForStartDate(Calendar endDate) {
        maxDateForStartDate = endDate.getTimeInMillis();
    }

    private LocalTime getPrevSaveTime(String prevSaveTime) {
        LocalTime resultTime;
        if (!Checker.isNull(prevSaveTime))
            resultTime = Transformer.convertTimeDisplayToLocalTimeObjectRaw(prevSaveTime);//
        else
            resultTime = LocalTime.now();
        return resultTime;
    }

    private Calendar getPrevSaveDate(String prevSaveDate) {
        Calendar resultDate;
        if (!Checker.isNull(prevSaveDate))
            resultDate = Transformer.convertDateRawToObject(prevSaveDate);
        else
            resultDate = Calendar.getInstance();
        return resultDate;
    }
}
