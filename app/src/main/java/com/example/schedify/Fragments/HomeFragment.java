package com.example.schedify.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.schedify.Models.Task;
import com.example.schedify.Adaptors.HomePageAdaptor;
import com.example.schedify.Models.Course;
import com.example.schedify.R;
import com.example.schedify.Util.Transformer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private String mParam1;
    private String mParam2;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final String KEY_TASKLIST = "taskList";
    private final String KEY_COURSELIST = "courseList";

    private ListView list_view_home;
    private ArrayList<Course> courses;
    private ArrayList<Task> numTasks;
    Button syncBtn;

    private Handler handler = new Handler();
    private int lastCheckedMinute = -1;

    public HomeFragment() {
    }

    private OnSyncButtonClickListener mListener;

    public interface OnSyncButtonClickListener {
        void onSyncButtonClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSyncButtonClickListener) {
            mListener = (OnSyncButtonClickListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnSyncButtonClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startMinuteChangeCheck();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        list_view_home = view.findViewById(R.id.list_view_home); // Initialize the ListView
        syncBtn = view.findViewById(R.id.btn_sync);

        // Initialize course data (Replace with your data source)
        courses = new ArrayList<>();
        numTasks = new ArrayList<>();

        syncBtn.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onSyncButtonClicked();
            }
        });
        createList();

        if (getActivity() != null && getActivity().getIntent() != null) {
            Intent intent = getActivity().getIntent();
            String title = intent.getStringExtra("title");
            String description = intent.getStringExtra("description");
            String date = intent.getStringExtra("date");
            String time = intent.getStringExtra("time");
            String location = intent.getStringExtra("location");
            int index = intent.getIntExtra("index", -1);

            if ((title != null && description != null && date != null) && index != -1) {
                Task task = numTasks.get(index);
                int pos = -1;
                for (int i = 0; i < courses.size(); i++) {
//                    String tempStartTime = courses.get(i).getStartTime();
//                    String tempEndTime = courses.get(i).getEndTime();
//                    String tempStartDate = courses.get(i).getStartDate();
//                    String tempEndDate = courses.get(i).getEndDate();
                    String tempTime = courses.get(i).getTime();
                    String tempDate = courses.get(i).getDate();
                    if (courses.get(i).getTitle().equals(task.getTitle()) &&
                            courses.get(i).getLocation().equals(task.getLocation()) &&
                            courses.get(i).getDescription().equals(task.getDescription()) &&
                            tempTime.equals(task.getTime()) &&
                            tempDate.equals(task.getDate())) {
                        pos = i;
                        break;
                    }
                }
                if (pos != -1) {
//                    String[] newTime = time.split(" - ");
//                    newTime[0] = newTime[0].trim();
//                    newTime[1] = newTime[1].trim();
                    numTasks.get(index).setTitle(title);
                    numTasks.get(index).setDescription(description);
                    numTasks.get(index).setDate(date);
                    numTasks.get(index).setTime(time);
                    numTasks.get(index).setLocation(location);
                    courses.get(pos).setTitle(title);
                    courses.get(pos).setDate(date);
                    courses.get(pos).setTime(time);
                    courses.get(pos).setLocation(location);
                    saveTaskList(numTasks);
                }
            }
        }
        return view;
    }

    private void saveTaskList(List<Task> taskList) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        StringBuilder sb = new StringBuilder();
        for (Task task : numTasks)
            sb.append(task.getTitle()).append("|")
                    .append(task.getDescription()).append("|")
                    .append(task.getTime()).append("|")
                    .append(task.getDate()).append("|")
                    .append(task.getLocation()).append("|;");

        editor.putString(KEY_TASKLIST, sb.toString());
        editor.apply();
    }

    private void createList() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppData", Context.MODE_PRIVATE);
        String taskData = sharedPreferences.getString(KEY_TASKLIST, "");
        String courseData = sharedPreferences.getString(KEY_COURSELIST, "");
        courses.clear();
        if (!taskData.isEmpty()) {
            String[] tasks = taskData.split(";");
            for (String task : tasks) {
                String[] taskDetails = task.split("\\|");
                Log.d(taskDetails.length + "", Arrays.toString(taskDetails));
                if (taskDetails.length == 5) { // Task data - full detail
                    String title = taskDetails[0];
                    String description = taskDetails[1];
                    String location = taskDetails[4];
                    String time = taskDetails[2];
//                    String[] times = time.split(" - ");
                    String date = taskDetails[3];
//                    String[] dates = date.split(" - ");
//                    dates[0] = dates[0].trim();
//                    dates[1] = dates[1].trim();
                    boolean[] classDayList = {false, true};
                    boolean isToday = compareDate(date);
                    if (isToday) {
                        numTasks.add(new Task(title, description, time, date, location));
                        courses.add(new Course(title, description, time, date, location, classDayList, 0, true));
                    }
                } else if (taskDetails.length == 4) { // Task data - without location
                    String title = taskDetails[0];
                    String description = taskDetails[1];
                    String time = taskDetails[2];
//                    String[] times = time.split(" - ");
                    String date = taskDetails[3];
//                    String[] dates = date.split(" - ");
                    boolean[] classDayList = {false, true};
//                    dates[0] = dates[0].trim();
//                    dates[1] = dates[1].trim();
                    boolean isToday =  compareDate(date);
                    if (isToday) {
                        numTasks.add(new Task(title, description, time, date, ""));
                        courses.add(new Course(title, description, time, date, "", classDayList, 0, true));
                    }
                }
            }
        }

        if (!courseData.isEmpty()) {
            String[] tasks = courseData.split(";");
            for (String task : tasks) {
                String[] taskDetails = task.split("\\|");
//                Log.d(taskDetails.length + "", Arrays.toString(taskDetails));
                if (taskDetails.length >= 6) {
                    String title = taskDetails[0];
                    String description = taskDetails[1];
                    String time = taskDetails[2];
//                    String[] times = time.split(" - ");
                    String date = taskDetails[3];
//                    String[] dates = date.split(" - ");
                    String location = taskDetails[4];
//                    dates[0] = dates[0].trim();
//                    dates[1] = dates[1].trim();
                    int urlID = Integer.parseInt(taskDetails[6]);
                    boolean isRegistered = taskDetails.length > 7 ? Boolean.parseBoolean(taskDetails[7]) : false;
//                    Log.d("Class days", taskDetails[5]);

                    String[] stringArray = taskDetails[5].substring(1, taskDetails[5].length()-1).replace(",", "").trim().split(" ");
                    boolean[] classDayList = new boolean[stringArray.length];
                    for (int i = 0; i < stringArray.length; i++) {
                        if(title.toLowerCase().contains("data"))
                            classDayList[i] = true;
                        else
                            classDayList[i] = Boolean.parseBoolean(stringArray[i]);
                    }
//                    Log.d("Class days", Arrays.toString(classDayList));

                    LocalDate today = LocalDate.now();
                    int todayDayOfWeek = today.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday
                    todayDayOfWeek--; // Adjust to 0-based index (0=Monday, 6=Sunday)

                    boolean isToday = compareDate(date);
                    boolean exactDay = false;

                    if (isToday && classDayList[todayDayOfWeek]) {
                        exactDay = true;  // Set exactDay to true if today is a class day
                    }

                    if (isToday && exactDay)
                        courses.add(new Course(title, description, time, date, location, classDayList, urlID, isRegistered));
                }
            }
        }
        sortCourses();

        courses.sort((course1, course2) -> {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            try {
                String[] schduleTimeCourse1 = course1.getTime().split(" - ");
                String[] schduleTimeCourse2 = course2.getTime().split(" - ");

                Calendar currentDate = Calendar.getInstance();

                Calendar endTime1 = Calendar.getInstance();
                endTime1.setTime(timeFormat.parse(schduleTimeCourse1[1]));
                Calendar endTime2 = Calendar.getInstance();
                endTime2.setTime(timeFormat.parse(schduleTimeCourse2[1]));

                course1.setExpired(endTime1.getTime().before(currentDate.getTime()));
                course2.setExpired(endTime2.getTime().before(currentDate.getTime()));

                if (course1.isExpired() && !course2.isExpired()) return 1;
                if (!course1.isExpired() && course2.isExpired()) return -1;

                Calendar startTime1 = Calendar.getInstance();
                startTime1.setTime(timeFormat.parse(schduleTimeCourse1[0]));

                Calendar startTime2 = Calendar.getInstance();
                startTime2.setTime(timeFormat.parse(schduleTimeCourse2[0]));

                return startTime1.getTime().compareTo(startTime2.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        });

        HomePageAdaptor homePageAdaptor = new HomePageAdaptor(requireContext(), R.layout.home_items_view_holder, courses, numTasks);
        list_view_home.setAdapter(homePageAdaptor);
    }

    private String formatTimeToAMPM(String timeString) {
        try {
            if (timeString.length() == 4) {
                int hours = Integer.parseInt(timeString.substring(0, 2));
                int minutes = Integer.parseInt(timeString.substring(2, 4));

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hours);
                calendar.set(Calendar.MINUTE, minutes);

                Date date = calendar.getTime();

                SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a");
                return outputFormat.format(date);
            } else {
                throw new ParseException("Invalid time format", 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void filterOutCourseList(List<Course> newCourseList) {
        Log.d("HomeFragment", "Updating course list");

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        createList();

        if (newCourseList != null) {
            StringBuilder serializedCourses = new StringBuilder();

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy"); // Desired format: "Nov 1"

            for (Course newCourse : newCourseList) {
                String[] times = newCourse.getTime().split(" - ");
                String formattedStartTime = formatTimeToAMPM(times[0]);
                String formattedEndTime = formatTimeToAMPM(times[1]);
                String formattedTime = formattedStartTime + " - " + formattedEndTime;
                newCourse.setTime(formattedTime);
//                newCourse.setEndTime(formattedEndTime);

                String formattedDate = "";

                try {
                    String[] dates = newCourse.getDate().split(" - ");
                    LocalDate startDate = LocalDate.parse(dates[0], inputFormatter);
                    LocalDate endDate = LocalDate.parse(dates[1], inputFormatter);

                    String formattedStartDate = startDate.format(outputFormatter);// + getDaySuffix(startDate.getDayOfMonth());
                    String formattedEndDate = endDate.format(outputFormatter);// + getDaySuffix(endDate.getDayOfMonth());
                    formattedDate = formattedStartDate + " - " + formattedEndDate;
                } catch (Exception e) {
                    Log.e("HomeFragment", "Error parsing date: " + e.getMessage());
                }

//                String courseCode = newCourse.getTitle().replace(",", "");
//                String title = newCourse.getTitle().replace(",", "");
//                String description = newCourse.getTitle().replace(",", "");
//                String startTime = formattedTime.replace(",", "");
//                String endTime = formattedEndTime.replace(",", "");

                serializedCourses
                        .append(newCourse.getTitle()).append("|")
                        .append(newCourse.getDescription()).append("|")
                        .append(formattedTime).append("|")
                        .append(formattedDate).append("|")
                        .append(newCourse.getLocation()).append("|")
                        .append(Arrays.toString(newCourse.getClassDayList())).append("|")
                        .append(newCourse.getUrlID()).append("|")
                        .append(newCourse.isRegistered()).append(";");// Remove commas from days list
            }

            editor.putString(KEY_COURSELIST, serializedCourses.toString());
            editor.apply();

            sortCourses();

            if (list_view_home.getAdapter() instanceof HomePageAdaptor) {
                HomePageAdaptor adaptor = (HomePageAdaptor) list_view_home.getAdapter();
                adaptor.notifyDataSetChanged();
            } else {
                Log.e("HomeFragment", "Adapter is not of the expected type HomePageAdaptor");
            }
        }
    }

    private void startMinuteChangeCheck() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                int currentMinute = calendar.get(Calendar.MINUTE);

                if (currentMinute != lastCheckedMinute) {
                    lastCheckedMinute = currentMinute;
                    onMinuteChanged();  // Call your action here
                }
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    private void onMinuteChanged() {
        Log.d("List Creation", "New list created");
        createList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

//    private String getDaySuffix(int day) {
//        if (day >= 11 && day <= 13) {
//            return "th";
//        }
//        switch (day % 10) {
//            case 1:
//                return "st";
//            case 2:
//                return "nd";
//            case 3:
//                return "rd";
//            default:
//                return "th";
//        }
//    }

    private void sortCourses() {
        courses.sort((course1, course2) -> {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            try {
                String[] schduleTimeCourse1 = course1.getTime().split(" - ");
                String[] schduleTimeCourse2 = course2.getTime().split(" - ");
                String[] schduleDateCourse1 = course1.getDate().split(" - ");
                String[] schduleDateCourse2 = course2.getDate().split(" - ");
                Calendar[] localDatesCourse1 = {
                        Transformer.convertDateDisplayToObject(schduleDateCourse1[0]),
                        Transformer.convertDateDisplayToObject(schduleDateCourse1[1]),
                };
                Calendar[] localDatesCourse2 = {
                        Transformer.convertDateDisplayToObject(schduleDateCourse2[0]),
                        Transformer.convertDateDisplayToObject(schduleDateCourse2[1]),
                };

                Calendar currentDate = Calendar.getInstance();

                Calendar endTime1 = Calendar.getInstance();
                endTime1.setTime(timeFormat.parse(schduleTimeCourse1[1]));
                endTime1.set(Calendar.YEAR, localDatesCourse1[1].get(Calendar.YEAR));
                endTime1.set(Calendar.MONTH, localDatesCourse1[1].get(Calendar.MONTH));
                endTime1.set(Calendar.DAY_OF_MONTH, localDatesCourse1[1].get(Calendar.DAY_OF_MONTH));

                Calendar endTime2 = Calendar.getInstance();
                endTime2.setTime(timeFormat.parse(schduleTimeCourse2[1]));
                endTime2.set(Calendar.YEAR, localDatesCourse2[1].get(Calendar.YEAR));
                endTime2.set(Calendar.MONTH, localDatesCourse2[1].get(Calendar.MONTH));
                endTime2.set(Calendar.DAY_OF_MONTH, localDatesCourse2[1].get(Calendar.DAY_OF_MONTH));

                boolean isExpired1 = endTime1.getTime().before(currentDate.getTime());
                boolean isExpired2 = endTime2.getTime().before(currentDate.getTime());

                if (isExpired1 && !isExpired2) return 1;
                if (!isExpired1 && isExpired2) return -1;

                Calendar startTime1 = Calendar.getInstance();
                startTime1.setTime(timeFormat.parse(schduleTimeCourse1[0]));
                startTime1.set(Calendar.YEAR, localDatesCourse1[0].get(Calendar.YEAR));
                startTime1.set(Calendar.MONTH, localDatesCourse1[0].get(Calendar.MONTH));
                startTime1.set(Calendar.DAY_OF_MONTH, localDatesCourse1[0].get(Calendar.DAY_OF_MONTH));

                Calendar startTime2 = Calendar.getInstance();
                startTime2.setTime(timeFormat.parse(schduleTimeCourse2[0]));
                startTime2.set(Calendar.YEAR, localDatesCourse2[0].get(Calendar.YEAR));
                startTime2.set(Calendar.MONTH, localDatesCourse2[0].get(Calendar.MONTH));
                startTime2.set(Calendar.DAY_OF_MONTH, localDatesCourse2[0].get(Calendar.DAY_OF_MONTH));

                return startTime1.getTime().compareTo(startTime2.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        });
    }

    public boolean compareDate(String date) {
//        String cleanedInputDate = inputDate.replaceAll("(\\d+)(st|nd|rd|th)", "$1");
//        String cleanedEndDate = endDate.replaceAll("(\\d+)(st|nd|rd|th)", "$1");
        if (date.toLowerCase().contains("null"))
            return false;

        String[] dates = date.split(" - ");
//        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
//        dateFormat.setLenient(false);
//
//        try {
            // Parse the input date string into a LocalDate object
        Calendar startDate = Transformer.convertDateDisplayToObject(dates[0]);
        Calendar endDate = Transformer.convertDateDisplayToObject(dates[1]);

        // Get today's date
        Calendar todayDate = Calendar.getInstance();
        return todayDate.equals(startDate) || todayDate.equals(endDate)
                || (todayDate.after(startDate));// && todayDate.before(endDate)); // TEMP NOT CHECK FOR END DATE

//        } catch (DateTimeParseException e) {
//            System.err.println("Invalid date format: " + e.getMessage());
//        }
//        return false;
    }
}