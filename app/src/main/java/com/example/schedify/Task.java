package com.example.schedify;

public class Task {
    private final String title;
    private final String time;
    private final String description;
    private final String date;
    private final String location;

    public Task(String title, String description, String time, String date, String location) {
        this.title = title;
        this.time = time;
        this.description = description;
        this.date = date;
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }
}
