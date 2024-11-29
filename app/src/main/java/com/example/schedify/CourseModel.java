package com.example.schedify;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CourseModel {

    private String title;
    private boolean isRegistered;
    private String location;
    private boolean isExpired;

    // under meetingTime
    private String startDate;
    private String endDate;
    private String description;
    private String startTime; //beginTime
    private String endTime;
    private boolean[] classDayList; // Class day in a week, start from monday
    private int urlID;


    public CourseModel(String title, String location, String startTime, String endTime,
                       String startDate, String endDate, boolean[] classDayList, int urlID, boolean isRegistered, String description) {
        this.title = title;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.classDayList = classDayList;
        this.urlID = urlID;
        this.isRegistered = isRegistered;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getDescription() { return description; }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
