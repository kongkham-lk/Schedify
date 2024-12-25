package com.example.schedify.Models;

public class Course extends Task {

    private boolean isRegistered;
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

    public boolean[] getClassDayList() {
        return classDayList;
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
