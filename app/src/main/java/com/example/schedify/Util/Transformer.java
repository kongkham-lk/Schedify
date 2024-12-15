package com.example.schedify.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Transformer {

    private static final SimpleDateFormat dateDisplayFormatter = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH);
    private static final SimpleDateFormat dateRawFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
    private static final SimpleDateFormat timeDisplayFormatter = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
    private static final SimpleDateFormat timeRawFormatter = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

    public static String convertDateRawToStringDateDisplay(String dateRaw) {
        String resultDate;
        if (Checker.isNull(dateRaw))
            resultDate = convertObjectToStringDateDisplay(Calendar.getInstance().getTime());
        else {
            Calendar tempDate = convertDateRawToObject(dateRaw);
            resultDate = convertObjectToStringDateDisplay(tempDate.getTime());
        }
        return resultDate;
    }

    public static Calendar convertDateRawToObject(String dateRaw) {
        Calendar resultDate = Calendar.getInstance();
        if (!Checker.isNull(dateRaw)) {
            try {
                Date tempDate = dateRawFormatter.parse(dateRaw);
                resultDate.setTime(tempDate);
            } catch (ParseException e) {
                System.err.println("Invalid date format: " + e.getMessage());
            }
        }
        return resultDate;
    }

    public static Calendar convertDateDisplayToObject(String dateDisplay) {
        Calendar resultDate = Calendar.getInstance();
        if (!Checker.isNull(dateDisplay)) {
            try {
                Date tempDate = dateDisplayFormatter.parse(dateDisplay);
                resultDate.setTime(tempDate);
            } catch (ParseException e) {
                System.err.println("Invalid date format: " + e.getMessage());
            }
        }
        return resultDate;
    }

    public static String convertObjectToStringDateDisplay(Date date) {
        String resultDate;
        if (date == null)
            resultDate = dateDisplayFormatter.format(Calendar.getInstance().getTime());
        else
            resultDate = dateDisplayFormatter.format(date);
        return resultDate;
    }

    public static String convertObjectToStringDateRaw(Date date) {
        String resultDate;
        if (date == null)
            resultDate = dateRawFormatter.format(Calendar.getInstance().getTime());
        else
            resultDate = dateRawFormatter.format(date);
        return resultDate;
    }

    public static String convertObjectToStringTimeDisplay(Date time) {
        String resultTime;
        if (time == null)
            resultTime = timeDisplayFormatter.format(Calendar.getInstance().getTime());
        else
            resultTime = timeDisplayFormatter.format(time);
        return resultTime;
    }

    public static String convertObjectTimeToStringTimeRaw(Date time) {
        String resultTime;
        if (time != null)
            resultTime = timeRawFormatter.format(time);
        else
            resultTime = timeRawFormatter.format(Calendar.getInstance().getTime());
        return resultTime;
    }

    public static String convertTimeDisplayToStringTimeRaw(String timeDisplay) {
        String resultTime;
        if (!Checker.isNull(timeDisplay)) {
            try {
                Date tempTime = timeDisplayFormatter.parse(timeDisplay);
                resultTime = timeDisplayFormatter.format(tempTime);
            } catch (ParseException e) {
                System.err.println("Invalid date format: " + e.getMessage());
                resultTime = timeRawFormatter.format(Calendar.getInstance().getTime());
            }
        } else {
            resultTime = timeRawFormatter.format(Calendar.getInstance().getTime());
        }
        return resultTime;
    }

    public static LocalTime convertTimeDisplayToObjectTimeRaw(String timeDisplay) {
        LocalTime resultTime;
        if (!Checker.isNull(timeDisplay)) {
            String[] times = timeDisplay.split(":");
            resultTime = LocalTime.now();
            resultTime.withHour(Integer.parseInt(times[0]));
            resultTime.withMinute(Integer.parseInt(times[1]));
        } else
            resultTime = LocalTime.now();
        return resultTime;
    }

    public static int[] splitTimeToArray24hr(String timeDisplay) {
        String[] times = timeDisplay.split("[\\s:]+");
        int additionalHour = times[2].equalsIgnoreCase("pm") ? 12 : 0;
        int hour = Integer.parseInt(times[0]) + additionalHour;
        int minute = Integer.parseInt(times[1]);
        return new int[] {hour, minute};
    }

    // Helper method to determine the suffix for a day
//    public static String getDaySuffix(int day) {
//        if (day >= 11 && day <= 13) {
//            return "th"; // Special case for 11th, 12th, 13th
//        }
//        switch (day % 10) {
//            case 1: return "st";
//            case 2: return "nd";
//            case 3: return "rd";
//            default: return "th";
//        }
//    }
}
