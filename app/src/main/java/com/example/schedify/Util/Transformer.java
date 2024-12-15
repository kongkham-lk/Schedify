package com.example.schedify.Util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;

public class Transformer {

    private static final DateTimeFormatter dateDisplayToStringFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
    private static final DateTimeFormatter dateRawToStringFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final SimpleDateFormat dateDisplayToObjectFormatter = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
    private static final SimpleDateFormat dateRawToObjectFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
    private static final SimpleDateFormat timeDisplayToStringFormatter = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);

    public static String transformStartEndDate(String startEndDate) {
        String[] dates = startEndDate.split(" - ");
        String startDateString = transformDate(dates[0]);
        String endDateString = transformDate(dates[1]);
        return startDateString + " - " + endDateString;
    }

    public static String transformDate(String date) {
        if (date == null || date.equals(""))
            return "";

        LocalDate localDate = convertRawStringDateToLocalDate(date);
        String stringDate = convertLocalDateToStringDate(localDate);
        return stringDate;
    }

    public static LocalDate convertRawStringDateToLocalDate(String date) {
        try {
            return LocalDate.parse(date, dateRawToStringFormatter);
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date format: " + e.getMessage());
        }
        return LocalDate.now();
    }

    public static LocalDate convertDisplayStringDateToLocalDate(String date) {
        try {
            return LocalDate.parse(date, dateDisplayToStringFormatter);
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date format: " + e.getMessage());
        }
        return LocalDate.now();
    }

    public static String convertLocalDateToStringDate(LocalDate date) {
        if (date == null || date.equals(""))
            return "";
        return date.format(dateDisplayToStringFormatter);
    }

    public static String convertDateObjectToStringDateDisplay(Date date) {
        if (date == null)
            return "";
        return dateDisplayToObjectFormatter.format(date);
    }

    public static String convertDateObjectToStringDateRaw(Date date) {
        if (date == null)
            return "";
        return dateRawToObjectFormatter.format(date);
    }

    public static String convertDateObjectToStringTimeDisplay(Date time) {
        if (time == null)
            return "";
        return timeDisplayToStringFormatter.format(time);
    }

    public static String transformStartEndTime(String startEndTime) {
        String[] times = startEndTime.split(" - ");
        String startDateString = transformTime(times[0]);
        String endDateString = transformTime(times[1]);
        return startDateString + " - " + endDateString;
    }

    public static String transformTime(String time) {
        if (time == null || time.equals(""))
            return "";
        return timeDisplayToStringFormatter.format(time);
    }

    public static LocalDateTime convertStringTimeToLocalDateTimeRaw(String time) {
        if (!Checker.isNull(time)) {
            String[] times = time.split("[\\s:]+");
            int additionalHour = times[2].equalsIgnoreCase("pm") ? 12 : 0;
            int hour = Integer.parseInt(times[0]) + additionalHour;
            int minute = Integer.parseInt(times[1]);

            LocalDateTime now = LocalDateTime.now();
            now.withHour(hour);
            now.withMinute(minute);
            return now;
        }
        return LocalDateTime.now();
    }

    public static String convertStringTimeToStringTimeRaw(String time) {
        if (!Checker.isNull(time)) {
            String[] times = time.split("[\\s:]+");
            int additionalHour = times[2].equalsIgnoreCase("pm") ? 12 : 0;
            int hour = Integer.parseInt(times[0]) + additionalHour;
            int minute = Integer.parseInt(times[1]);
            return String.join(":", times);
        }
        LocalDateTime now = LocalDateTime.now();
        return String.valueOf(now.getHour()) + ":" + String.valueOf(now.getMinute());
    }

    // Helper method to determine the suffix for a day
    public static String getDaySuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th"; // Special case for 11th, 12th, 13th
        }
        switch (day % 10) {
            case 1: return "st";
            case 2: return "nd";
            case 3: return "rd";
            default: return "th";
        }
    }
}
