package com.example.schedify.Models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Locale;

public class Course extends Task {

    private boolean isRegistered;
    private boolean isExpired;
    private boolean[] classDayList; // Class day in a week, start from monday
    private int urlID;


    public Course(String title, String description, String time, String date, String location, boolean[] classDayList,
                  int urlID, boolean isRegistered) {
        super(title, description, time, date, location);
        this.classDayList = classDayList;
        this.urlID = urlID;
        this.isRegistered = isRegistered;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }

    public boolean isExpire() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
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
            String[] scheduleTimes = this.getTime().split(" - ");
            endTime.setTime(timeFormat.parse(scheduleTimes[0]));
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
