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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

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

        // get the latest task property from create task screen
        if (getActivity() != null && getActivity().getIntent() != null)
            updateTaskPropertyFromCreateTaskScreen();

        return view;
    }

    private void updateTaskPropertyFromCreateTaskScreen() {
        Intent intent = getActivity().getIntent();
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        String location = intent.getStringExtra("location");
        int index = intent.getIntExtra("index", -1);

        if ((title != null && description != null && date != null) && index != -1) {
            Task targetTask = numTasks.get(index);
            int pos = indexOf(targetTask);
            if (pos != -1) {
                Course targetCourse = courses.get(pos);
                updateTaskProperty(targetCourse, title, description, date, time, location);
                updateTaskProperty(targetTask, title, description, date, time, location);
                saveTaskList(numTasks);
            }
        }
    }

    private int indexOf(Task targetTask) {
        for (int i = 0; i < courses.size(); i++) {
            Course targetCourse = courses.get(i);
            if (isEqual(targetCourse, targetTask)) {
                return i;
            }
        }
        return  -1;
    }

    private boolean isEqual(Course targetCourse, Task targetTask) {
        return targetCourse.getTitle().equals(targetTask.getTitle())
                && targetCourse.getLocation().equals(targetTask.getLocation())
                && targetCourse.getDescription().equals(targetTask.getDescription())
                && targetCourse.getTime().equals(targetTask.getTime())
                && targetCourse.getDate().equals(targetTask.getDate());
    }

    private void updateTaskProperty(Task targetTask, String title, String description, String date,
                                    String time, String location) {
        targetTask.setTitle(title);
        targetTask.setDescription(description);
        targetTask.setDate(date);
        targetTask.setTime(time);
        targetTask.setLocation(location);
    }

    private void saveTaskList(List<Task> taskList) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        StringBuilder sb = new StringBuilder();
        for (Task task : numTasks)
            sb.append(task.getTitle()).append(",")
                    .append(task.getDescription()).append(",")
                    .append(task.getTime()).append(",")
                    .append(task.getDate()).append(",")
                    .append(task.getLocation()).append(",;");

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
                    String title = Transformer.replaceUnderscoreWithComma(taskDetails[0]);
                    String description = Transformer.replaceUnderscoreWithComma(taskDetails[1]);
                    String location = taskDetails[4];
                    String time = taskDetails[2];
                    String date = taskDetails[3];
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
                    String date = taskDetails[3];
                    boolean[] classDayList = {false, true};
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
                String[] taskDetails = task.split(",");
                if (taskDetails.length >= 6) {
                    String title = taskDetails[0];
                    String description = taskDetails[1];
                    String time = taskDetails[2];
                    String date = taskDetails[3];
                    String location = taskDetails[4];
                    int urlID = Integer.parseInt(taskDetails[6]);
                    boolean isRegistered = taskDetails.length > 7 ? Boolean.parseBoolean(taskDetails[7]) : false;

                    String[] stringArray = taskDetails[5].replace("[", "").replace("]", "").trim().split(" ");
                    boolean[] classDayList = new boolean[stringArray.length];
                    for (int i = 0; i < stringArray.length; i++) {
                            classDayList[i] = Boolean.parseBoolean(stringArray[i]);
                    }

                    LocalDate today = LocalDate.now();
                    int todayDayOfWeek = today.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday
                    todayDayOfWeek--; // Adjust to 0-based index (0=Monday, 6=Sunday)

                    boolean isToday = compareDate(date);
                    boolean exactDay = false;

                    if (isToday && classDayList[todayDayOfWeek])
                        exactDay = true;  // Set exactDay to true if today is a class day

                    if (isToday && exactDay)
                        courses.add(new Course(title, description, time, date, location, classDayList, urlID, isRegistered));
                }
            }
        }
        sortCourses();

        courses.sort((course1, course2) -> {
            String[] schduleTimeCourse1 = course1.getTime().split(" - ");
            String[] schduleTimeCourse2 = course2.getTime().split(" - ");

            Calendar currentDate = Calendar.getInstance();

            Calendar endTime1 = Calendar.getInstance();
            endTime1.setTime(Transformer.convertTimeRawToObject(schduleTimeCourse1[1]).getTime());
            Calendar endTime2 = Calendar.getInstance();
            endTime2.setTime(Transformer.convertTimeRawToObject(schduleTimeCourse2[1]).getTime());

            course1.setExpired(endTime1.getTime().before(currentDate.getTime()));
            course2.setExpired(endTime2.getTime().before(currentDate.getTime()));

            if (course1.isExpired() && !course2.isExpired()) return 1;
            if (!course1.isExpired() && course2.isExpired()) return -1;

            Calendar startTime1 = Calendar.getInstance();
            startTime1.setTime(Transformer.convertTimeRawToObject(schduleTimeCourse1[0]).getTime());

            Calendar startTime2 = Calendar.getInstance();
            startTime2.setTime(Transformer.convertTimeRawToObject(schduleTimeCourse2[0]).getTime());

            return startTime1.getTime().compareTo(startTime2.getTime());
        });

        HomePageAdaptor homePageAdaptor = new HomePageAdaptor(requireContext(), R.layout.home_items_view_holder, courses, numTasks);
        list_view_home.setAdapter(homePageAdaptor);
    }

    public void filterOutCourseList(List<Course> newCourseList) {
        Log.d("HomeFragment", "Updating course list");

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        createList();

        if (newCourseList != null) {
            StringBuilder serializedCourses = new StringBuilder();

            for (Course newCourse : newCourseList) {
                String title = Transformer.replaceCommaWithUnderscore(newCourse.getTitle());
                String description = Transformer.replaceCommaWithUnderscore(newCourse.getDescription());
                String location = Transformer.replaceCommaWithUnderscore(newCourse.getLocation());
                String classDay = Transformer.replaceCommaWithUnderscore(Arrays.toString(newCourse.getClassDayList()));

                serializedCourses
                        .append(title).append(",")
                        .append(description).append(",")
                        .append(newCourse.getTime()).append(",")
                        .append(newCourse.getDate()).append(",")
                        .append(location).append(",")
                        .append(classDay).append(",")
                        .append(newCourse.getUrlID()).append(",")
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

    private void sortCourses() {
        courses.sort((course1, course2) -> {
            String[] schduleTimeCourse1 = course1.getTime().split(" - ");
            String[] schduleTimeCourse2 = course2.getTime().split(" - ");
            String[] schduleDateCourse1 = course1.getDate().split(" - ");
            String[] schduleDateCourse2 = course2.getDate().split(" - ");
            Calendar[] localDatesCourse1 = {
                    Transformer.convertDateRawToObject(schduleDateCourse1[0]),
                    Transformer.convertDateRawToObject(schduleDateCourse1[1]),
            };
            Calendar[] localDatesCourse2 = {
                    Transformer.convertDateRawToObject(schduleDateCourse2[0]),
                    Transformer.convertDateRawToObject(schduleDateCourse2[1]),
            };

            Calendar currentDate = Calendar.getInstance();

            Calendar endTime1 = Calendar.getInstance();
            endTime1.setTime(Transformer.convertTimeRawToObject(schduleTimeCourse1[1]).getTime());
            endTime1.set(Calendar.YEAR, localDatesCourse1[1].get(Calendar.YEAR));
            endTime1.set(Calendar.MONTH, localDatesCourse1[1].get(Calendar.MONTH));
            endTime1.set(Calendar.DAY_OF_MONTH, localDatesCourse1[1].get(Calendar.DAY_OF_MONTH));

            Calendar endTime2 = Calendar.getInstance();
            endTime2.setTime(Transformer.convertTimeRawToObject(schduleTimeCourse2[1]).getTime());
            endTime2.set(Calendar.YEAR, localDatesCourse2[1].get(Calendar.YEAR));
            endTime2.set(Calendar.MONTH, localDatesCourse2[1].get(Calendar.MONTH));
            endTime2.set(Calendar.DAY_OF_MONTH, localDatesCourse2[1].get(Calendar.DAY_OF_MONTH));

            boolean isExpired1 = endTime1.getTime().before(currentDate.getTime());
            boolean isExpired2 = endTime2.getTime().before(currentDate.getTime());

            if (isExpired1 && !isExpired2) return 1;
            if (!isExpired1 && isExpired2) return -1;

            Calendar startTime1 = Calendar.getInstance();
            startTime1.setTime(Transformer.convertTimeRawToObject(schduleTimeCourse1[0]).getTime());
            startTime1.set(Calendar.YEAR, localDatesCourse1[0].get(Calendar.YEAR));
            startTime1.set(Calendar.MONTH, localDatesCourse1[0].get(Calendar.MONTH));
            startTime1.set(Calendar.DAY_OF_MONTH, localDatesCourse1[0].get(Calendar.DAY_OF_MONTH));

            Calendar startTime2 = Calendar.getInstance();
            startTime2.setTime(Transformer.convertTimeRawToObject(schduleTimeCourse2[0]).getTime());
            startTime2.set(Calendar.YEAR, localDatesCourse2[0].get(Calendar.YEAR));
            startTime2.set(Calendar.MONTH, localDatesCourse2[0].get(Calendar.MONTH));
            startTime2.set(Calendar.DAY_OF_MONTH, localDatesCourse2[0].get(Calendar.DAY_OF_MONTH));

            return startTime1.getTime().compareTo(startTime2.getTime());
        });
    }

    public boolean compareDate(String date) {
        if (date.toLowerCase().contains("null"))
            return false;

        String[] dates = date.split(" - ");
        Calendar startDate = Transformer.convertDateRawToObject(dates[0]);
        Calendar endDate = Transformer.convertDateRawToObject(dates[1]);

        // Get today's date
        Calendar todayDate = Calendar.getInstance();
        return todayDate.equals(startDate) || todayDate.equals(endDate)
                || todayDate.after(startDate);// && todayDate.before(endDate); // for dev purpose
    }
}