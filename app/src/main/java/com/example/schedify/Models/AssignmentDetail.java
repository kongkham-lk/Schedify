package com.example.schedify.Models;

public class AssignmentDetail {
    private String title;
    private String assignmentLink;
    private String courseDetails;
    private String dueTime;

    // Constructor
    public AssignmentDetail(String title, String assignmentLink, String courseDetails, String dueTime) {
        this.title = title;
        this.assignmentLink = assignmentLink;
        this.courseDetails = courseDetails;
        this.dueTime = dueTime;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAssignmentLink() {
        return assignmentLink;
    }

    public void setAssignmentLink(String assignmentLink) {
        this.assignmentLink = assignmentLink;
    }

    public String getCourseDetails() {
        return courseDetails;
    }

    public void setCourseDetails(String courseDetails) {
        this.courseDetails = courseDetails;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }
}

