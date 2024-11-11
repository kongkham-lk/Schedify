package com.example.schedify;

public class CourseModel {

    private String courseCode; //subject + courseNumber
    private String courseTitle;
    private boolean isRegistered;

    // under meetingTime
    private String startDate;
    private String endDate;
    private String startTime; //beginTime
    private String endTime;
    private String buildingCode;
    private int roomNumber;
    private boolean[] classDay; // Class day in a week


    public CourseModel(String courseCode, String courseTitle)
    {
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
    }

    public CourseModel(String courseCode, String courseTitle, boolean isRegistered, String startDate,
                       String endDate, String startTime, String endTime, String buildingCode,
                       int roomNumber, boolean[] classDay) {
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.isRegistered = isRegistered;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.buildingCode = buildingCode;
        this.roomNumber = roomNumber;
        this.classDay = classDay;
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

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getBuildingCode() {
        return buildingCode;
    }

    public void setBuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public boolean[] getClassDay() {
        return classDay;
    }

    public void setClassDay(boolean[] classDay) {
        this.classDay = classDay;
    }
}
