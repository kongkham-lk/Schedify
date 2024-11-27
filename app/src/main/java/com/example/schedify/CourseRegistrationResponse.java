package com.example.schedify;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CourseRegistrationResponse {

    private List<CourseModel> courseList;
    private static final String[] DayInAWeek = new String[]{"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
    private String requestMethod = "";

    public CourseRegistrationResponse() {
        courseList = new ArrayList<>();
    }

    // Callable task to fetch API data
    public List<CourseModel> fetchCourseSchedule(String apiUrl, String cookie) {
        APIFetcher moodleFetcher = new APIFetcher();
        moodleFetcher.setRequestMethod(requestMethod);
        String moodleResponse = moodleFetcher.getResponse(apiUrl, cookie); // fetch course schedule from API
        extractJSONResponse(moodleResponse); // Bind data and save to courseList
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
                    int courseNumber = courseItem.getInt("courseNumber");
                    String courseCode = subject + " " + courseNumber;
                    String courseTitle = courseItem.getString("courseTitle");
                    boolean isRegistered = courseItem.getString("statusDescription").toLowerCase().equals("registered") ? true : false;
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
                    CourseModel course = new CourseModel(R.drawable.gradient_color_3, courseCode, roomNumber, startTime, endTime,
                            courseTitle, isRegistered, startDate,
                            endDate,  room, classDayList, urlID);
                    courseList.add(course);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }
}
