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

import com.example.schedify.Models.Course;
import com.example.schedify.Adaptors.HomePageAdaptor;
import com.example.schedify.R;
import com.example.schedify.Models.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private ArrayList<Course> displayItemList; // consist of course and task
    private ArrayList<Task> displayTaskItemList;
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
        displayItemList = new ArrayList<>();
        displayTaskItemList = new ArrayList<>();

        syncBtn.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onSyncButtonClicked();
            }
        });
        createDisplayListOnScreen();

        if (getActivity() != null && getActivity().getIntent() != null) {
            Intent intent = getActivity().getIntent();
            String title = intent.getStringExtra("title");
            String description = intent.getStringExtra("description");
            String date = intent.getStringExtra("date");
            String time = intent.getStringExtra("time");
            String location = intent.getStringExtra("location");
            int index = intent.getIntExtra("index", -1);

            if ((title != null && description != null && date != null) && index != -1) {
                Task task = displayTaskItemList.get(index);
                int pos = -1;
                for (int i = 0; i < displayItemList.size(); i++) {
                    Course targetItem = displayItemList.get(i);
                    String tempStartTime = targetItem.getStartTime();
                    String tempEndTime = targetItem.getEndTime();
                    String tempStartDate = targetItem.getStartDate();
                    String tempEndDate = targetItem.getEndDate();
                    String tempTime = tempStartTime + " - " + tempEndTime;
                    String tempDate = tempStartDate + " - " + tempEndDate;
                    if (targetItem.getTitle().equals(task.getTitle()) && targetItem.getLocation().equals(task.getLocation()) &&
                            targetItem.getDescription().equals(task.getDescription()) && tempTime.equals(task.getTime()) && tempDate.equals(task.getDate())) {
                        pos = i;
                        break;
                    }
                }
                if (pos != -1) {
                    String[] newTime = time.split(" - ");
                    newTime[0] = newTime[0].trim();
                    newTime[1] = newTime[1].trim();
                    displayTaskItemList.get(index).setTitle(title);
                    displayTaskItemList.get(index).setDescription(description);
                    displayTaskItemList.get(index).setDate(date);
                    displayTaskItemList.get(index).setTime(time);
                    displayTaskItemList.get(index).setLocation(location);
                    displayItemList.get(pos).setTitle(title);
                    displayItemList.get(pos).setLocation(location);
                    displayItemList.get(pos).setStartTime(newTime[0]);
                    displayItemList.get(pos).setEndTime(newTime[1]);
                    saveTaskList(displayTaskItemList);
                }
            }
        }
        return view;
    }

    private void saveTaskList(List<Task> taskList) {
//        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        StringBuilder sb = new StringBuilder();
        for (Task task : displayTaskItemList)
            sb.append(task.getTitle()).append(",").append(task.getDescription()).append(",").append(task.getTime()).append(",").append(task.getDate()).append(",").append(task.getLocation()).append(",;");

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
                String[] taskDetails = task.split(",");
                Log.d(taskDetails.length + "", Arrays.toString(taskDetails));
                if (taskDetails.length == 5) { // Task data - full detail
                    String title = taskDetails[0];
                    String description = taskDetails[1];
                    String location = taskDetails[4];
                    String time = taskDetails[2];
                    String[] times = time.split(" - ");
                    String date = taskDetails[3];
                    String[] dates = date.split(" - ");
                    dates[0] = dates[0].trim();
                    dates[1] = dates[1].trim();
                    boolean[] classDayList = {false, true};
                    boolean isToday = compareDate(dates[0], dates[1]);
                    if (isToday) {
                        numTaskModels.add(new TaskModel(title, description, time, date, location));
                        courses.add(new CourseModel(title, location, times[0], times[1], dates[0], dates[1], classDayList, 0,true, description));
                    }
                } else if (taskDetails.length == 4) { // Task data - without location
                    String title = taskDetails[0];
                    String description = taskDetails[1];
                    String time = taskDetails[2];
                    String[] times = time.split(" - ");
                    String date = taskDetails[3];
                    String[] dates = date.split(" - ");
                    boolean[] classDayList = {false, true};
                    dates[0] = dates[0].trim();
                    dates[1] = dates[1].trim();
                    boolean isToday = compareDate(dates[0], dates[1]);
                    if (isToday) {
                        numTaskModels.add(new TaskModel(title, description, time, date, ""));
                        courses.add(new CourseModel(title, "", times[0], times[1], dates[0], dates[1], classDayList, 0, true, description));
                    }
                }
            }
        }

        if (!courseData.isEmpty()) {
            String[] tasks = courseData.split(";");
            for (String task : tasks) {
                String[] taskDetails = task.split(",");
//                Log.d(taskDetails.length + "", Arrays.toString(taskDetails));
                if (taskDetails.length >= 6) {
                    String title = taskDetails[0];
                    String description = taskDetails[1];
                    String time = taskDetails[2];
                    String[] times = time.split(" - ");
                    String date = taskDetails[3];
                    String[] dates = date.split(" - ");
                    String location = taskDetails[4];
                    dates[0] = dates[0].trim();
                    dates[1] = dates[1].trim();
                    int urlID = Integer.parseInt(taskDetails[6]);
//                    Log.d("Class days", taskDetails[5]);

                    String[] stringArray = taskDetails[5].replace("[", "").replace("]", "").trim().split(" ");
                    boolean[] classDayList = new boolean[stringArray.length];
                    for (int i = 0; i < stringArray.length; i++) {
                        classDayList[i] = Boolean.parseBoolean(stringArray[i]);
                    }
//                    Log.d("Class days", Arrays.toString(classDayList));

                    LocalDate today = LocalDate.now();
                    int todayDayOfWeek = today.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday
                    todayDayOfWeek--; // Adjust to 0-based index (0=Monday, 6=Sunday)

                    boolean isToday = compareDate(dates[0], dates[1]);
                    boolean exactDay = false;

                    if (isToday && classDayList[todayDayOfWeek]) {
                        exactDay = true;  // Set exactDay to true if today is a class day
                    }

                    if (isToday && exactDay) {
                        courses.add(new CourseModel(title, location, times[0], times[1], dates[0], dates[1], classDayList, urlID, true, ""));
                    }
                }
            }
        }
        sortCourses();

        courses.sort((course1, course2) -> {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            try {
                Calendar currentDate = Calendar.getInstance();

                Calendar endTime1 = Calendar.getInstance();
                endTime1.setTime(timeFormat.parse(course1.getEndTime()));
                Calendar endTime2 = Calendar.getInstance();
                endTime2.setTime(timeFormat.parse(course2.getEndTime()));

                course1.setExpired(endTime1.getTime().before(currentDate.getTime()));
                course2.setExpired(endTime2.getTime().before(currentDate.getTime()));

                if (course1.isExpired() && !course2.isExpired()) return 1;
                if (!course1.isExpired() && course2.isExpired()) return -1;

                Calendar startTime1 = Calendar.getInstance();
                startTime1.setTime(timeFormat.parse(course1.getStartTime()));

                Calendar startTime2 = Calendar.getInstance();
                startTime2.setTime(timeFormat.parse(course2.getStartTime()));

                return startTime1.getTime().compareTo(startTime2.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        });

        HomePageAdaptor homePageAdaptor = new HomePageAdaptor(requireContext(), R.layout.home_items_view_holder, courses, numTaskModels);
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

        createDisplayListOnScreen();

        if (newCourseList != null) {
            StringBuilder serializedCourses = new StringBuilder();

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMM d"); // Desired format: "Nov 1"

            for (Course newCourse : newCourseList) {
                String formattedStartTime = formatTimeToAMPM(newCourse.getStartTime());
                String formattedEndTime = formatTimeToAMPM(newCourse.getEndTime());
                newCourse.setStartTime(formattedStartTime);
                newCourse.setEndTime(formattedEndTime);

                String formattedStartDate = "";
                String formattedEndDate = "";

                try {
                    LocalDate startDate = LocalDate.parse(newCourse.getStartDate(), inputFormatter);
                    LocalDate endDate = LocalDate.parse(newCourse.getEndDate(), inputFormatter);

                    formattedStartDate = startDate.format(outputFormatter) + getDaySuffix(startDate.getDayOfMonth());
                    formattedEndDate = endDate.format(outputFormatter) + getDaySuffix(endDate.getDayOfMonth());
                } catch (Exception e) {
                    Log.e("HomeFragment", "Error parsing date: " + e.getMessage());
                }

//                String courseCode = newCourse.getTitle().replace(",", "");
                String title = newCourse.getTitle().replace(",", "");
                String description = newCourse.getTitle().replace(",", "");
                String startTime = formattedStartTime.replace(",", "");
                String endTime = formattedEndTime.replace(",", "");

                serializedCourses
                        .append(title).append(",")
                        .append(description).append(",")
                        .append(startTime).append(" - ")
                        .append(endTime).append(",")
                        .append(formattedStartDate).append(" - ")
                        .append(formattedEndDate).append(",")
                        .append(newCourse.getLocation()).append(",")
                        .append(Arrays.toString(newCourse.getClassDayList()).replace(",", "")).append(",")
                        .append(newCourse.getUrlID()).append(";");// Remove commas from days list
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
        createDisplayListOnScreen();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    private String getDaySuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }



    private void sortCourses() {
        displayItemList.sort((course1, course2) -> {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            try {
                Calendar currentDate = Calendar.getInstance();

                Calendar endTime1 = Calendar.getInstance();
                endTime1.setTime(timeFormat.parse(course1.getEndTime()));
                endTime1.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));
                endTime1.set(Calendar.MONTH, currentDate.get(Calendar.MONTH));
                endTime1.set(Calendar.DAY_OF_MONTH, currentDate.get(Calendar.DAY_OF_MONTH));

                Calendar endTime2 = Calendar.getInstance();
                endTime2.setTime(timeFormat.parse(course2.getEndTime()));
                endTime2.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));
                endTime2.set(Calendar.MONTH, currentDate.get(Calendar.MONTH));
                endTime2.set(Calendar.DAY_OF_MONTH, currentDate.get(Calendar.DAY_OF_MONTH));

                boolean isExpired1 = endTime1.getTime().before(currentDate.getTime());
                boolean isExpired2 = endTime2.getTime().before(currentDate.getTime());

                if (isExpired1 && !isExpired2) return 1;
                if (!isExpired1 && isExpired2) return -1;

                Calendar startTime1 = Calendar.getInstance();
                startTime1.setTime(timeFormat.parse(course1.getStartTime()));
                startTime1.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));
                startTime1.set(Calendar.MONTH, currentDate.get(Calendar.MONTH));
                startTime1.set(Calendar.DAY_OF_MONTH, currentDate.get(Calendar.DAY_OF_MONTH));

                Calendar startTime2 = Calendar.getInstance();
                startTime2.setTime(timeFormat.parse(course2.getStartTime()));
                startTime2.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));
                startTime2.set(Calendar.MONTH, currentDate.get(Calendar.MONTH));
                startTime2.set(Calendar.DAY_OF_MONTH, currentDate.get(Calendar.DAY_OF_MONTH));

                return startTime1.getTime().compareTo(startTime2.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        });
    }


    public boolean compareDate(String inputDate, String endDate) {
        String cleanedInputDate = inputDate.replaceAll("(\\d+)(st|nd|rd|th)", "$1");
        String cleanedEndDate = endDate.replaceAll("(\\d+)(st|nd|rd|th)", "$1");

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d", Locale.ENGLISH);
        dateFormat.setLenient(false);

        try {
            Date formattedInputDate = dateFormat.parse(cleanedInputDate);
            Date formattedEndDate = dateFormat.parse(cleanedEndDate);

            Calendar currentCal = Calendar.getInstance();
            currentCal.setTime(new Date());
            int currentDayOfYear = currentCal.get(Calendar.DAY_OF_YEAR);

            Calendar startCal = Calendar.getInstance();
            startCal.setTime(formattedInputDate);
            int startDayOfYear = startCal.get(Calendar.DAY_OF_YEAR);

            Calendar endCal = Calendar.getInstance();
            endCal.setTime(formattedEndDate);
            int endDayOfYear = endCal.get(Calendar.DAY_OF_YEAR);

            return (currentDayOfYear - 1)  >= startDayOfYear && (currentDayOfYear - 1) <= endDayOfYear;

        } catch (ParseException e) {
            System.out.println("Error: Invalid date format.");
        }
        return false;
    }
}