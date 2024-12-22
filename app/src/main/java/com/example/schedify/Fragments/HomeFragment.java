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
import com.example.schedify.Util.Checker;
import com.example.schedify.Util.Transformer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {
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

    Handler handler = new Handler();
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
            int pos = indexOf(targetTask, courses);
            if (pos != -1) {
                Course targetCourse = courses.get(pos);
                updateTaskProperty(targetCourse, title, description, date, time, location);
                updateTaskProperty(targetTask, title, description, date, time, location);
                saveTaskList(numTasks);
            }
        }
    }

    private int indexOf(Task targetTask, ArrayList<Course> courses) {
        for (int i = 0; i < courses.size(); i++) {
            Course targetCourse = courses.get(i);
            if (isEqual(targetCourse, targetTask))
                return i;
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

    private void updateTaskProperty(Task targetTask, String title, String description, String date, String time, String location) {
        targetTask.setTitle(title);
        targetTask.setDescription(description);
        targetTask.setDate(date);
        targetTask.setTime(time);
        targetTask.setLocation(location);
    }

    private void saveTaskList(List<Task> taskList) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        StringBuilder serializedTask = getTskSharedPreference(numTasks);
        editor.putString(KEY_TASKLIST, serializedTask.toString());
        editor.apply();
    }

    private void createList() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppData", Context.MODE_PRIVATE);
        courses.clear();
        updateTaskListAndFilterOutInvalidDate(sharedPreferences, KEY_TASKLIST);
        updateTaskListAndFilterOutInvalidDate(sharedPreferences, KEY_COURSELIST);
        
        sortCourses();
        
        HomePageAdaptor homePageAdaptor = new HomePageAdaptor(requireContext(), R.layout.home_items_view_holder, displayItems, tasks);
        list_view_home.setAdapter(homePageAdaptor);
    }

    private void updateTaskListAndFilterOutInvalidDate(SharedPreferences sharedPreferences, String targetSharedPrefKey) {
        String taskData = sharedPreferences.getString(targetSharedPrefKey, "");
        if (!taskData.isEmpty()) {
            String[] tasks = taskData.split(";");
            for (String task : tasks) {
                // Retrieved all property from share preference
                String[] taskDetails = task.split(",");
                String title = Transformer.replaceUnderscoreWithComma(taskDetails[0]);
                String description = Transformer.replaceUnderscoreWithComma(taskDetails[1]);
                String time = taskDetails[2];
                String date = taskDetails[3];
                String location = taskDetails.length > 4 ? Transformer.replaceUnderscoreWithComma(taskDetails[4]) : "";
                boolean[] classDayList;
                int todayDayOfWeek = 0;
                String[] stringArray = taskDetails.length > 5
                        ? taskDetails[5].replace("[", "").replace("]", "").trim().split(" ")
                        : null;

                if (stringArray == null)
                    classDayList = new boolean[] { true };
                else {
                    classDayList = new boolean[stringArray.length];
                    for (int i = 0; i < stringArray.length; i++)
                        classDayList[i] = Boolean.parseBoolean(stringArray[i]) || title.contains("Data"); // for dev purpose

                    LocalDate today = LocalDate.now();
                    todayDayOfWeek = today.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday
                    todayDayOfWeek--; // Adjust to 0-based index (0=Monday, 6=Sunday)
                }
                int urlID = taskDetails.length > 6 ? Integer.parseInt(taskDetails[6]) : 0;
                boolean isRegistered = taskDetails.length > 7 && Boolean.parseBoolean(taskDetails[7]);

                // Filter out invalid-date's task
                boolean isTodayWithinValidDates = Checker.isDateExpired(date); // checking if today is in between the starting and endaring date
                boolean isTodayHasClass = classDayList[todayDayOfWeek]; // for specifically course which
                if (isTodayWithinValidDates && isTodayHasClass) {
                    if (targetSharedPrefKey.equals(KEY_TASKLIST))
                        numTasks.add(new Task(title, description, time, date, location));
                    courses.add(new Course(title, description, time, date, location, classDayList, urlID, isRegistered));
                }
            }
        }
    }

    public void filterOutCourseList(List<Course> newCourseList) {
        Log.d("HomeFragment", "Updating course list");

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("AppData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        createList();

        if (newCourseList != null) {
            StringBuilder serializedCourses = this.getCourseSharedPreference(newCourseList);
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

    private StringBuilder getTskSharedPreference(List<Task> numTasks) {
        StringBuilder serializedTask = new StringBuilder();
        for (Task task : numTasks) {
            String title = Transformer.replaceCommaWithUnderscore(task.getTitle());
            String description = Transformer.replaceCommaWithUnderscore(task.getDescription());
            String location = Transformer.replaceCommaWithUnderscore(task.getLocation());

            serializedTask.append(title).append(",")
                    .append(description).append(",")
                    .append(task.getTime()).append(",")
                    .append(task.getDate()).append(",")
                    .append(location).append(",;");
        }
        return serializedTask;
    }

    private StringBuilder getCourseSharedPreference(List<Course> newCourseList) {
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
        return serializedCourses;
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
        displayItems.sort((course1, course2) -> {
            boolean isExpired1 = Checker.isTimeExpired(course1);
            boolean isExpired2 = Checker.isTimeExpired(course2);
            if (isExpired1 && !isExpired2) return 1;
            if (!isExpired1 && isExpired2) return -1;

            return Checker.isBefore(course1, course2);
        });
    }
}