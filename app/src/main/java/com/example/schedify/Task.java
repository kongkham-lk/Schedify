package com.example.schedify;

public class Task {
    private final String title;
    private final String time;

    public Task(String title, String time) {
        this.title = title;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }
}
