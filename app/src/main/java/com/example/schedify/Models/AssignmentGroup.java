package com.example.schedify.Models;

import java.util.List;

public class AssignmentGroup {
    private String date;
    private List<AssignmentDetail> assignments;

    public AssignmentGroup(String date, List<AssignmentDetail> assignments) {
        this.date = date;
        this.assignments = assignments;
    }

    public String getDate() {
        return date;
    }

    public List<AssignmentDetail> getAssignments() {
        return assignments;
    }
}

