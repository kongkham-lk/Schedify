package com.example.schedify.Models;

import java.util.List;

public class AssignmentsByDateGroup {
    private String date;
    private List<AssignmentDetail> assignmentItemList;

    public AssignmentsByDateGroup(String date, List<AssignmentDetail> assignmentItemList) {
        this.date = date;
        this.assignmentItemList = assignmentItemList;
    }

    public String getDate() {
        return date;
    }

    public List<AssignmentDetail> getAssignmentItemList() {
        return assignmentItemList;
    }
}

