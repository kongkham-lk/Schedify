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

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Locale;

public class CreateTaskActivity extends AppCompatActivity {

    private EditText titleInput, descriptionInput, locationInput;
    private Button datePickerStart, timePickerStart, datePickerEnd, timePickerEnd;
    int year, month, day, minute, hour;
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
                titleInput.setText(loaded_title);
                deleteBtn.setVisibility(View.VISIBLE);
            }
            if (loaded_description != null) {
                descriptionInput.setText(loaded_description);
            }
            if (loaded_date != null) {
                String[] dates = loaded_date.split(" - ");
                datePickerStart.setText(Transformer.transformDate(dates[0]));
                datePickerEnd.setText(Transformer.transformDate(dates[1]));
                prevSaveDates = dates;
            }
            if (loaded_time != null) {
                String[] times = loaded_time.split(" - ");
                timePickerStart.setText(times[0]);
                timePickerEnd.setText(times[1]);
                prevSaveTimes = times;
            }
            if (loaded_location != null) {
                locationInput.setText(loaded_location);
            }
            if (position != -1) {
                index = position;
            }
            if (locatedPage != null) {
                fromHome = true;
//                deleteBtn.setVisibility(View.INVISIBLE);
//                returnBtn.setVisibility(View.INVISIBLE);
            }

        }
        if (timePickerStart.getText().toString().isEmpty()) {
            Calendar calendar = Calendar.getInstance();
//            int year = calendar.get(Calendar.YEAR);
//            int month = calendar.get(Calendar.MONTH);
//            int day = calendar.get(Calendar.DAY_OF_MONTH);
//            int hour = calendar.get(Calendar.HOUR_OF_DAY);
//            int minute = calendar.get(Calendar.MINUTE);

            if (!Checker.isContainNullElement(prevSaveTimes)) {
//                prevSaveTimes = DataTransformer.transformTime(String.join(" - ", prevSaveTimes)).split(" - ");
                timePickerStart.setText(prevSaveTimes[0]);
                timePickerEnd.setText(prevSaveTimes[1]);
            } else {
                String formattedStartTime = Transformer.convertDateObjectToStringTimeDisplay(calendar.getTime());
                timePickerStart.setText(formattedStartTime);
                prevSaveTimes[0] = Transformer.convertStringTimeToLocalDateTimeRaw(calendar.getTime());

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int secondHour = hour + 1;
                calendar.set(Calendar.HOUR_OF_DAY, secondHour == 24 ? 0 : secondHour); // increment 1 hour

                String formattedEndTime = Transformer.convertDateObjectToStringTimeDisplay(calendar.getTime());
                timePickerEnd.setText(formattedEndTime);
                prevSaveTimes[1] = Transformer.convertDateObjectToStringTimeDisplay(calendar.getTime());
            }

            if (!Checker.isContainNullElement(prevSaveDates)) {
                String startDate = Transformer.transformDate(prevSaveDates[0]);
                String endDate = Transformer.transformDate(prevSaveDates[1]);
                datePickerStart.setText(startDate);
                datePickerEnd.setText(endDate);
            } else {
                String formattedStartDate = Transformer.convertDateObjectToStringDateDisplay(calendar.getTime());// + getDayOfMonthSuffix(day);
                datePickerStart.setText(formattedStartDate);
                String formattedEndDate = Transformer.convertDateObjectToStringDateDisplay(calendar.getTime());// + getDayOfMonthSuffix(day);
                datePickerEnd.setText(formattedEndDate);
                prevSaveDates[0] = prevSaveDates[1] = Transformer.convertDateObjectToStringDateRaw(calendar.getTime());
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
            LocalDateTime prevSaveStartTime = getPrevSaveTime(prevSaveTimes != null ? prevSaveTimes[0] : null);
            hour = prevSaveStartTime.getHour();
            minute = prevSaveStartTime.getMinute();

            TimePickerDialog timePickerDialog = new TimePickerDialog(CreateTaskActivity.this, 2, (view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                String formattedTime = sdf.format(calendar.getTime());
                timePickerStart.setText(formattedTime);
                prevSaveTimes[0] = Transformer.convertDateObjectToStringTimeDisplay(calendar.getTime());
            }, hour, minute, false);
            timePickerDialog.show();
        });

        timePickerEnd.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            LocalDateTime prevSaveEndTime = getPrevSaveTime(prevSaveTimes != null ? prevSaveTimes[1] : null);
            hour = prevSaveEndTime.getHour();
            minute = prevSaveEndTime.getMinute();

            TimePickerDialog timePickerDialog = new TimePickerDialog(CreateTaskActivity.this, 2,(view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                String formattedTime = sdf.format(calendar.getTime());
                timePickerEnd.setText(formattedTime);
                prevSaveTimes[1] = Transformer.convertDateObjectToStringDateRaw(calendar.getTime());
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
            LocalDate prevSaveStartDate = getPrevSaveDate(prevSaveDates != null ? prevSaveDates[0] : null);
            int year = prevSaveStartDate.getYear();
            int month = prevSaveStartDate.getMonthValue() - 1;
            int day = prevSaveStartDate.getDayOfMonth();

            DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTaskActivity.this, (view1, year1, month1, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year1);
                calendar.set(Calendar.MONTH, month1);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
                String formattedDate = sdf.format(calendar.getTime());// + getDayOfMonthSuffix(dayOfMonth);
                datePickerStart.setText(formattedDate);
                setMinDateForEndDatePicker(calendar);
                prevSaveDates[0] = ++month1 + "/" + dayOfMonth + "/" + year1;
            }, year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(maxDateForStartDate);

            datePickerDialog.show();
        });

        datePickerEnd.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            LocalDate prevSaveEndDate = getPrevSaveDate(prevSaveDates != null ? prevSaveDates[1] : null);
            int year = prevSaveEndDate.getYear();
            int month = prevSaveEndDate.getMonthValue() - 1;
            int day = prevSaveEndDate.getDayOfMonth();

            DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTaskActivity.this, (view1, year1, month1, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year1);
                calendar.set(Calendar.MONTH, month1);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
                String formattedDate = sdf.format(calendar.getTime());// + getDayOfMonthSuffix(dayOfMonth);
                datePickerEnd.setText(formattedDate);
                setMaxDateForStartDate(calendar);
                prevSaveDates[1] = ++month1 + "/" + dayOfMonth + "/" + year1;
            }, year, month, day);

            datePickerDialog.show();
            datePickerDialog.getDatePicker().setMinDate(minDateForEndDate);
        });

        saveBtn.setOnClickListener(view -> {
            if(titleInput.getText().toString().isEmpty() || datePickerStart.getText().toString().isEmpty() || datePickerEnd.getText().toString().isEmpty() || timePickerStart.getText().toString().isEmpty() || timePickerEnd.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Title and Date cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                String title = titleInput.getText().toString();
                String description = descriptionInput.getText().toString();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                if (fromHome) {
                    intent.putExtra("Fragment", "HomeFragment");
                } else {
                    intent.putExtra("Fragment", "CreateFragment");
                }
                intent.putExtra("title", title);
                intent.putExtra("description", description);
                intent.putExtra("date", (String.join(" - ", prevSaveDates)));
                intent.putExtra("time", (String.join(" - ", prevSaveTimes)));
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

//    private String getDayOfMonthSuffix(int day) {
//        if (day >= 11 && day <= 13) {
//            return "th";
//        }
//        switch (day % 10) {
//            case 1: return "st";
//            case 2: return "nd";
//            case 3: return "rd";
//            default: return "th";
//        }
//    }

    private void setMinDateForEndDatePicker(Calendar startDate) {
        minDateForEndDate = startDate.getTimeInMillis();
    }

    private void setMaxDateForStartDate(Calendar endDate) {
        maxDateForStartDate = endDate.getTimeInMillis();
    }

    private LocalDateTime getPrevSaveTime(String prevSaveTime) {
        if (prevSaveTime != null && prevSaveTime.length() > 3) {
            try {
                // Parse the input date string
                LocalDateTime resultDate = Transformer.convertStringTimeToLocalDateTimeRaw(prevSaveTime);
                return resultDate;
            } catch (DateTimeParseException e) {
                System.err.println("Invalid date format: " + e.getMessage());
            }
        }
        return LocalDateTime.now();
    }

    private LocalDate getPrevSaveDate(String prevSaveDate) {
        if (prevSaveDate != null && prevSaveDate.length() > 3) {
            try {
                LocalDate resultDate = Transformer.convertRawStringDateToLocalDate(prevSaveDate);
                return resultDate;
            } catch (DateTimeParseException e) {
                System.err.println("Invalid date format: " + e.getMessage());
            }
        }
        return LocalDate.now();
    }


}
