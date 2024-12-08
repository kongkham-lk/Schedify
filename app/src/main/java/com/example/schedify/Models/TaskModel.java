package com.example.schedify.Models;

public class TaskModel {
    private String title;
    private String time;
    private String description;
    private String date;
    private String location;

    public TaskModel(String title, String description, String time, String date, String location) {
        this.title = title;
        this.time = time;
        this.description = description;
        this.date = date;
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
