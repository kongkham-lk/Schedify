package com.example.schedify;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CourseModel {

    private String courseCode; //subject + courseNumber
    private String courseTitle;
    private boolean isRegistered;
    private String tv_location;
    public int course_img;
    private boolean isExpired;

    // Existing fields and methods...

    // under meetingTime
    private String startDate;
    private String endDate;
    private String startTime; //beginTime
    private String endTime;
    private String roomNumber;
    private boolean[] classDayList; // Class day in a week, start from monday
    private int urlID;


    public CourseModel(int course_img, String courseCode, String tv_location, String startTime, String endTime, String courseTitle, boolean isRegistered, String startDate,
                       String endDate,
                       String roomNumber, boolean[] classDayList, int urlID) {
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.isRegistered = isRegistered;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomNumber = roomNumber;
        this.classDayList = classDayList;
        this.urlID = urlID;
        this.course_img = course_img;
        this.tv_location = tv_location;
    }

    public int getCourse_img()
    {
        return course_img;
    }

    public String getTv_location() {
        return tv_location;
    }


    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public boolean isExpire() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public boolean[] getClassDayList() {
        return classDayList;
    }

    public boolean isExpired() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        Calendar currentDate = Calendar.getInstance();

        try {
            // Parse end time and associate it with today's date
            Calendar endTime = Calendar.getInstance();
            endTime.setTime(timeFormat.parse(this.getStartTime()));
            endTime.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));
            endTime.set(Calendar.MONTH, currentDate.get(Calendar.MONTH));
            endTime.set(Calendar.DAY_OF_MONTH, currentDate.get(Calendar.DAY_OF_MONTH));

            // Check if end time is before the current time
            return endTime.getTime().before(currentDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void setClassDayList(boolean[] classDayList) {
        this.classDayList = classDayList;
    }

    public int getUrlID() {
        return urlID;
    }

    public void setUrlID(int urlID) {
        this.urlID = urlID;
    }
}
