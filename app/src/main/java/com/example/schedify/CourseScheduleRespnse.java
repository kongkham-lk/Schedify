package com.example.schedify;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class CourseScheduleRespnse {

    private List<CourseModel> courseList;
    private static final String[] DayInAWeek = new String[]{"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};

    public CourseScheduleRespnse() {
        courseList = new ArrayList<>();
    }

    // Callable task to fetch API data
    public List<CourseModel> fetchCourseSchedule(String apiUrl) {
        APIFetcher courseFetcher = new APIFetcher();
        String courseResponse = courseFetcher.getResponse(apiUrl); // fetch course schedule from API
        extractJSONResponse(courseResponse); // Bind data and save to courseList
        return courseList;
    }

    // Parsing logic for the articles
    private void extractJSONResponse(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray registeredCourseList = jsonObject.getJSONObject("data").getJSONArray("registrations");

            for (int i = 0; i < registeredCourseList.length(); i++) {
                try {
                    JSONObject courseItem = registeredCourseList.getJSONObject(i);
                    String subject = courseItem.getString("subject");
                    String courseNumber = courseItem.getString("courseNumber");
                    String courseCode = subject + " " + courseNumber;
                    String courseTitle = courseItem.getString("courseTitle");
                    boolean isRegistered = courseItem.getString("statusDescription") == "Registered" ? true : false;
                    JSONObject meetingDetail = courseItem.getJSONArray("meetingTimes").getJSONObject(0);
                    String startTime = meetingDetail.getString("beginTime");
                    String endTime = meetingDetail.getString("endTime");
                    String startDate = meetingDetail.getString("startDate");
                    String endDate = meetingDetail.getString("endDate");
                    String building = meetingDetail.getString("building");
                    String room = meetingDetail.getString("room");
                    String roomNumber = building + " " + room;
                    int numDays = DayInAWeek.length;
                    boolean[] classDayList = new boolean[numDays];
                    for (int day = 0; day < numDays; day++)
                        classDayList[i] = meetingDetail.getBoolean(DayInAWeek[day]);
                    CourseModel course = new CourseModel(courseCode, courseTitle, isRegistered, startDate,
                            endDate, startTime, endTime, roomNumber, classDayList);
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
