package com.example.schedify.ApiClients;

import android.content.Context;

import com.example.schedify.Models.Course;
import com.example.schedify.Util.SharePreference;
import com.example.schedify.Util.Transformer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CourseRegistrationResponse {

    private List<Course> courseList;
    private static final String[] DayInAWeek = new String[]{"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
//    private String requestMethod = "";
    private Context context;

    public CourseRegistrationResponse(Context context) {
        courseList = new ArrayList<>();
        this.context = context;
    }

//    public String getRequestMethod() {
//        return requestMethod;
//    }
//
//    public void setRequestMethod(String requestMethod) {
//        this.requestMethod = requestMethod;
//    }

    // Callable task to fetch API data
    public List<Course> retrievedCourseSchedule() {
        extractJSONResponse(); // Bind data and save to courseList
        return courseList;
    }

    // Parsing logic for the articles
    private void extractJSONResponse() {
        try {
//            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject jsonObject = SharePreference.loadJson(context);
            JSONArray registeredCourseList = jsonObject.getJSONObject("data").getJSONArray("registrations");

            for (int i = 0; i < registeredCourseList.length(); i++) {
                try {
                    JSONObject courseItem = registeredCourseList.getJSONObject(i);
                    String subject = courseItem.getString("subject");
                    int courseNumber = courseItem.getInt("courseNumber");
                    String courseCode = subject + " " + courseNumber;
                    String courseTitle = courseItem.getString("courseTitle");
                    String title = courseCode + ": " + courseTitle;
                    boolean isRegistered = courseItem.getString("statusDescription").toLowerCase().equals("registered") ? true : false;
                    JSONObject meetingDetail = courseItem.getJSONArray("meetingTimes").getJSONObject(0);
                    String startTime = meetingDetail.getString("beginTime");
                    startTime = Transformer.convertUnSplitToSplitStringTimeRow(startTime);
                    String endTime = meetingDetail.getString("endTime");
                    endTime = Transformer.convertUnSplitToSplitStringTimeRow(endTime);
                    String startDate = meetingDetail.getString("startDate");
                    String endDate = meetingDetail.getString("endDate");
                    String building = meetingDetail.getString("building");
                    String room = meetingDetail.getString("room");
                    String location = building + " " + room;
                    int numDays = DayInAWeek.length;
                    boolean[] classDayList = new boolean[numDays];
                    for (int day = 0; day < numDays; day++) {
                        String dayInAWeek = DayInAWeek[day];
                        boolean hasClass = meetingDetail.getBoolean(dayInAWeek);
                        classDayList[day] = hasClass;
                    }
                    int urlID = 0;
                    switch (courseNumber) {
                        case 2130:
                            urlID = 57100;
                            break;
                        case 2160:
                            urlID = 57049;
                            break;
                        case 2210:
                            urlID = 56936;
                            break;
                        case 2230:
                            urlID = 57127;
                            break;
                        case 2920:
                            urlID = 56165;
                            break;
                    }
                    Course course = new Course(title, "", startTime + " - " + endTime, startDate + " - " + endDate, location, classDayList, urlID, isRegistered);
                    courseList.add(course);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
