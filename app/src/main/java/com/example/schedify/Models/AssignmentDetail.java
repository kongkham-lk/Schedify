package com.example.schedify.Models;

public class AssignmentDetail {
    private String title;
    private String assignmentLink;
    private String description;
    private String dueTime;

    // Constructor
    public AssignmentDetail(String title, String assignmentLink, String description, String dueTime) {
        this.title = title;
        this.assignmentLink = assignmentLink;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }
}

