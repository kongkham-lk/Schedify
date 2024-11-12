package com.example.schedify;

public class Task {
    private final String title;
    private final String time;
    private final String description;

    public Task(String title, String description, String time) {
        this.title = title;
        this.time = time;
        this.description = description;
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
}
