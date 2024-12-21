package com.example.schedify.Util;

import com.example.schedify.Models.Course;

import java.util.Arrays;
import java.util.Calendar;

public class Checker {
    private static String[] invalidStringList = {"", "null"};

    public static boolean isContainNullElement (String[] arr) {
        if (arr == null)
            return true;

        for (String el : arr) {
            if (isNull(el))
                return true;
        }
        return false;
    }

    public static boolean isNull (String str) {
        if (str == null || Arrays.asList(invalidStringList).contains(str))
            return true;
        return false;
    }


    public static boolean isStartTimeExpired(Course course) {
        String[] scheduleTimes = course.getTime().split(" - ");
        return isExpired(scheduleTimes[0]);
    }

    public static boolean isEndTimeExpired(Course course) {
        String[] scheduleTimes = course.getTime().split(" - ");
        return isExpired(scheduleTimes[1]);
    }

    public static boolean isExpired(String targetTime) {
        Calendar currentDate = Calendar.getInstance();
        Calendar targetCal = getCurrentTimeDateCal(targetTime, currentDate);

        // Check if end time is before the current time
        return targetCal.getTime().before(currentDate.getTime());
    }

    private static Calendar getCurrentTimeDateCal(String targetTime, Calendar currentDate) {
        Calendar targetCal = Transformer.convertTimeRawToObject(targetTime);
        targetCal.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));
        targetCal.set(Calendar.MONTH, currentDate.get(Calendar.MONTH));
        targetCal.set(Calendar.DAY_OF_MONTH, currentDate.get(Calendar.DAY_OF_MONTH));

        return targetCal;
    }

    public static int isBefore(Course course1, Course course2) {
        String[] schduleTimeCourse1 = course1.getTime().split(" - ");
        String[] schduleTimeCourse2 = course2.getTime().split(" - ");
        String[] schduleDateCourse1 = course1.getDate().split(" - ");
        String[] schduleDateCourse2 = course2.getDate().split(" - ");
        Calendar startTime1 = getCurrentTimeDateCal(schduleTimeCourse1[0], Transformer.convertDateRawToObject(schduleDateCourse1[0]));
        Calendar startTime2 = getCurrentTimeDateCal(schduleTimeCourse2[0], Transformer.convertDateRawToObject(schduleDateCourse2[0]));

        return startTime1.getTime().compareTo(startTime2.getTime());
    }

    public static boolean isClassToday(String date) {
        if (date.toLowerCase().contains("null"))
            return false;

        String[] dates = date.split(" - ");
        Calendar startDate = Transformer.convertDateRawToObject(dates[0]);
        Calendar endDate = Transformer.convertDateRawToObject(dates[1]);

        // Get today's date
        Calendar todayDate = Calendar.getInstance();
        return todayDate.equals(startDate) || todayDate.equals(endDate) || todayDate.after(startDate);// && todayDate.before(endDate); // for dev purpose
    }
}
