package com.example.schedify;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;



import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView textView;

    public HomeFragment() {
        // Required empty public constructor
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
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private ListView list_view_home;
    private HomePageAdaptor homePageAdaptor;
    private ArrayList<CourseModel> courses;
    Button syncBtn;

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Initialize the ListView
        list_view_home = view.findViewById(R.id.list_view_home);

        // Initialize course data (Replace with your data source)
        courses = new ArrayList<>();

        syncBtn = view.findViewById(R.id.sync_btn);

        syncBtn.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onSyncButtonClicked();
            }
        });

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("TaskData", Context.MODE_PRIVATE);
        String taskData = sharedPreferences.getString("taskList", "");
        String courseData = sharedPreferences.getString("taskList2", "");

        if (!taskData.isEmpty()) {
            String[] tasks = taskData.split(";");
            for (String task : tasks) {
                String[] taskDetails = task.split(",");
                Log.d(taskDetails.length + "", Arrays.toString(taskDetails));
                if (taskDetails.length == 5) {
                    String title = taskDetails[0];
                    String description = taskDetails[1];
                    String location = taskDetails[4];
                    String time = taskDetails[2];
                    String[] times = time.split("-");
                    String date = taskDetails[3];
                    String[] dates = date.split("-");
                    dates[0] = dates[0].trim();
                    dates[1] = dates[1].trim();
                    boolean[] classDayList = {false, true};
                    boolean isToday = compareDate(dates[0], dates[1]);
                    if (isToday) {
                        courses.add(new CourseModel(R.drawable.gradient_color_2, title, location, times[0], times[1], description, true, dates[0], dates[1], "2560", classDayList, R.drawable.gradient_color_2));
                    }
                } else if (taskDetails.length == 4) {
                    String title = taskDetails[0];
                    String description = taskDetails[1];
                    String time = taskDetails[2];
                    String[] times = time.split("-");
                    String date = taskDetails[3];
                    String[] dates = date.split("-");
                    boolean[] classDayList = {false, true};
                    dates[0] = dates[0].trim();
                    dates[1] = dates[1].trim();
                    boolean isToday = compareDate(dates[0], dates[1]);
                    if (isToday) {
                        courses.add(new CourseModel(R.drawable.gradient_color_2, title, "", times[0], times[1], description, true, dates[0], dates[1], "2560", classDayList, R.drawable.gradient_color_2));
                    }
                } else if (taskDetails.length == 7) {
                    String title = taskDetails[1];
                    String description = taskDetails[2];
                    String location = taskDetails[5];
                    String time = taskDetails[3];
                    String[] times = time.split("-");
                    String date = taskDetails[4];
                    String[] dates = date.split("-");
                    dates[0] = dates[0].trim();
                    dates[1] = dates[1].trim();
                    Log.d("Class days", taskDetails[6]);

                    // Parse the class days
                    String[] stringArray = taskDetails[6].replace("[", "").replace("]", "").trim().split(" ");
                    boolean[] classDayList = new boolean[stringArray.length];
                    for (int i = 0; i < stringArray.length; i++) {
                        classDayList[i] = Boolean.parseBoolean(stringArray[i]);
                    }
                    Log.d("Class days", Arrays.toString(classDayList));

                    // Get today's day of the week (0=Monday, 1=Tuesday, ..., 6=Sunday)
                    LocalDate today = LocalDate.now();
                    int todayDayOfWeek = today.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday
                    todayDayOfWeek--; // Adjust to 0-based index (0=Monday, 6=Sunday)

                    boolean isToday = compareDate(dates[0], dates[1]);
                    boolean exactDay = false;

                    // Check if today is a class day and it's set to true in the classDayList
                    if (isToday && classDayList[todayDayOfWeek]) {
                        exactDay = true;  // Set exactDay to true if today is a class day
                    }

                    // If today is a class day and it matches the date range, add the course
                    if (isToday && exactDay) {
                        courses.add(new CourseModel(R.drawable.gradient_color_2, title, location, times[0], times[1], description, true, dates[0], dates[1], "2560", classDayList, R.drawable.gradient_color_2));
                    }
                }

            }
        }

        if (!courseData.isEmpty()) {
            String[] tasks = courseData.split(";");
            for (String task : tasks) {
                String[] taskDetails = task.split(",");
                Log.d(taskDetails.length + "", Arrays.toString(taskDetails));
                if (taskDetails.length == 7) {
                    String title = taskDetails[1];
                    String description = taskDetails[2];
                    String location = taskDetails[5];
                    String time = taskDetails[3];
                    String[] times = time.split("-");
                    String date = taskDetails[4];
                    String[] dates = date.split("-");
                    dates[0] = dates[0].trim();
                    dates[1] = dates[1].trim();
                    Log.d("Class days", taskDetails[6]);

                    // Parse the class days
                    String[] stringArray = taskDetails[6].replace("[", "").replace("]", "").trim().split(" ");
                    boolean[] classDayList = new boolean[stringArray.length];
                    for (int i = 0; i < stringArray.length; i++) {
                        classDayList[i] = Boolean.parseBoolean(stringArray[i]);
                    }
                    Log.d("Class days", Arrays.toString(classDayList));

                    // Get today's day of the week (0=Monday, 1=Tuesday, ..., 6=Sunday)
                    LocalDate today = LocalDate.now();
                    int todayDayOfWeek = today.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday
                    todayDayOfWeek--; // Adjust to 0-based index (0=Monday, 6=Sunday)

                    boolean isToday = compareDate(dates[0], dates[1]);
                    boolean exactDay = false;

                    // Check if today is a class day and it's set to true in the classDayList
                    if (isToday && classDayList[todayDayOfWeek]) {
                        exactDay = true;  // Set exactDay to true if today is a class day
                    }

                    // If today is a class day and it matches the date range, add the course
                    if (isToday && exactDay) {
                        courses.add(new CourseModel(R.drawable.gradient_color_2, title, location, times[0], times[1], description, true, dates[0], dates[1], "2560", classDayList, R.drawable.gradient_color_2));
                    }
                }

            }
        }



        // Sort courses by the start time
        courses.sort((course1, course2) -> {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            try {
                Calendar currentDate = Calendar.getInstance();

                // Parse end times
                Calendar endTime1 = Calendar.getInstance();
                endTime1.setTime(timeFormat.parse(course1.getEndTime()));
                Calendar endTime2 = Calendar.getInstance();
                endTime2.setTime(timeFormat.parse(course2.getEndTime()));

                // Mark expired courses
                course1.setExpired(endTime1.getTime().before(currentDate.getTime()));
                course2.setExpired(endTime2.getTime().before(currentDate.getTime()));

                // Sorting logic
                if (course1.isExpired() && !course2.isExpired()) return 1;
                if (!course1.isExpired() && course2.isExpired()) return -1;

                // Compare start times for non-expired courses
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



        // Set up the adapter
        HomePageAdaptor homePageAdaptor = new HomePageAdaptor(requireContext(), R.layout.home_items, courses);
        list_view_home.setAdapter(homePageAdaptor);

        return view;
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

    public void updateCourseList(List<CourseModel> newCourseList) {
        Log.d("HomeFragment", "Updating course list");

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("TaskData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

// Retrieve the current task data

        if (newCourseList != null) {
            StringBuilder serializedCourses = new StringBuilder();

            // Formatter for the desired output date format
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMM d"); // Desired format: "Nov 1"


            for (CourseModel newCourse : newCourseList) {
                // Format course start and end times
                String formattedStartTime = formatTimeToAMPM(newCourse.getStartTime());
                String formattedEndTime = formatTimeToAMPM(newCourse.getEndTime());
                newCourse.setStartTime(formattedStartTime);
                newCourse.setEndTime(formattedEndTime);

                String formattedStartDate = "";
                String formattedEndDate = "";

                try {
                    // Parse input dates and reformat
                    LocalDate startDate = LocalDate.parse(newCourse.getStartDate(), inputFormatter);
                    LocalDate endDate = LocalDate.parse(newCourse.getEndDate(), inputFormatter);

                    formattedStartDate = startDate.format(outputFormatter) + getDaySuffix(startDate.getDayOfMonth());
                    formattedEndDate = endDate.format(outputFormatter) + getDaySuffix(endDate.getDayOfMonth());
                } catch (Exception e) {
                    Log.e("HomeFragment", "Error parsing date: " + e.getMessage());
                }

                // Remove or replace commas in course details
                String courseCode = newCourse.getCourseCode().replace(",", "");
                String title = newCourse.getCourseCode().replace(",", "");
                String description = newCourse.getCourseTitle().replace(",", "");
                String startTime = formattedStartTime.replace(",", "");
                String endTime = formattedEndTime.replace(",", "");

                // Serialize the course details into a single string
                serializedCourses.append(courseCode).append(",")
                        .append(title).append(",")
                        .append(description).append(",")
                        .append(startTime).append(" - ")
                        .append(endTime).append(",")
                        .append(formattedStartDate).append(" - ")
                        .append(formattedEndDate).append(",")
                        .append(newCourse.getRoomNumber()).append(",")
                        .append(Arrays.toString(newCourse.getClassDayList()).replace(",", "")).append(";");// Remove commas from days list
            }

            // Save all serialized courses to SharedPreferences
            editor.putString("taskList2", serializedCourses.toString());
            editor.apply();

            // Sort courses after adding new ones
            sortCourses();

            // Notify the adapter about the data changes
            if (list_view_home.getAdapter() instanceof HomePageAdaptor) {
                HomePageAdaptor adaptor = (HomePageAdaptor) list_view_home.getAdapter();
                adaptor.notifyDataSetChanged();
            } else {
                Log.e("HomeFragment", "Adapter is not of the expected type HomePageAdaptor");
            }
        }
    }

    // Helper method to get the suffix for the day
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
        courses.sort((course1, course2) -> {
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

            // Get start and end dates
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